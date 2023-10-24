package com.innovationandtrust.utils.file.utils;

import com.innovationandtrust.utils.exception.exceptions.FileExceedLimitException;
import com.innovationandtrust.utils.exception.exceptions.FileNotSupportException;
import com.innovationandtrust.utils.exception.exceptions.InternalErrorException;
import com.innovationandtrust.utils.file.exception.FileRequestException;
import com.innovationandtrust.utils.file.exception.UnableCreateDirectoryException;
import com.innovationandtrust.utils.file.exception.UnableMoveFileException;
import com.innovationandtrust.utils.file.model.FileResourceResponse;
import com.innovationandtrust.utils.file.model.FileResponse;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.InternalServerErrorException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.PixelGrabber;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.swing.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Range;
import org.springframework.core.io.Resource;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;

/** Provide a file utility for working with file */
@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class FileUtils {

  private static final String OS = System.getProperty("os.name").toLowerCase();

  private static final Color backColor = new Color(255, 255, 255);
  private static final int THRESHOLD = 35;
  private static final int TRANSPARENT = 0; // 0x00000000;

  /**
   * validate width and height for image file uploaded.
   *
   * @param file refers to image that want to check width and height
   */
  public static void validateWidthHeights(MultipartFile file) {
    try {
      log.info("Getting width and height of the image...");
      InputStream inputStream = new BufferedInputStream(file.getInputStream());
      BufferedImage img = ImageIO.read(inputStream);
      int width = img.getWidth();
      int height = img.getHeight();

      Range<Integer> widthHeightRange = Range.between(500, 2000);
      if (!(widthHeightRange.contains(width) && widthHeightRange.contains(height))) {
        log.error("The image should measure between 500x500 and 2000×2000 pixels.");
        throw new BadRequestException(
            "The image should measure between 500x500 and 2000×2000 pixels.");
      }
    } catch (IOException e) {
      log.error("Failed to get width and height: ", e);
      throw new InternalServerErrorException("Failed to get width and height...");
    }
  }

  /**
   * Validate file content type.
   *
   * @param fileContentType refers to file content type
   * @param validateContentType refers to content type that we want to validate with file uploaded
   */
  public static void validateFileContentType(String fileContentType, String validateContentType) {
    if (!Objects.equals(fileContentType, validateContentType)) {
      log.error("Format not supported, we accept only: {}", validateContentType);
      throw new BadRequestException("Format not supported, we accept only: " + validateContentType);
    }
  }

  /**
   * to create a directory if not exist
   *
   * @param dirPath refer to the file directory
   * @return a path {@link Path} after completed action
   */
  public static Path createDirIfNotExist(Path dirPath) {
    if (dirPath.toString().contains(".")) {
      dirPath = dirPath.getParent();
    }

    return Files.isDirectory(dirPath) && Files.exists(dirPath)
        ? dirPath
        : createDirectories(dirPath);
  }

  /**
   * To get the file path.
   *
   * @param basePath refer to a base path
   * @param path refer to file's path
   * @return a path {@link Path} after completed action
   */
  public static Path path(String basePath, String path) {
    return Paths.get(basePath, path).normalize();
  }

  /**
   * To copy an existing file.
   *
   * @param source refer to source's path
   * @param destination refer to destination's path
   * @return a path {@link Boolean} after completed action
   */
  public static Boolean copyTheExistingFile(Path source, Path destination) {
    if (Files.exists(source)) {
      try {
        FileUtils.createDirIfNotExist(destination);
        if (!Files.isDirectory(source)) {
          Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
        } else {
          try (var files = Files.walk(source)) {
            files
                .filter(Files::isRegularFile)
                .forEach(
                    file -> {
                      try {
                        Files.copy(
                            file,
                            Paths.get(String.valueOf(destination)).resolve(file.getFileName()),
                            StandardCopyOption.REPLACE_EXISTING);
                      } catch (IOException e) {
                        log.error("Failed to copy file: ", e);
                      }
                    });
          }
        }
        return true;
      } catch (IOException exception) {
        log.error("File creation failed: ", exception);
        return false;
      }
    }
    return false;
  }

  /**
   * To get a files that exist inside the folder.
   *
   * @param path refer to a path of file
   * @return a path {@link List<Path>} after completed action
   */
  public static List<Path> getFiles(Path path) {
    try (var files = Files.walk(path)) {
      return files.toList();
    } catch (IOException exception) {
      log.error("Error while getting files:  ", exception);
      throw new FileRequestException("Unable to get files");
    }
  }

  /**
   * To move a files inside source folder to destination folder.
   *
   * @param source refer to path's source file
   * @param destination refer to path's destination file
   */
  public static void moveFiles(Path source, Path destination) {
    getFiles(source)
        .forEach(
            file ->
                moveFile(file, Paths.get(destination.toString(), file.getFileName().toString())));
  }

  /**
   * To move a specific file to destination folder.
   *
   * @param source refer to path's source file
   * @param destination refer to path's destination file
   */
  public static void moveFile(Path source, Path destination) {
    try {
      if (!Files.isDirectory(source)) {
        Files.move(source, destination, StandardCopyOption.ATOMIC_MOVE);
      }
    } catch (IOException exception) {
      log.error("File moving failed", exception);
      throw new UnableMoveFileException("Unable to move file");
    }
  }

  /**
   * To store a file from a resource.
   *
   * @param fileContent the resource of file (pdf)
   * @param directoryPath refer to the path directory
   * @return {@link FileResourceResponse} can get it only these properties
   */
  public static FileResourceResponse store(
      Resource fileContent, Path directoryPath, String fileName) {
    var fullPath = Paths.get(directoryPath.toString(), fileName).toString();
    File output = new File(fullPath);

    try (FileOutputStream outputStream = new FileOutputStream(output)) {
      outputStream.write(fileContent.getContentAsByteArray());
    } catch (IOException e) {
      log.error("File uploading failed", e);
      throw new FileRequestException("Unable to upload file");
    }
    return FileResourceResponse.builder()
        .fileName(fileContent.getFilename())
        .fullPath(fullPath)
        .build();
  }

  /**
   * To store a file to the specific destination.
   *
   * @param file multipart file
   * @param directoryPath refer to the path directory
   * @param keepOriginal refer to the original file
   * @return {@link FileResponse} after completed the action
   */
  public static FileResponse store(MultipartFile file, Path directoryPath, Boolean keepOriginal) {
    if (file.getSize() <= 0) {
      throw new FileRequestException("Please upload a file size that is larger than 0 KB.");
    }

    return storeFile(
        file, Paths.get(directoryPath.toString(), file.getOriginalFilename()), keepOriginal);
  }

  private static FileResponse storeFile(MultipartFile file, Path storePath, Boolean keepOriginal) {
    try {
      String fileName = generateFileName(storePath);
      Path pathFileName = Boolean.TRUE.equals(keepOriginal) ? storePath : Paths.get(fileName);

      if (Boolean.FALSE.equals(copyTheExistingFile(storePath, pathFileName))) {
        file.transferTo(pathFileName);
      }

      return FileResponse.builder()
          .fileName(pathFileName.getFileName().toString())
          .originalFileName(file.getOriginalFilename())
          .contentType(file.getContentType())
          .fullPath(pathFileName.toString())
          .size(file.getSize())
          .build();
    } catch (IOException exception) {
      log.error("File uploading failed", exception);
      throw new FileRequestException("Unable to upload file");
    }
  }

  private static String generateFileName(Path fileName) {
    String pathToStore =
        Paths.get(fileName.getParent().toString(), UUID.randomUUID().toString()).toString();
    return pathToStore + fileName.toString().substring(fileName.toString().indexOf("."));
  }

  public static void deleteDirectory(Path directory) {
    if (Files.isDirectory(directory)) {
      try {
        FileSystemUtils.deleteRecursively(directory);
      } catch (IOException exception) {
        log.error("File deleting fails", exception);
        throw new FileRequestException("Unable to delete directory " + exception.getMessage());
      }
    }
  }

  public static Path toPath(Path source) {
    return Path.of(source.toString().split("\\.")[0]);
  }

  public static void createAndWriteTextFile(Path path, String text) {
    try {
      Files.writeString(path, text);
    } catch (IOException exception) {
      log.error("File creation failed", exception);
      throw new FileRequestException("Unable to create file");
    }
  }

  public static boolean isUnix() {
    return (OS.contains("nix") || OS.contains("nux") || OS.contains("aix"));
  }

  public static Path createDirectories(Path path) {
    try {
      log.info("System OS: {}", OS);
      if (isUnix()) {
        Set<PosixFilePermission> permissions = PosixFilePermissions.fromString("rwxrwxrwx");
        FileAttribute<Set<PosixFilePermission>> fileAttributes =
            PosixFilePermissions.asFileAttribute(permissions);
        Files.createDirectories(path, fileAttributes);
      }
      Files.createDirectories(path);
    } catch (IOException e) {
      log.error("Unable to create directory", e);
      throw new UnableCreateDirectoryException("Error creating directory!");
    }
    return path;
  }

  public static void removeWhiteBackground(File file) {
    try {
      var image = ImageIO.read(file);
      int width = image.getWidth();
      int height = image.getHeight();
      BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
      Graphics g = img.getGraphics();
      g.drawImage(image, 0, 0, null);
      for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
          int pixel = img.getRGB(x, y);
          var color = new Color(pixel);
          int dr = Math.abs(color.getRed() - backColor.getRed()),
              dg = Math.abs(color.getGreen() - backColor.getGreen()),
              db = Math.abs(color.getBlue() - backColor.getBlue());
          if (dr < THRESHOLD && dg < THRESHOLD && db < THRESHOLD) {
            img.setRGB(x, y, TRANSPARENT);
          }
        }
      }
      ImageIO.write(img, "PNG", file);
    } catch (Exception ignore) {
      log.error("Cannot remove background...");
    }
  }

  /**
   * @param maxSize refers to max of file size to validate maxSize as string 12MB, 1200KB
   */
  public static void validateFileSize(MultipartFile file, String maxSize) {
    final DataSize maxFileSize = DataSize.parse(maxSize);
    final DataSize fileSize = DataSize.ofBytes(file.getSize());
    log.info("The max size of file {} and this file size {}", maxSize, fileSize.toMegabytes());
    if (maxFileSize.compareTo(fileSize) < 0) {
      throw new FileExceedLimitException();
    }
  }

  public static void validateImageType(MultipartFile file, Set<String> allowTypes) {
    try {
      ImageInputStream imageInputStream = ImageIO.createImageInputStream(file.getInputStream());
      Iterator<ImageReader> imageReaders = ImageIO.getImageReaders(imageInputStream);
      if (!imageReaders.hasNext()) {
        throw new FileNotSupportException();
      }
      ImageReader reader = imageReaders.next();
      reader.setInput(imageInputStream);

      if (!allowTypes.contains(reader.getFormatName().toLowerCase())) {
        throw new FileNotSupportException(reader.getFormatName());
      }
    } catch (IOException exception) {
      log.error("Can't not read metadata file");
      throw new InternalErrorException("Fail to read metadata file.");
    }
  }

  public static boolean isTransparentImage(File file) {
    try {
      BufferedImage img = ImageIO.read(file);
      PixelGrabber pg = new PixelGrabber(img, 0, 0, 1, 1, false);
      pg.grabPixels();
      ColorModel cm = pg.getColorModel();
      return cm.hasAlpha();
    } catch (IOException | InterruptedException e) {
      log.error("An error has occurred: ", e);
      return false;
    }
  }
}
