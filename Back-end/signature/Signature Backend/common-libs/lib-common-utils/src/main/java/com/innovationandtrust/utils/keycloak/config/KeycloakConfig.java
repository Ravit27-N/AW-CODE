package com.innovationandtrust.utils.keycloak.config;

import com.innovationandtrust.utils.keycloak.provider.IKeycloakProvider;
import com.innovationandtrust.utils.keycloak.provider.IKeycloakTokenExchange;
import com.innovationandtrust.utils.keycloak.provider.impl.KeycloakProvider;
import com.innovationandtrust.utils.keycloak.provider.impl.KeycloakTokenExchangeProvider;
import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.client.RestTemplate;

public class KeycloakConfig {
  private final KeycloakProperties properties;

  public KeycloakConfig(KeycloakProperties properties) {
    this.properties = properties;
  }

  @Lazy
  @Bean
  public Keycloak getInstance() {
    return KeycloakBuilder.builder()
        .serverUrl(properties.getAuthServerUrl())
        .realm(properties.getRealm())
        .clientId(properties.getResource())
        .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
        .clientSecret(properties.getSecret())
        .resteasyClient(new ResteasyClientBuilderImpl().connectionPoolSize(10).build())
        .build();
  }

  @Lazy
  @Bean
  public IKeycloakProvider provider(Keycloak keycloak, KeycloakProperties properties) {
    return new KeycloakProvider(keycloak, properties.getRealm(), properties);
  }

  @Lazy
  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }

  @Lazy
  @Bean
  public IKeycloakTokenExchange tokenExchange(
      Keycloak keycloak, RestTemplate restTemplate, KeycloakProperties properties) {
    return new KeycloakTokenExchangeProvider(keycloak, restTemplate, properties);
  }
}
