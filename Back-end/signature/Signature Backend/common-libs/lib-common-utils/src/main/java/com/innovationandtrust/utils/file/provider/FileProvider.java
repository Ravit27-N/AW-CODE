package com.innovationandtrust.utils.file.provider;

import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.innovationandtrust.utils.file.exception.FileNotFoundException;
import com.innovationandtrust.utils.file.exception.FileRequestException;
import com.innovationandtrust.utils.file.exception.UnableDeleteFileException;
import com.innovationandtrust.utils.file.model.FileInfo;
import com.innovationandtrust.utils.file.model.FileResourceResponse;
import com.innovationandtrust.utils.file.model.FileResponse;
import com.innovationandtrust.utils.file.utils.FileUtils;
import com.innovationandtrust.utils.file.utils.UnzipCommand;
import com.innovationandtrust.utils.file.utils.ZipCommand;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import javax.imageio.ImageIO;
import javax.xml.XMLConstants;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.modelmapper.ModelMapper;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

/** provide a FileProvider as a library with several functionalities. */
@Slf4j
public record FileProvider(@Getter String basePath) {

  public static final String XM_EXTENSION = "xml";

  /**
   * To upload several files.
   *
   * @param files refer to a list of files to upload
   * @param directory refer to directory to store files as a path value
   * @return an object of {@link List<FileResponse>} after completed action
   */
  public List<FileResponse> uploads(MultipartFile[] files, Path directory) {
    Path dirPath =
        FileUtils.createDirIfNotExist(FileUtils.path(basePath, directory.normalize().toString()));
    return Arrays.stream(files).map(file -> FileUtils.store(file, dirPath, false)).toList();
  }

  /**
   * To upload multiple files and response as a list of FileResponse objects.
   *
   * @param files refers to files to upload
   * @param directory refer to directory to store files as a string value
   * @return an object of {@link List<FileResponse>} after completed action
   */
  public List<FileResponse> uploads(MultipartFile[] files, String directory) {
    return this.uploads(files, Paths.get(directory).normalize());
  }

  /**
   * To upload a single file and response as a FileResponse To store the original or create a new
   * existing file, check the request's options.
   *
   * @param file refer to a single file to upload
   * @param directory refer to the directory to store file as a path value
   * @param keepOriginal refer to keeping the original file
   * @return an object of {@link FileResponse} after completed action
   */
  public FileResponse upload(MultipartFile file, Path directory, Boolean keepOriginal) {
    Path dirPath =
        FileUtils.createDirIfNotExist(FileUtils.path(basePath, directory.normalize().toString()));
    return FileUtils.store(file, dirPath, keepOriginal);
  }

  /**
   * To upload a single file and response as a FileResponse To store the original or create a new
   * existing file, check the request's options.
   *
   * @param file refer to a single file to upload
   * @param directory refer to a directory to store file as a string value
   * @param keepOriginal refer to keeping the original file
   * @return an object of {@link FileResponse} after completed action
   */
  public FileResponse upload(MultipartFile file, String directory, Boolean keepOriginal) {
    return this.upload(file, Paths.get(directory).normalize(), keepOriginal);
  }

  /**
   * To upload a single file by resource and response as a FileResourceResponse.
   *
   * @param resource the resource of file (pdf)
   * @param directoryPath refer to the path directory
   * @return an object of {@link FileResourceResponse} after completed action
   */
  public FileResourceResponse upload(Resource resource, String directoryPath, String fileName) {
    Path dirPath = FileUtils.createDirIfNotExist(FileUtils.path(basePath, directoryPath));
    return FileUtils.store(resource, dirPath.normalize(), fileName);
  }

  /**
   * To move files or specific file inside source directory to destination directory if directory
   * was not existed, then generate a new one as well as source files
   *
   * @param sourceFile refer to source file
   * @param destinationFile refer to destination file
   */
  public void moveFile(String sourceFile, String destinationFile) {
    Path source = FileUtils.path(basePath, sourceFile);
    Path destination = FileUtils.createDirIfNotExist(FileUtils.path(basePath, destinationFile));

    if (Files.isDirectory(source)) {
      FileUtils.moveFiles(source, destination);
      return;
    }

    if (Files.exists(source)) {
      FileUtils.moveFile(
          source, Paths.get(destination.toString(), source.getFileName().toString()));
      return;
    }

    throw new FileNotFoundException("Source path doesn't exist");
  }

