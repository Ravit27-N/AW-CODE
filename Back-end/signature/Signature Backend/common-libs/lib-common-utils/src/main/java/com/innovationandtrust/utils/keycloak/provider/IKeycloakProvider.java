package com.innovationandtrust.utils.keycloak.provider;

import com.innovationandtrust.utils.keycloak.model.KeycloakRoleResponse;
import com.innovationandtrust.utils.keycloak.model.KeycloakUserRequest;
import com.innovationandtrust.utils.keycloak.model.KeycloakUserResponse;
import com.innovationandtrust.utils.keycloak.model.ResetPasswordRequest;
import com.innovationandtrust.utils.keycloak.model.UserInfo;
import java.util.List;
import java.util.Optional;

public interface IKeycloakProvider {
  KeycloakUserResponse createUser(KeycloakUserRequest userKeycloak);

  void updateUser(KeycloakUserRequest userRequest);

  void resetPassword(ResetPasswordRequest userKeycloak);

  List<KeycloakUserResponse> getUsers();

  Optional<KeycloakUserResponse> getUserInfo(String userId);

  void assignRealmRoleToUser(String userId, List<String> roles);

  KeycloakRoleResponse addRealmRole(String newRoleName);

  List<KeycloakRoleResponse> getRoles();

  Optional<KeycloakRoleResponse> getRoleInfo(String name);

  void deleteUser(String userId);

  void isActive(KeycloakUserRequest userRequest);

  void setUserAttributes(String keycloakUserId, UserInfo userInfo);

  boolean isValidPassword(String username, String currentPassword);

  void updateUserEmail(KeycloakUserRequest userRequest);
}
