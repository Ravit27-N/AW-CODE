package com.allweb.rms.config;

import com.allweb.rms.core.storage.StorageFactory;
import com.allweb.rms.core.storage.commands.StorageCommandFactory;
import com.allweb.rms.core.storage.driver.filesystem.FileSystemStorageCommandFactory;
import com.allweb.rms.core.storage.driver.filesystem.FileSystemStorageFactory;
import com.allweb.rms.core.storage.properties.StorageConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({StorageConfigurationProperties.class})
public class StorageConfig {
  @Bean
  public StorageFactory storageFactory(StorageConfigurationProperties properties) {
    return new FileSystemStorageFactory(properties);
  }

  @Bean
  StorageCommandFactory storageCommandFactory() {
    return new FileSystemStorageCommandFactory();
  }
}