  /**
   * To call moveFile method of a source and destination as path value.
   *
   * @param source refer to source file
   * @param destination refer to destination file
   */
  public void moveFile(Path source, Path destination) {
    this.moveFile(source.normalize().toString(), destination.normalize().toString());
  }

  public void copyFile(Path source, Path destination) {
    FileUtils.copyTheExistingFile(
        Path.of(basePath, String.valueOf(source)).normalize(),
        Path.of(basePath, String.valueOf(destination)).normalize());
  }

  /**
   * To read or download a file from existing file by providing a string value of path parameter.
   *
   * @param path refer to file's path
   * @return {@link Resource} when completed action
   */
  public Resource download(String path, boolean isAbsolute) {
    Path filePath;
    if (isAbsolute) {
      filePath = Paths.get(path);
    } else {
      filePath = FileUtils.path(basePath, path);
    }
    try {
      Resource resource = new UrlResource(filePath.toUri());
      if (resource.isFile() && resource.isReadable() && resource.exists()) {
        return resource;
      }
    } catch (IOException exception) {
      log.error("File downloading failed", exception);
      throw new FileRequestException(
          "The specified path could lead to an invalid or unreadable file.");
    }
    return null;
  }

  public byte[] loadFile(Path filePath, boolean isAbsolute) {
    var finalPath = filePath;
    if (!isAbsolute) {
      finalPath = Path.of(basePath, String.valueOf(filePath)).toAbsolutePath();
    }
    try {
      return Files.readAllBytes(finalPath);
    } catch (IOException e) {
      log.error("Failed to read file", e);
      throw new FileRequestException(
          "The specified path could lead to an invalid or unreadable file.");
    }
  }

  /**
   * To delete a file.
   *
   * @param path refer to a file path as a string value
   */
  public void deleteFile(String path) {
    try {
      Files.deleteIfExists(FileUtils.path(basePath, path));
    } catch (IOException exception) {
      log.error("Fail while deleting file", exception);
      throw new UnableDeleteFileException("Unable to delete the existing file");
    }
  }

  /**
   * To delete a file.
   *
   * @param path refer to a file full path as a string value
   */
  public void deleteFileFullPath(String path) {
    try {
      Files.deleteIfExists(Path.of(path));
    } catch (IOException exception) {
      log.error("Fail while deleting file", exception);
    }
  }

  /**
   * To delete a file.
   *
   * @param path refer to a file path as a path value
   */
  public void deleteFile(Path path) {
    this.deleteFile(path.normalize().toString());
  }

  /**
   * To get a list of file in side directory.
   *
   * @param path refer to a file path as a string value
   * @return a list of string {@link List<String>} after the action completed
   */
  public List<String> getFiles(String path) {
    Path filePath = FileUtils.path(basePath, path);
    return FileUtils.getFiles(filePath).stream().map(Path::toString).toList();
  }

  /**
   * To get a list of file in side directory.
   *
   * @param path refer to a file path as a path value
   * @return a list of string {@link List<String>} after the action completed
   */
  public List<String> getFiles(Path path) {
    return this.getFiles(path.normalize().toString());
  }

  /**
   * to get the information of specific file.
   *
   * @param path refer to a file path as a string value
   * @return an object of FileInfo {@link FileInfo} after the action is completed
   */
  public FileInfo getFileInfo(String path) {
    File file = new File(FileUtils.path(basePath, path).toString());
    if (!file.exists()) {
      throw new FileNotFoundException("Invalid file");
    }

    return FileInfo.builder()
        .fileName(file.getName())
        .size(file.length() + " bytes")
        .lastModified(new SimpleDateFormat().format(file.lastModified()))
        .hidden(file.isHidden())
        .build();
  }

