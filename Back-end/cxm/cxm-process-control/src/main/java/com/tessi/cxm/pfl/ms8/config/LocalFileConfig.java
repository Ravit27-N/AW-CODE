package com.tessi.cxm.pfl.ms8.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cxm.storage.local")
@Data
public class LocalFileConfig {
  private String path; // local path of file.
}
