package com.tessi.cxm.pfl.ms15.config;

import com.tessi.cxm.pfl.shared.document.BaseSwaggerConfig;
import com.tessi.cxm.pfl.shared.document.DocumentProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * To configure Swagger
 *
 * @author Sokhour LACH
 * @since 05/01/2022
 */
@Configuration
@EnableConfigurationProperties(DocumentProperties.class)
public class SwaggerConfig extends BaseSwaggerConfig {
  public SwaggerConfig(@Autowired DocumentProperties properties) {
    super(properties);
  }
}
