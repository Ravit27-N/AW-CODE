package com.innovationandtrust.sftp.utils;

import com.innovationandtrust.sftp.constant.FileProcessingStatus;
import com.innovationandtrust.sftp.exception.InvalidSftpFileRequestException;
import com.innovationandtrust.utils.file.utils.FileUtils;
import com.innovationandtrust.utils.file.utils.UnzipCommand;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FileValidator {

  public static final String ZIP_EXTENSION = "zip";
  public static final String XML_EXTENSION = "xml";
  public static final String PDF_EXTENSION = "pdf";

  public static final Pattern FILENAME_REGEX_PATTERN =
      Pattern.compile("[a-f0-9]{8}(?:-[a-f0-9]{4}){4}[a-f0-9]{8}_[^\\\\]+$");

  public static FileProcessingStatus isZipFile(String path) {
    if (ZIP_EXTENSION.equalsIgnoreCase(FilenameUtils.getExtension(path))) {
      return FileProcessingStatus.IS_ZIP;
    }
    return FileProcessingStatus.ERROR_NO_ZIP_FILE;
  }

  public static FileProcessingStatus isZipFile(Path path) {
    return isZipFile(String.valueOf(path));
  }

  public static FileProcessingStatus containPdfFile(String path) {
    var extractPath = Path.of(FilenameUtils.getFullPath(path));
    if (ZIP_EXTENSION.equalsIgnoreCase(FilenameUtils.getExtension(path))) {
      extractPath = extractZip(Path.of(path));
    }
    try (final var files = Files.walk(extractPath)) {
      if (files.anyMatch(
          f -> FilenameUtils.getExtension(String.valueOf(f.getFileName())).equals(PDF_EXTENSION))) {
        return FileProcessingStatus.CONTAIN_PDF;
      }
      return FileProcessingStatus.ERROR_NO_PDF_FILE;
    } catch (IOException e) {
      log.error("Failed to check pdf extension", e);
      return FileProcessingStatus.ERROR_NO_PDF_FILE;
    } finally {
      FileUtils.deleteDirectory(extractPath);
    }
  }

  private static Path extractZip(Path source) {
    Path extractPath =
        source
            .getParent()
            .resolve(FilenameUtils.getBaseName(source.toString()))
            .resolve("extracted").resolve(UUID.randomUUID().toString());
    FileUtils.createDirectories(extractPath);
    try {
      UnzipCommand.newBuilder()
          .sourceZip(source)
          .targetDir(extractPath)
          .bufferSize(1024)
          .build()
          .exec();
      return extractPath;
    } catch (IOException e) {
      log.error("Failed to unzip file", e);
    }
    return source;
  }

  public static FileProcessingStatus containPdfFile(Path path) {
    return containPdfFile(String.valueOf(path));
  }

  public static FileProcessingStatus containXmlFile(String path) {
    var extractPath = Path.of(FilenameUtils.getFullPath(path));
    if (ZIP_EXTENSION.equalsIgnoreCase(FilenameUtils.getExtension(path))) {
      extractPath = extractZip(Path.of(path));
    }
    try (final var files = Files.walk(extractPath)) {
      if (files.anyMatch(
          f -> FilenameUtils.getExtension(String.valueOf(f.getFileName())).equals(XML_EXTENSION))) {
        return FileProcessingStatus.CONTAIN_XML;
      }
      return FileProcessingStatus.ERROR_NO_XML_FILE;
    } catch (IOException e) {
      log.error("Failed to check xml extension", e);
      return FileProcessingStatus.ERROR_NO_XML_FILE;
    } finally {
      FileUtils.deleteDirectory(extractPath);
    }
  }

  public static FileProcessingStatus containXmlFile(Path path) {
    return containXmlFile(String.valueOf(path));
  }

  public static FileProcessingStatus validateFileName(Path source) {
    if (FILENAME_REGEX_PATTERN.matcher(FilenameUtils.getName(String.valueOf(source))).find()) {
      return FileProcessingStatus.VALID_FILE_NAME;
    }
    return FileProcessingStatus.ERROR_INVALID_FILE_NAME;
  }

  public static FileProcessingStatus validateZipFile(Path source) {
    FileProcessingStatus status;
    var validation = List.of("validateFileName", "containPdfFile", "containXmlFile").iterator();
    do {
      if (!validation.hasNext()) {
        return FileProcessingStatus.VALID_FILE;
      }
      String methodName = validation.next();
      try {
        var method = FileValidator.class.getDeclaredMethod(methodName, Path.class);
        status = (FileProcessingStatus) method.invoke(null, source);
      } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
        log.error("Error processing file: " + methodName, e);
        throw new InvalidSftpFileRequestException();
      }
    } while (!FileProcessingStatus.isError(status));
    return status;
  }
}
