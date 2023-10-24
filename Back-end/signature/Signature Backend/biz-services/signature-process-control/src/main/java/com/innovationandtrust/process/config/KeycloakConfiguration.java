package com.innovationandtrust.process.config;

import com.innovationandtrust.utils.keycloak.config.KeycloakConfig;
import com.innovationandtrust.utils.keycloak.config.KeycloakProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(KeycloakProperties.class)
public class KeycloakConfiguration extends KeycloakConfig {
  public KeycloakConfiguration(KeycloakProperties properties) {
    super(properties);
  }
}
