package com.allweb.rms.core.storage.driver.filesystem;

import com.allweb.rms.core.storage.StorageObject;
import com.allweb.rms.core.storage.Volume;
import com.allweb.rms.core.storage.utils.PathUtils;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Builder;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileSystemVolume implements Volume {
  private static final Logger LOGGER = LoggerFactory.getLogger(FileSystemVolume.class);

  private final Map<String, FileSystemStorageObject> storageObjectCaches =
      new ConcurrentHashMap<>();
  private final FileSystemStorageObject baseStorageObject;

  @Getter private final String alias;

  private final String id;

  @Getter private final Path path;

  @Builder()
  public FileSystemVolume(String id, String alias, Path path) {
    this.id = id;
    this.alias = alias;
    this.path = path;
    baseStorageObject =
        FileSystemStorageObject.builder().alias(alias).volume(this).path(Paths.get("")).build();
    storageObjectCaches.putIfAbsent(this.baseStorageObject.getHashKey(false), baseStorageObject);
  }

  public Path getAbsolutePath() {
    return this.path.normalize().toAbsolutePath();
  }

  @Override
  public List<StorageObject> getStorageObjects() {
    return Collections.unmodifiableList(new ArrayList<>(storageObjectCaches.values()));
  }

  @Override
  public StorageObject getStorageObject(String hashKey) {
    if (!this.storageObjectCaches.containsKey(hashKey)) {
      Path decodedPath = PathUtils.decodeHashKeyAsPath(hashKey);
      decodedPath = PathUtils.removeParentPath(this.getPath(), decodedPath);
      String name = decodedPath.getFileName().toString();

      FileSystemStorageObject fileSystemObject =
          FileSystemStorageObject.builder().alias(name).volume(this).path(decodedPath).build();
      if (!hashKey.equals(fileSystemObject.getHashKey(false))) {
        LOGGER.debug("Bad format hash key: {}", hashKey);
        return null;
      }
      this.addStorage(hashKey, fileSystemObject);
      return fileSystemObject;
    }
    return storageObjectCaches.get(hashKey);
  }

  @Override
  public String getId() {
    return this.id;
  }

  @Override
  public StorageObject getBaseStorageObject() {
    return this.baseStorageObject;
  }

  // Volume's StorageObject manipulation

  void setStorages(Map<String, FileSystemStorageObject> storages) {
    this.storageObjectCaches.putAll(storages);
  }

  StorageObject addStorage(String hashKey, FileSystemStorageObject storage) {
    return storageObjectCaches.putIfAbsent(hashKey, storage);
  }

  StorageObject remove(String hashKey) {
    return storageObjectCaches.remove(hashKey);
  }

  boolean contains(String hashKey) {
    return storageObjectCaches.containsKey(hashKey);
  }
}
