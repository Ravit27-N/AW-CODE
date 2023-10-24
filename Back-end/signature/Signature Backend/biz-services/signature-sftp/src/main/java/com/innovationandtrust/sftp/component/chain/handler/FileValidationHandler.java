package com.innovationandtrust.sftp.component.chain.handler;

import com.innovationandtrust.sftp.component.chain.execution.FileProcessingExecutionManager;
import com.innovationandtrust.sftp.constant.FileProcessingDirectory;
import com.innovationandtrust.sftp.constant.FileProcessingStatus;
import com.innovationandtrust.sftp.constant.SftpProcessConstant;
import com.innovationandtrust.sftp.exception.InvalidSftpFileRequestException;
import com.innovationandtrust.sftp.model.EmailModel;
import com.innovationandtrust.sftp.model.SftpFileRequest;
import com.innovationandtrust.sftp.service.FileService;
import com.innovationandtrust.sftp.service.MailService;
import com.innovationandtrust.sftp.utils.FileValidator;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.chain.ExecutionState;
import com.innovationandtrust.utils.chain.handler.AbstractExecutionHandler;
import com.innovationandtrust.utils.file.provider.FileProvider;
import java.nio.file.Path;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileValidationHandler extends AbstractExecutionHandler {

  private final FileProvider fileProvider;

  private final FileProcessingExecutionManager fileProcessingExecutionManager;

  private final MailService mailService;

  private final FileService fileService;

  @Override
  public ExecutionState execute(ExecutionContext context) {
    var request =
        context
            .find(SftpProcessConstant.SFTP_FILE_REQUEST, SftpFileRequest.class)
            .orElseThrow(InvalidSftpFileRequestException::new);
    var status = this.processFile(request);
    if (Objects.equals(status, ExecutionState.NEXT)) {
      context.put(SftpProcessConstant.SFTP_FILE_REQUEST, request);
      fileProcessingExecutionManager.execute(context);
    }
    return status;
  }

  private ExecutionState processFile(SftpFileRequest request) {
    if (!Objects.equals(
        FileValidator.isZipFile(request.getFilePath()), FileProcessingStatus.IS_ZIP)) {
      log.warn("The processing file is not a zip file.");
      // copy file to error folder in source-out
      this.fileService.copyFileToErrorOUT(request);
      // copy file from processing folder into error folder in source-in
      this.fileService.moveFileToErrorIN(request);
      request.setFilePath(
          String.valueOf(
              Path.of(
                      request.getIntegrationProperty().getBasePathOut(),
                      request.getCorporateUser().getUserEntityId(),
                      FileProcessingDirectory.OUT.getDirectory(),
                      FileProcessingDirectory.ERROR.getDirectory())
                  .resolve(request.getFilename())));
      var errorEmail =
          EmailModel.builder()
              .subject("Le fichier de traitement n'est pas un fichier zip")
              .message("Le fichier de traitement n'est pas un fichier zip: ")
              .build();
      this.mailService.sendErrorMail(request, errorEmail);
      return ExecutionState.END;
    } else {
      log.info(
          "Processing zip file with corporate uuid: {}",
          request.getCorporateUser().getUserEntityId());
      var processPath =
          Path.of(
              request.getIntegrationProperty().getBasePathIn(),
              request.getCorporateUser().getUserEntityId(),
              FileProcessingDirectory.PROCESSING.getDirectory());
      this.fileProvider.moveFile(Path.of(request.getFilePath()), processPath);
      request.setFilePath(String.valueOf(processPath.resolve(request.getFilename())));
      return ExecutionState.NEXT;
    }
  }
}
