package com.allweb.shared.service.keycloak;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@ConfigurationProperties(prefix = "keycloak.java-admin", ignoreInvalidFields = true)
public class KeycloakAdminClientProperties {

  @NotEmpty
  @NotNull
  private String severUrl;

  @NotEmpty
  @NotNull
  private String realm;

  @NotEmpty
  @NotNull
  private String clientId;

  @NotEmpty
  @NotNull
  private String clientSecret;
}