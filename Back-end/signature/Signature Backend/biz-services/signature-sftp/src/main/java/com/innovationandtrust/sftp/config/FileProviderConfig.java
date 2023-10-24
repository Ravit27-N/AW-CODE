package com.innovationandtrust.sftp.config;

import com.innovationandtrust.utils.file.config.FileProperties;
import com.innovationandtrust.utils.file.config.FileServiceConfigurer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(value = {FileProperties.class})
public class FileProviderConfig extends FileServiceConfigurer {

  public FileProviderConfig(FileProperties fileProperties) {
    super(fileProperties);
  }
}
