package com.allweb.shared.service.keycloak;

import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.GroupsResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KeycloakServiceImp implements KeycloakService {

  private final String keycloakServerUrl;
  private final String keycloakRealm;
  private final String clientId;
  private final String clientSecret;
  private Keycloak keycloak;

  public KeycloakServiceImp(@Autowired KeycloakAdminClientProperties props) {
    this.keycloakServerUrl = props.getSeverUrl();
    this.keycloakRealm = props.getRealm();
    this.clientId = props.getClientId();
    this.clientSecret = props.getClientSecret();
  }

  public Keycloak getInstance() {
    if (keycloak == null) {
      keycloak = KeycloakBuilder.builder()
          .serverUrl(this.keycloakServerUrl)
          .realm(keycloakRealm)
          .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
          .clientId(clientId)
          .clientSecret(clientSecret)
          .resteasyClient(new ResteasyClientBuilderImpl().connectionPoolSize(10).build())
          .build();
    }

    return keycloak;
  }

  @Override
  public GroupsResource getGroupResource() {
    return this.getInstance().realm(keycloakRealm).groups();
  }

  @Override
  public RolesResource getRoleResource() {
    return getInstance().realm(keycloakRealm).roles();
  }

  @Override
  public UsersResource getUserResource() {
    return getInstance().realm(keycloakRealm).users();
  }
}