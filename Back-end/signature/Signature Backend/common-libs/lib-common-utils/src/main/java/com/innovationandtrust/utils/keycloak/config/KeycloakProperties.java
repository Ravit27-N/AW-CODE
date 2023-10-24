package com.innovationandtrust.utils.keycloak.config;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "signature.keycloak")
public class KeycloakProperties {
  @NotNull
  @NotEmpty
  private String authServerUrl;
  @NotNull
  @NotEmpty
  private String realm;
  @NotNull
  @NotEmpty
  private String resource;
  @NotNull
  @NotEmpty
  private String secret;
  @NotEmpty
  public String technicalUserId;
}
