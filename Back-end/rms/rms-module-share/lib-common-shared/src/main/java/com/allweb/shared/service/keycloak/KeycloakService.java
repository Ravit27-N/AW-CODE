package com.allweb.shared.service.keycloak;

import org.keycloak.admin.client.resource.GroupsResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UsersResource;

public interface KeycloakService {
  GroupsResource getGroupResource();
  RolesResource getRoleResource();
  UsersResource getUserResource();
}