package com.allweb.rms.core.storage.driver.filesystem;

import com.allweb.rms.core.storage.SecurityConstraints;
import com.allweb.rms.core.storage.Storage;
import com.allweb.rms.core.storage.StorageConstants;
import com.allweb.rms.core.storage.StorageFactory;
import com.allweb.rms.core.storage.Volume;
import com.allweb.rms.core.storage.properties.StorageConfigurationProperties;
import com.allweb.rms.core.storage.properties.StorageConfigurationProperties.VolumeProperties;
import com.allweb.rms.core.storage.properties.StorageConfigurationProperties.VolumeProperties.SecurityConstraintProperties;
import com.allweb.rms.core.storage.properties.StorageConfigurationProperties.VolumeProperties.StorageObjectProperties;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileSystemStorageFactory implements StorageFactory {
  private char baseId = 'A';
  private Storage storage;

  public FileSystemStorageFactory(StorageConfigurationProperties storageConfigurationProperties) {

    List<VolumeProperties> volumePropertiesList = storageConfigurationProperties.getVolumes();
    if (!volumePropertiesList.isEmpty()) {
      // Configure storage's volume
      Map<String, Volume> volumeMap = new ConcurrentHashMap<>();
      for (VolumeProperties volumeProperties : volumePropertiesList) {
        FileSystemVolume fileSystemVolume = this.buildVolume(volumeProperties);
        if (fileSystemVolume != null) {
          volumeMap.put(fileSystemVolume.getId(), fileSystemVolume);
        }
      }
      FileSystemStorage fileSystemStorage = new FileSystemStorage();
      fileSystemStorage.setAlias(storageConfigurationProperties.getAlias());
      fileSystemStorage.setDefaultVolumeName(storageConfigurationProperties.getDefaultVolumeName());
      fileSystemStorage.setVolumes(volumeMap);
      this.storage = fileSystemStorage;
    }
  }

  @Override
  public Storage getStorage() {
    return this.storage;
  }

  private FileSystemVolume buildVolume(VolumeProperties volumeProperties) {
    String volumeAlias = volumeProperties.getAlias();
    Path volumePath = volumeProperties.getPath();
    String volumeId = String.valueOf(baseId++);

    if (this.checkOrCreateDirectory(volumePath)) {
      FileSystemVolume fileSystemVolume = new FileSystemVolume(volumeId, volumeAlias, volumePath);
      // Configure volume's storage object, folder.
      List<StorageObjectProperties> storageObjectPropertiesList =
          volumeProperties.getStorageObjects();
      this.buildVolumeStorageObjects(fileSystemVolume, storageObjectPropertiesList);
      return fileSystemVolume;
    }
    return null;
  }

  private void buildVolumeStorageObjects(
      FileSystemVolume fileSystemVolume,
      List<StorageObjectProperties> storageObjectPropertiesList) {
    if (!storageObjectPropertiesList.isEmpty()) {
      for (StorageObjectProperties storageObjectProperty : storageObjectPropertiesList) {
        Path storageObjectPath = storageObjectProperty.getPath();
        String storageObjectPathString = storageObjectPath.toString().replaceFirst("^[\\\\,/]", "");
        if (this.checkOrCreateDirectory(
            fileSystemVolume.getPath().resolve(storageObjectPathString))) {

          FileSystemStorageObject fileSystemObject =
              new FileSystemStorageObject(
                  storageObjectProperty.getAlias(),
                  storageObjectProperty.getMime(),
                  fileSystemVolume,
                  storageObjectPath);
          SecurityConstraintProperties securityConstraintsProperty =
              storageObjectProperty.getSecurityConstraints();
          if (securityConstraintsProperty != null) {
            SecurityConstraints securityConstraints =
                SecurityConstraints.builder()
                    .locked(securityConstraintsProperty.isLocked())
                    .readable(securityConstraintsProperty.isReadable())
                    .writable(securityConstraintsProperty.isWritable())
                    .build();
            fileSystemObject.setSecurityConstraints(securityConstraints);
          }
          String hashKey = fileSystemObject.getHashKey(false);

          fileSystemVolume.addStorage(hashKey, fileSystemObject);
        }
      }
    } else {
      Path defaultStorageObjectPath = fileSystemVolume.getPath();
      if (this.checkOrCreateDirectory(defaultStorageObjectPath)) {
        FileSystemStorageObject defaultFileSystemObject =
            new FileSystemStorageObject(
                fileSystemVolume.getAlias(),
                StorageConstants.MIME_TYPE_DIRECTORY,
                fileSystemVolume,
                defaultStorageObjectPath);
        String hashKey = defaultFileSystemObject.getHashKey(false);

        fileSystemVolume.addStorage(hashKey, defaultFileSystemObject);
      }
    }
  }

  private boolean checkOrCreateDirectory(Path path) {
    if (!Files.exists(path)) {
      try {
        Files.createDirectory(path);
        return true;
      } catch (IOException e) {
        log.debug(e.getMessage(), e);
        return false;
      }
    }
    return true;
  }
}
