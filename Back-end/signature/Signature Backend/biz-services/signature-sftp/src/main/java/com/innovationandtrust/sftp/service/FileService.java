package com.innovationandtrust.sftp.service;

import com.innovationandtrust.sftp.config.FileIntegrationProperty;
import com.innovationandtrust.sftp.constant.FileProcessingDirectory;
import com.innovationandtrust.sftp.model.SftpFileRequest;
import com.innovationandtrust.utils.file.provider.FileProvider;
import com.innovationandtrust.utils.file.utils.FileUtils;
import java.nio.file.Path;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {

  private static final String ERROR_FILE_NAME = "error.txt";

  private final FileProvider fileProvider;

  private final FileIntegrationProperty fileIntegrationProperty;

  public void moveFileToErrorIN(SftpFileRequest request) {
    this.fileProvider.moveFile(
        Path.of(request.getFilePath()),
        Path.of(
            request.getIntegrationProperty().getBasePathIn(),
            request.getCorporateUuid(),
            FileProcessingDirectory.ERROR.getDirectory()));
  }

  public void moveFileToDoneIN(SftpFileRequest request) {
    this.fileProvider.moveFile(
        Path.of(request.getFilePath()),
        Path.of(
            request.getIntegrationProperty().getBasePathIn(),
            request.getCorporateUuid(),
            FileProcessingDirectory.DONE.getDirectory()));
  }

  public void copyFileToErrorOUT(SftpFileRequest request) {
    this.fileProvider.copyFile(
        Path.of(request.getFilePath()),
        Path.of(
                request.getIntegrationProperty().getBasePathOut(),
                request.getCorporateUuid(),
                FileProcessingDirectory.OUT.getDirectory(),
                FileProcessingDirectory.ERROR.getDirectory())
            .resolve(request.getFilename()));
  }

  public void copyFileToProcessed(SftpFileRequest request) {
    this.fileProvider.copyFile(
        Path.of(request.getFilePath()),
        Path.of(
                request.getIntegrationProperty().getBasePathOut(),
                request.getCorporateUuid(),
                FileProcessingDirectory.OUT.getDirectory(),
                FileProcessingDirectory.PROCESSED.getDirectory())
            .resolve(request.getFilename()));
  }

  public void createErrorMessage(SftpFileRequest request) {
    var basePath = this.fileProvider.getBasePath();
    var source = Path.of(request.getFilePath());
    var destinationPath = FileUtils.toPath(source);

    this.fileProvider.unZipCommand(source, destinationPath);

    FileUtils.createAndWriteTextFile(
        Path.of(basePath, String.valueOf(destinationPath), ERROR_FILE_NAME), request.getMessage());

    var extractedFolder = FileUtils.path(basePath, String.valueOf(destinationPath));
    this.fileProvider.zipFile(extractedFolder, FileUtils.path(basePath, String.valueOf(source)));
    FileUtils.deleteDirectory(extractedFolder);
  }

  public void deleteDirectory(SftpFileRequest request) {
    var basePath = this.fileProvider.getBasePath();
    var source = Path.of(request.getFilePath());
    var dirPath = FileUtils.toPath(source);
    FileUtils.deleteDirectory(FileUtils.path(basePath, String.valueOf(dirPath)));
  }

  public void createCorporateFolder(String uuid) {
    Path path =
        Path.of(
            this.fileProvider.basePath(),
            fileIntegrationProperty.getBasePathIn(),
            uuid,
            FileProcessingDirectory.IN.name());
    try {
      log.info("Creating corporate folder : " + path);
      FileUtils.createDirIfNotExist(path);
    } catch (Exception e) {
      log.error("Error while creating folder ", e);
    }
  }
}