  /**
   * to get the information of specific file.
   *
   * @param path refer to a file path as a path value
   * @return an object of FileInfo {@link FileInfo} after the action is completed
   */
  public FileInfo getFileInfo(Path path) {
    return this.getFileInfo(path.normalize().toString());
  }
  /**
   * To read or download a file from existing file by providing a path value of path parameter.
   *
   * @param path refer to file's path
   * @return {@link Resource} when the action is completed
   */
  public Resource download(Path path) {
    return this.download(path.toString(), false);
  }

  public void storeFile(Resource file, String filename, String dirs) {
    try {
      var filePath = FileUtils.createDirIfNotExist(FileUtils.path(basePath, dirs));
      Files.copy(
          file.getInputStream(), filePath.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException ex) {
      log.error("Failed to store file", ex);
    }
  }

  public void storeFiles(List<Resource> files, String filename, String dirs) {
    files.forEach(file -> this.storeFile(file, filename, dirs));
  }

  public void createSubDirectory(String subPath) {
    FileUtils.createDirIfNotExist(
        FileUtils.path(basePath, Paths.get(subPath).normalize().toString()));
  }

  public <T extends Serializable> void writeJson(T data, String filename, String dirs) {
    var pathFile = Paths.get(basePath).resolve(dirs);
    if (!pathFile.toFile().exists()) {
      var isCreate = pathFile.toFile().mkdirs();
      log.info("File path {} is created {}", dirs, isCreate);
    }
    var objectMapper =
        new ObjectMapper()
            .enable(Feature.AUTO_CLOSE_SOURCE)
            .enable(Feature.ALLOW_UNQUOTED_FIELD_NAMES);
    try {
      objectMapper.writeValue(new File(pathFile.resolve(filename).toString()), data);
    } catch (IOException e) {
      log.error("Unable to write json file" + filename, e);
      throw new FileNotFoundException("Unable to write metadata file");
    }
  }

  /**
   * To retrieve information from a json file by its name and path, then mapped into the specific
   * class.
   *
   * @param filename refers to the name of a json file
   * @param dirs refers to the directories of a file
   * @param clazz refers to a pojo class to be mapped
   * @return object of the {@link T}
   */
  public <T extends Serializable> T readJson(String filename, String dirs, Class<T> clazz) {
    var objectMapper =
        new ObjectMapper()
            .enable(Feature.AUTO_CLOSE_SOURCE)
            .enable(Feature.ALLOW_UNQUOTED_FIELD_NAMES);
    var filePath = Paths.get(basePath).resolve(dirs).resolve(filename);
    if (!filePath.toFile().exists()) {
      throw new FileNotFoundException("Failed to read metadata file");
    }
    try {
      // Load JSON data from file
      Reader reader = new BufferedReader(new FileReader(filePath.toFile()));
      return objectMapper.readValue(reader, clazz);
    } catch (IOException e) {
      log.error("Failed to read json file" + filename, e);
      throw new FileNotFoundException("Failed to read metadata file");
    }
  }

  /**
   * To update an existing json file.
   *
   * @param data refers to the object for insert into a json file
   * @param filename refers to the name of a file to update
   * @param dirs refers refers to the directories of a file
   * @param clazz refers to the class to be mapped
   * @param <T> refers the type of class
   */
  public <T extends Serializable> void updateJson(
      T data, String filename, String dirs, Class<T> clazz) {
    var json = this.readJson(filename, dirs, clazz);
    new ModelMapper().map(data, json);
    this.writeJson(json, filename, dirs);
  }

  public String encodeFileToBase64(String sourcePath) {
    var filePath = FileUtils.path(basePath, sourcePath);
    try {
      return Base64.getEncoder().encodeToString(Files.readAllBytes(filePath));
    } catch (IOException e) {
      log.error("Failed to encode file to base64", e);
      throw new FileNotFoundException(
          String.format("Unable to covert file to base64  because %s", e.getMessage()));
    }
  }

  /**
   * To unzip file.
   *
   * @param source refers to the source directory of zip file
   * @param destinationPath refers to destination directory for zip file extraction
   */
  public void unZipCommand(Path source, Path destinationPath) {
    try {
      UnzipCommand.newBuilder()
          .sourceZip(Path.of(basePath, String.valueOf(source.normalize())))
          .targetDir(
              FileUtils.createDirIfNotExist(
                  FileUtils.path(basePath, String.valueOf(destinationPath))))
          .build()
          .exec();
    } catch (IOException e) {
      log.error("Failed to unzip file" + e);
      throw new FileRequestException(
          String.format("Unable to unzip file  because %s", e.getMessage()));
    }
  }

  public void zipFile(Path source, Path destinationPath, int byteSize) {
    try {
      ZipCommand.newBuilder()
          .sourceDir(source)
          .targetDir(destinationPath)
          .bufferSize(byteSize)
          .build()
          .exec();
    } catch (IOException e) {
      log.error("Unable to compress file", e);
      throw new FileRequestException(
          String.format("Unable to zip file  because %s", e.getMessage()));
    }
  }

  public void zipFile(Path source, Path destinationPath) {
    this.zipFile(source, destinationPath, 16384);
  }

  public <T> T readXmlValue(Path source, Class<T> clazz, XmlMapper xmlMapper) {
    Assert.isTrue(
        XMLConstants.XML_NS_PREFIX.equals(FilenameUtils.getExtension(source.toString())),
        "Source file must be xml file only!");
    try {
      return xmlMapper.readValue(source.toFile(), clazz);
    } catch (IOException ex) {
      log.error("Unable to read xml file", ex);
      throw new FileRequestException(
          String.format("Unable to read xml file  because %s", ex.getMessage()));
    }
  }

  public <T> T readXmlValue(Path source, Class<T> clazz) {
    return this.readXmlValue(source, clazz, new XmlMapper());
  }

  public Path getXmlPath(Path path) {
    final Path finalPath = Path.of(basePath, String.valueOf(path));
    try (var files = Files.walk(finalPath)) {
      var xmlPath =
          files
              .filter(file -> FilenameUtils.getExtension(String.valueOf(file)).equals(XM_EXTENSION))
              .findFirst()
              .orElse(null);
      if (Objects.nonNull(xmlPath)) {
        return xmlPath.toAbsolutePath();
      }
      throw new IllegalArgumentException("Invalid xml path");
    } catch (IOException e) {
      log.error("Invalid xml file", e);
      throw new IllegalArgumentException("Invalid xml path");
    }
  }

  public String uploadImage(String base64, String directory) {
    FileUtils.createDirIfNotExist(Paths.get(basePath, directory));
    var format = Objects.requireNonNull(this.getImageFormat(base64));
    var pathToStore =
        Paths.get(directory, UUID.randomUUID().toString()).toString().concat(".").concat(format);
    var storePath = FileUtils.path(basePath, pathToStore).toString();
    try {
      ImageIO.write(this.decodeToImage(base64), format, new File(storePath));
    } catch (IOException exception) {
      log.error("Error while uploading image", exception);
      throw new FileRequestException("Error while uploading image");
    }
    return pathToStore;
  }

  private String getImageFormat(String dataUrl) {
    int startIndex = dataUrl.indexOf('/') + 1;
    int endIndex = dataUrl.indexOf(';');

    if (endIndex != -1) {
      return dataUrl.substring(startIndex, endIndex);
    }

    return null;
  }

  private BufferedImage decodeToImage(String imageString) {
    BufferedImage image;
    var base64Image = this.removePrefix(imageString);
    try {
      Base64.Decoder decoder = Base64.getDecoder();
      byte[] imageByte = decoder.decode(base64Image);
      ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
      image = ImageIO.read(bis);
      bis.close();
    } catch (IOException e) {
      log.error("Error while decoding image", e);
      throw new FileRequestException("Error while decoding image");
    }
    return image;
  }

  private String removePrefix(String imageString) {
    String base64Image = imageString;
    if (imageString.startsWith("data:image")) {
      int commaIndex = imageString.indexOf(',');
      if (commaIndex != -1) {
        base64Image = imageString.substring(commaIndex + 1);
      }
    }
    return base64Image;
  }
}
