package com.innovationandtrust.sftp.service;

import com.innovationandtrust.sftp.component.chain.execution.FileValidatingExecutionManager;
import com.innovationandtrust.sftp.component.chain.handler.FileErrorHandler;
import com.innovationandtrust.sftp.config.FileIntegrationProperty;
import com.innovationandtrust.sftp.constant.FileIntegrationConstant;
import com.innovationandtrust.sftp.constant.FileProcessingDirectory;
import com.innovationandtrust.sftp.constant.SftpProcessConstant;
import com.innovationandtrust.sftp.model.SftpFileRequest;
import com.innovationandtrust.sftp.restclient.ProfileFeignClient;
import com.innovationandtrust.share.model.profile.CorporateUser;
import com.innovationandtrust.share.model.profile.NormalUser;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.file.provider.FileProvider;
import com.innovationandtrust.utils.keycloak.provider.IKeycloakTokenExchange;
import java.nio.file.Path;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileDetectionService {

  private static final String TEMP_DIR = "temp";
  private static final String UUID_REGEX = "[a-f0-9]{8}(?:-[a-f0-9]{4}){4}[a-f0-9]{8}";
  private final FileProvider fileProvider;
  private final FileIntegrationProperty fileIntegrationProperty;
  private final FileValidatingExecutionManager validatingExecutionManager;
  private final ProfileFeignClient profileFeignClient;
  private final IKeycloakTokenExchange tokenExchange;
  private final FileErrorHandler fileErrorHandler;

  public static String getUuid(String filename, Integer index) {
    final var pattern = Pattern.compile(UUID_REGEX);
    Matcher matcher = pattern.matcher(filename);
    if (matcher.find()) {
      // The filename will contain the underscore
      var tmp = matcher.group(index);
      var uuids = tmp.split("_");
      return uuids[0];
    }
    return null;
  }

  @Async(value = FileIntegrationConstant.ASYNC_TASK_EXECUTOR_BEAN)
  public void receiveFile(Path path, Long timestamp) throws InterruptedException {
    Thread.sleep(TimeUnit.SECONDS.toMillis(this.fileIntegrationProperty.getDelayTime()));
    log.info("Receive file: {}", path);
    final var pattern = Pattern.compile(UUID_REGEX);
    Matcher matcher = pattern.matcher(path.toString());
    CorporateUser corporate;
    if (matcher.find()) {
      var filename = FilenameUtils.getName(String.valueOf(path));
      var corporateUuid = matcher.group(0);
      // Create in directories
      this.createInDirectories(corporateUuid);

      // create out directories
      this.createOutDirectories(corporateUuid);
      var userUuid = this.getUserUuid(filename);
      var token = String.format("Bearer %s", tokenExchange.getToken(corporateUuid));
      corporate = profileFeignClient.findCorporateByUuid(corporateUuid, userUuid, token);
      if (Objects.isNull(corporate)) {
        // create error.txt file
        this.fileErrorHandler.errorUserNotFound(
            SftpFileRequest.builder()
                .filePath(
                    String.valueOf(
                        Path.of(fileIntegrationProperty.getBasePathIn(), path.toString())))
                .corporateUser(new CorporateUser(corporateUuid))
                .filename(filename)
                .integrationProperty(fileIntegrationProperty)
                .build());
        // stop process
        return;
      }

      if (Objects.isNull(corporate.getNormalUser())) {
        corporate.setNormalUser(new NormalUser(userUuid));
      }

      var flowId = UUID.randomUUID().toString();
      var tmpPath =
          Path.of(TEMP_DIR, flowId, corporateUuid.concat("_").concat(String.valueOf(timestamp)));
      var sourcePath = Path.of(fileIntegrationProperty.getBasePathIn(), String.valueOf(path));
      var context = new ExecutionContext();
      context.put(
          SftpProcessConstant.SFTP_FILE_REQUEST,
          SftpFileRequest.builder()
              .flowId(flowId)
              .corporateUser(corporate)
              .filePath(String.valueOf(tmpPath.resolve(filename)))
              .filename(filename)
              .timestamp(timestamp)
              .integrationProperty(fileIntegrationProperty)
              .token(token)
              .build());
      Executors.newSingleThreadExecutor()
          .execute(
              () -> {
                // Move to temporary folder
                this.fileProvider.moveFile(sourcePath, tmpPath);

                // Copy file to receive folder of out directory
                this.copyReferenceFile(tmpPath, corporateUuid);

                // Chaining execution process
                this.validatingExecutionManager.execute(context);
              });
    }
  }

  private void copyReferenceFile(Path source, String corporateUuid) {
    var outPath =
        Path.of(
            fileIntegrationProperty.getBasePathOut(),
            corporateUuid,
            FileProcessingDirectory.OUT.getDirectory(),
            FileProcessingDirectory.RECEIVED.getDirectory());
    this.fileProvider.copyFile(source, outPath);
  }

  private String getUserUuid(String filename) {
    Matcher matcher = Pattern.compile(UUID_REGEX).matcher(filename);
    if (matcher.find()) {
      return matcher.group();
    }
    return "";
  }

  private void createInDirectories(String corporateUuid) {
    var baseInDir = Path.of(fileIntegrationProperty.getBasePathIn(), corporateUuid);
    Executors.newSingleThreadExecutor()
        .execute(
            () -> {
              this.fileProvider.createSubDirectory(
                  String.valueOf(
                      baseInDir.resolve(FileProcessingDirectory.PROCESSING.getDirectory())));
              this.fileProvider.createSubDirectory(
                  String.valueOf(baseInDir.resolve(FileProcessingDirectory.ERROR.getDirectory())));
              this.fileProvider.createSubDirectory(
                  String.valueOf(baseInDir.resolve(FileProcessingDirectory.DONE.getDirectory())));
            });
  }

  private void createOutDirectories(String corporateUuid) {
    var baseOutDir =
        Path.of(
            fileIntegrationProperty.getBasePathOut(),
            corporateUuid,
            FileProcessingDirectory.OUT.getDirectory());
    Executors.newSingleThreadExecutor()
        .execute(
            () -> {
              this.fileProvider.createSubDirectory(
                  String.valueOf(
                      baseOutDir.resolve(FileProcessingDirectory.PROCESSED.getDirectory())));
              this.fileProvider.createSubDirectory(
                  String.valueOf(baseOutDir.resolve(FileProcessingDirectory.ERROR.getDirectory())));
              this.fileProvider.createSubDirectory(
                  String.valueOf(
                      baseOutDir.resolve(FileProcessingDirectory.RECEIVED.getDirectory())));
            });
  }
}
