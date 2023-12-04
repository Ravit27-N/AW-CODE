package com.innovationandtrust.process.config;

import com.innovationandtrust.utils.keycloak.config.KeycloakConfig;
import com.innovationandtrust.utils.keycloak.config.KeycloakProperties;
import com.innovationandtrust.utils.keycloak.provider.impl.KeycloakProvider;
import org.keycloak.admin.client.Keycloak;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
@EnableConfigurationProperties(KeycloakProperties.class)
public class KeycloakConfiguration extends KeycloakConfig {
  public KeycloakConfiguration(KeycloakProperties properties) {
    super(properties);
  }

  @Lazy
  @Bean
  public KeycloakProvider keycloakProvider(Keycloak keycloak, KeycloakProperties properties) {
    return new KeycloakProvider(keycloak, properties.getRealm(), properties);
  }
}
