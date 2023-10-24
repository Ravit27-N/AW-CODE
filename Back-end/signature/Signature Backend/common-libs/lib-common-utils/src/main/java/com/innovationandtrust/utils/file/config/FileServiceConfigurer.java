package com.innovationandtrust.utils.file.config;

import com.innovationandtrust.utils.file.provider.FileProvider;
import jakarta.servlet.MultipartConfigElement;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.util.unit.DataSize;
import org.springframework.util.unit.DataUnit;

public class FileServiceConfigurer {

  private final String baseDirectory;

  private final long maxUploadSize;

  private final DataUnit dataUnit;

  public FileServiceConfigurer(FileProperties fileProperties) {
    this.baseDirectory = fileProperties.getBasePath();
    this.maxUploadSize = fileProperties.getMaxUploadSize();
    this.dataUnit = fileProperties.getDataUnit();
  }

  @Bean
  public MultipartConfigElement commonsMultipartResolver() {
    final MultipartConfigFactory factory = new MultipartConfigFactory();
    factory.setMaxFileSize(DataSize.of(maxUploadSize, dataUnit));
    factory.setMaxRequestSize(DataSize.of(maxUploadSize, dataUnit));
    return factory.createMultipartConfig();
  }

  @Bean
  public FileProvider fileProvider() {
    return new FileProvider(this.baseDirectory);
  }
}
