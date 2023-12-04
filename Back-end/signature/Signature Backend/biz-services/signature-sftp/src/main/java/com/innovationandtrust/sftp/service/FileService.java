package com.innovationandtrust.sftp.service;

import com.innovationandtrust.sftp.config.FileIntegrationProperty;
import com.innovationandtrust.sftp.constant.FileProcessingDirectory;
import com.innovationandtrust.sftp.model.SftpFileRequest;
import com.innovationandtrust.sftp.utils.FileValidator;
import com.innovationandtrust.utils.file.provider.FileProvider;
import com.innovationandtrust.utils.file.utils.FileUtils;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

  public void insertFilesToZipped(
      String corporateUuid,
      String zipPath,
      MultipartFile[] signedFiles,
      MultipartFile manifestFile) {
    try {
      var basePath = this.fileProvider.getBasePath();
      var source =
          Path.of(
                  basePath,
                  corporateUuid,
                  FileProcessingDirectory.OUT.getDirectory(),
                  FileProcessingDirectory.PROCESSED.getDirectory())
              .resolve(zipPath);
      if (!Files.exists(source)) {
        throw new IllegalArgumentException("File does not exist: " + source);
      }
      var destinationPath = FileUtils.toPath(source);

      this.fileProvider.unZipCommand(source, destinationPath);
      this.fileProvider.uploads(
          signedFiles, destinationPath.resolve(FileValidator.SIGNED_FOLDER), true);
      this.fileProvider.upload(manifestFile, destinationPath, true);

      var extractedFolder = FileUtils.path(basePath, String.valueOf(destinationPath));
      this.fileProvider.zipFile(extractedFolder, FileUtils.path(basePath, String.valueOf(source)));
      FileUtils.deleteDirectory(extractedFolder);
    } catch (Exception e) {
      log.error("Unable insert into the zipped file:", e);
      throw new IllegalArgumentException(e.getMessage());
    }
  }

  public void deleteDirectory(SftpFileRequest request) {
    var basePath = this.fileProvider.getBasePath();
    var source = Path.of(request.getFilePath());
    var dirPath = FileUtils.toPath(source);
    FileUtils.deleteDirectory(FileUtils.path(basePath, String.valueOf(dirPath)));
  }

  public void createCorporateFolder(String uuid) {
    final var pathIn =
        Path.of(
            this.fileProvider.basePath(),
            fileIntegrationProperty.getBasePathIn(),
            uuid,
            FileProcessingDirectory.IN.name());

    final var pathOut =
        Path.of(this.fileProvider.basePath(), fileIntegrationProperty.getBasePathOut(), uuid);

    try {
      log.debug("Creating corporate folder : {}", pathIn);
      FileUtils.createDirIfNotExist(pathIn);
      log.debug("Creating corporate folder : {}", pathOut);
      FileUtils.createDirIfNotExist(pathOut);
    } catch (Exception e) {
      log.error("Error while creating folder ", e);
    }
  }
}
