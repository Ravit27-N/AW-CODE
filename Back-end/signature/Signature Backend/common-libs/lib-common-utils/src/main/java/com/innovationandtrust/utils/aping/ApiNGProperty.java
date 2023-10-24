package com.innovationandtrust.utils.aping;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties("signature.api-ng")
public class ApiNGProperty {

  @NotEmpty(message = "api-ng `url` property is required and cannot be empty")
  private String url;

  @NotEmpty(message = "api-ng `context-path` property is required and cannot be empty")
  private String contextPath;

  private String accessToken;

  @NotEmpty(message = "api-ng `certigna-user` property is required and cannot be empty")
  private String certignaUser;

  @NotEmpty(message = "api-ng `certigna-role` property is required and cannot be empty")
  private String certignaRole;

  @NotEmpty(message = "api-ng `certigna-hash` property is required and cannot be empty")
  private String certignaHash;

  @NotEmpty(message = "api-ng `default-language` property is required and cannot be empty")
  private String defaultLanguage;

  private String testFilePath;

  private String frontEndUrl;
}
