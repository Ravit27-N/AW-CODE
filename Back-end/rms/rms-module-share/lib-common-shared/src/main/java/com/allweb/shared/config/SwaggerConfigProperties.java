package com.allweb.shared.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@ConfigurationProperties(prefix = "openapi.service", ignoreInvalidFields = true)
public class SwaggerConfigProperties {

  private String title;
  private String description;
  private String version;
  private String url; // api gateway url that access to this microservice.
}