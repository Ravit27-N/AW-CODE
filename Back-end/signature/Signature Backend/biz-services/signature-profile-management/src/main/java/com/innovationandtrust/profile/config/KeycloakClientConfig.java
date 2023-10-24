package com.innovationandtrust.profile.config;

import com.innovationandtrust.utils.keycloak.config.KeycloakConfig;
import com.innovationandtrust.utils.keycloak.config.KeycloakProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(value = {KeycloakProperties.class})
public class KeycloakClientConfig extends KeycloakConfig {
  public KeycloakClientConfig(final KeycloakProperties properties) {
    super(properties);
  }
}
