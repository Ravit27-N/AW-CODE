package com.allweb.rms.core.storage.properties;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "application.storage")
public class StorageConfigurationProperties {

  private String alias;

  private String defaultVolumeName;

  private List<VolumeProperties> volumes = new ArrayList<>();

  public static class VolumeProperties {
    @Getter @Setter private String alias;

    @Getter @Setter private Path path;

    @Getter @Setter private List<StorageObjectProperties> storageObjects;

    @Getter
    @Setter
    public static class StorageObjectProperties {
      private String alias;
      private String mime;
      private Path path;
      private SecurityConstraintProperties securityConstraints;
    }

    @Getter
    @Setter
    public static class SecurityConstraintProperties {
      private boolean locked = false;
      private boolean readable = true;
      private boolean writable = true;
    }
  }
}
