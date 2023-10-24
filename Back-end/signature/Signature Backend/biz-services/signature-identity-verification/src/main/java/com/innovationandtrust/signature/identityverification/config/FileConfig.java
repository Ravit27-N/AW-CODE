package com.innovationandtrust.signature.identityverification.config;

import com.innovationandtrust.utils.file.config.FileProperties;
import com.innovationandtrust.utils.file.config.FileServiceConfigurer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/** Configuration class for file service. */
@EnableConfigurationProperties(FileProperties.class)
@Configuration
public class FileConfig extends FileServiceConfigurer {

  public FileConfig(FileProperties fileProperties) {
    super(fileProperties);
  }
}
