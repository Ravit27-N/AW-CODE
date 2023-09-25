package com.allweb.rms.service;

import com.allweb.rms.core.storage.StorageObject;
import com.allweb.rms.core.storage.StorageObjectManager;
import com.allweb.rms.utils.StorageUtils;
import com.allweb.rms.utils.UUIDUtils;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.SneakyThrows;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class TemporaryStorageService {
  private static final String TEMPORARY_FOLDER_NAME = "temp";
  private final StorageUtils storageUtils;
  private final StorageObject temporaryFolder;

  public TemporaryStorageService(StorageUtils storageUtils) {
    this.storageUtils = storageUtils;
    this.temporaryFolder = this.storageUtils.getSubDirectory(TEMPORARY_FOLDER_NAME);
  }

  public void saveToTemporaryStorage(InputStream source, String name) {
    storageUtils.saveFile(source, name, this.temporaryFolder.getStorageObjectManager());
  }

  @SneakyThrows
  public String uploadToTemporaryStorage(MultipartFile file) {
    String uuid = UUID.randomUUID().toString(); // uuid_fileName.extension
    String fileName = uuid + "_" + file.getOriginalFilename();
    saveToTemporaryStorage(file.getInputStream(), fileName);
    return fileName;
  }

  @SneakyThrows
  public List<String> uploadMultiToTemporaryStorage(MultipartFile[] files) {
    List<String> filenames = new ArrayList<>();

    for (MultipartFile file : files) {
      String uuid = UUID.randomUUID().toString(); // uuid_fileName.extension
      String fileName = uuid + "_" + file.getOriginalFilename();
      saveToTemporaryStorage(file.getInputStream(), fileName);
      filenames.add(fileName);
    }
    return filenames;
  }

  public StorageObject deleteFromTemporaryStorage(String subDirectory, String fileName)
      throws IOException {
    StorageObject targetFile =
        this.storageUtils.getFile(this.temporaryFolder, subDirectory, fileName);
    StorageObjectManager targetFileManager = targetFile.getStorageObjectManager();
    targetFileManager.remove();
    return targetFile;
  }

  public void deleteFromTemporaryStorage(String fileName) throws IOException {
    StorageObject targetFile = this.storageUtils.getFile(this.temporaryFolder, fileName);
    StorageObjectManager targetFileManager = targetFile.getStorageObjectManager();
    targetFileManager.remove();
  }

  @SneakyThrows
  public void moveTo(
      List<String> sourceFileName, StorageObject destination, boolean useOriginalFileName) {
    for (String filename : sourceFileName) {
      StorageObjectManager source =
          this.storageUtils.getFile(this.temporaryFolder, filename).getStorageObjectManager();
      String originalFileName =
          useOriginalFileName ? UUIDUtils.removeUUIDFromStart("_", filename) : filename;
      Path target = destination.getStorageObjectManager().getPath().resolve(originalFileName);
      Files.move(source.getPath(), target);
    }
  }

  public Resource loadFile(String filename) {
    return storageUtils.loadFile(filename, this.temporaryFolder);
  }
}
