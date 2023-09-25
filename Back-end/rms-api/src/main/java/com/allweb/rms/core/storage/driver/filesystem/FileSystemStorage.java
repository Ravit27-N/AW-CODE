package com.allweb.rms.core.storage.driver.filesystem;

import com.allweb.rms.core.storage.SecurityConstraints;
import com.allweb.rms.core.storage.Storage;
import com.allweb.rms.core.storage.Volume;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Setter;

public final class FileSystemStorage implements Storage {
  private static final String SEPARATOR = "_";
  private final Map<String, Volume> volumes = new ConcurrentHashMap<>();
  private SecurityConstraints securityConstraints;
  @Setter private String alias;
  @Setter private String defaultVolumeName;

  @Override
  public String getAlias() {
    return alias;
  }

  @Override
  public List<Volume> getVolumes() {
    return Collections.unmodifiableList(new ArrayList<>(this.volumes.values()));
  }

  void setVolumes(Map<String, Volume> volumes) {
    this.volumes.clear();
    this.volumes.putAll(volumes);
  }

  @Override
  public Volume getVolume(String id) {
    return volumes.get(id);
  }

  @Override
  public Volume getDefaultVolume() {
    Optional<Volume> defaultVolumeObject =
        this.getVolumes().stream()
            .filter(volume -> volume.getAlias().equalsIgnoreCase(this.defaultVolumeName))
            .findFirst();
    return defaultVolumeObject.orElse(null);
  }

  public Volume getVolumeByHashKey(String hashKey) {
    String volumeId = decodeVolumeId(hashKey);
    return volumes.get(volumeId);
  }

  @Override
  public SecurityConstraints getSecurityConstraints() {
    return this.securityConstraints;
  }

  void setSecurityConstraints(SecurityConstraints securityConstraints) {
    this.securityConstraints = securityConstraints;
  }

  private String decodeVolumeId(String hashKey) {
    int volumeSeparatorIndex = hashKey.indexOf(SEPARATOR);
    if (volumeSeparatorIndex > -1) {
      hashKey = hashKey.substring(0, volumeSeparatorIndex);
    }
    return hashKey;
  }
}
