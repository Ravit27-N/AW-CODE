package com.allweb.rms.core.storage.driver.filesystem;

import com.allweb.rms.core.storage.SecurityConstraints;
import com.allweb.rms.core.storage.StorageObject;
import com.allweb.rms.core.storage.StorageObjectManager;
import com.allweb.rms.core.storage.Volume;
import com.allweb.rms.core.storage.utils.MimeTypeUtils;
import com.allweb.rms.core.storage.utils.PathUtils;
import io.micrometer.core.instrument.util.StringUtils;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class FileSystemStorageObject implements StorageObject {
  private static final List<String> SEPARATORS = Arrays.asList("/", "\\");

  private final FileSystemVolume rootVolume;
  private final StorageObjectManager storageObjectManager;
  @Getter private final Path path;
  @Setter SecurityConstraints securityConstraints = new SecurityConstraints();
  @Setter @Getter private String alias;
  @Setter private String mime;

  @Builder()
  public FileSystemStorageObject(String alias, String mimeType, Volume volume, Path path) {
    this.alias = alias;
    this.mime = mimeType;

    this.rootVolume = (FileSystemVolume) volume; // upload
    String pathString =
        path.toString().length() > 0 && SEPARATORS.contains(path.toString().substring(0, 1))
            ? path.toString().substring(1)
            : path.toString(); // temp

    this.path = this.rootVolume.getPath().resolve(pathString); // upload/temp

    this.storageObjectManager = new FileSystemStorageObjectManager(this);
  }

  public Path getAbsolutePath() {
    return this.path.toAbsolutePath();
  }

  @Override
  public String getMimeType() throws IOException {
    if (StringUtils.isBlank(this.mime)) {
      return MimeTypeUtils.getMimeType(this.getPath());
    }
    return this.mime;
  }

  @Override
  public String getName() {
    return this.path.getFileName().toString();
  }

  @Override
  public Volume getRootVolume() {
    return this.rootVolume;
  }

  @Override
  public StorageObjectManager getStorageObjectManager() {
    return this.storageObjectManager;
  }

  /**
   * {@inheritDoc}
   *
   * @param includeVolumeId if include volume id at the start of this hash value, otherwise false.
   * @return Hash key.
   */
  @Override
  public String getHashKey(boolean includeVolumeId) {
    FileSystemVolume fileSystemVolume = (FileSystemVolume) this.getRootVolume();
    Path root = fileSystemVolume.getPath();
    String encodedKey = encodeKey(root, this.getPath());
    if (includeVolumeId) {
      encodedKey = fileSystemVolume.getId() + "_" + encodedKey;
    }

    return encodedKey;
  }

  private String encodeKey(Path root, Path path) {
    if (path.startsWith(root)) {
      Path subPath = path.subpath(0, path.getNameCount());
      return PathUtils.encodeHashKey(subPath);
    }
    return "";
  }

  @Override
  public SecurityConstraints getSecurityConstraints() {
    return this.securityConstraints;
  }
}
