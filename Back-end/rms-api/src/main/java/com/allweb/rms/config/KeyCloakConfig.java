//package com.allweb.rms.config;
//
//import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;
//import org.keycloak.OAuth2Constants;
//import org.keycloak.admin.client.Keycloak;
//import org.keycloak.admin.client.KeycloakBuilder;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class KeyCloakConfig {
//  @Value("${keycloak.realm_url}")
//  private String realmUrl;
//
//  @Value("${keycloak.realm_name}")
//  private String realm;
//
//  @Value("${keycloak.admin_client_id}")
//  private String clientId;
//
//  @Value("${keycloak.admin_client_secret}")
//  private String clientSecret;
//
//  @Bean
//  public Keycloak keycloak() {
//    return KeycloakBuilder.builder()
//        .serverUrl(realmUrl)
//        .realm(realm)
//        .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
//        .clientId(clientId)
//        .clientSecret(clientSecret)
////        .username("pisey")
////        .password("123")
//        .resteasyClient(new ResteasyClientBuilderImpl().connectionPoolSize(10).build())
//        .build();
//  }
//}
