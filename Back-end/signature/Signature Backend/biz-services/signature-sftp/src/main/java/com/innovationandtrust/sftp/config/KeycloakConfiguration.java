package com.innovationandtrust.sftp.config;

import com.innovationandtrust.utils.keycloak.config.KeycloakConfig;
import com.innovationandtrust.utils.keycloak.config.KeycloakProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(KeycloakProperties.class)
public class KeycloakConfiguration extends KeycloakConfig {

  @Autowired
  public KeycloakConfiguration(final KeycloakProperties properties) {
    super(properties);
  }
}
