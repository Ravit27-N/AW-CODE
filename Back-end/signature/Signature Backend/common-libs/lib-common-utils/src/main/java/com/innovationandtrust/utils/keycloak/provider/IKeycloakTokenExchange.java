package com.innovationandtrust.utils.keycloak.provider;

public interface IKeycloakTokenExchange {

  String getToken(String uuid);

  String getToken();
  String getTokenTechnicalUser();

  Object introspectToken(String token);
}
