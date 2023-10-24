package com.innovationandtrust.configuration.document;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "signature.swagger.info")
@Setter
public class SwaggerProperty {
  private String securityKey;
  private String description;
  private String title;
  private String version;
  private String termOfService;
  private String email;
  private String url;
  private String name;
}
