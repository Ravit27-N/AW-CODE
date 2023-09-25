package com.allweb.rms.utils;

import com.allweb.rms.core.storage.Storage;
import com.allweb.rms.core.storage.StorageFactory;
import com.allweb.rms.core.storage.StorageObject;
import com.allweb.rms.core.storage.StorageObjectManager;
import com.allweb.rms.core.storage.Volume;
import com.allweb.rms.exception.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Optional;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;

@Component
public class StorageUtils {
  private static final String DEFAULT_VOLUME_NAME = "upload";
  private final Storage storage;

  public StorageUtils(StorageFactory storageFactory) {
    this.storage = storageFactory.getStorage();
  }

  public StorageObject getSubDirectory(String subDirectoryName) {
    Optional<Volume> volume =
        this.storage.getVolumes().stream()
            .filter(vol -> DEFAULT_VOLUME_NAME.equals(vol.getAlias()))
            .findFirst();
    if (volume.isPresent()) {
      Optional<StorageObject> subDirectory =
          volume.get().getStorageObjects().stream()
              .filter(storageObject -> subDirectoryName.equals(storageObject.getName()))
              .findFirst();
      if (subDirectory.isPresent()) {
        return subDirectory.get();
      }
    }
    return null;
  }

  public StorageObject getFile(StorageObject targetFolder, String sourcePath, String sourceFileName)
      throws IOException {
    StorageObjectManager targetFolderManager = targetFolder.getStorageObjectManager();
    if (targetFolderManager.exists(sourcePath)) {
      StorageObject subDirectory = targetFolderManager.getChild(sourcePath);
      StorageObjectManager subDirectoryManager = subDirectory.getStorageObjectManager();
      if (subDirectoryManager.isDirectory() && subDirectoryManager.exists(sourceFileName)) {
        return subDirectoryManager.getChild(sourceFileName);
      }
    }
    return null;
  }

  public StorageObject getFile(StorageObject targetFolder, String name) {
    StorageObjectManager targetFolderManager = targetFolder.getStorageObjectManager();
    if (targetFolderManager.exists(name)) {
      return targetFolderManager.getChild(name);
    }
    return null;
  }

  @SneakyThrows
  public Resource load(Path pathFile) {
    Resource resource = new UrlResource(pathFile.toUri().normalize());
    if (resource.exists() || resource.isReadable()) {
      return resource;
    } else {
      throw new FileNotFoundException("Could not read the file!");
    }
  }

  @SneakyThrows
  public Resource loadFile(String filename, StorageObject path) {
    final Resource[] resource = new Resource[1];
    path.getStorageObjectManager()
        .getChildren(3, null)
        .forEach(
            f -> {
              if (f.getName().startsWith(filename)) {
                resource[0] = this.load(f.getStorageObjectManager().getPath());
              }
            });
    if (resource[0] == null) throw new FileNotFoundException("File not found!");
    return resource[0];
  }

  @SneakyThrows
  public void removeFile(String filename, StorageObject path) {
    StorageObject targetFile = this.getFile(path, filename);
    StorageObjectManager targetFileManager = targetFile.getStorageObjectManager();
    targetFileManager.remove();
  }

  @SneakyThrows
  public void saveFile(InputStream source, String name, StorageObjectManager folderManager) {
    if (!folderManager.exists(name)) {
      StorageObject createdFile = folderManager.createFile(name, true);
      StorageObjectManager createdFileManager = createdFile.getStorageObjectManager();
      try (OutputStream output = createdFileManager.openOutputStream()) {
        IOUtils.copy(source, output);
      }
    }
  }
}
