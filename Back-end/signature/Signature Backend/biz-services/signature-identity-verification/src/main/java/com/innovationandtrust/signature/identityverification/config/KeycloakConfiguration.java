package com.innovationandtrust.signature.identityverification.config;

import com.innovationandtrust.utils.keycloak.config.KeycloakConfig;
import com.innovationandtrust.utils.keycloak.config.KeycloakProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/** Configuration class for Keycloak. */
@Configuration
@EnableConfigurationProperties(value = KeycloakProperties.class)
public class KeycloakConfiguration extends KeycloakConfig {

  public KeycloakConfiguration(final KeycloakProperties properties) {
    super(properties);
  }
}
