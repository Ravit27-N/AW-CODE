package com.innovationandtrust.utils.keycloak.provider.impl;

import com.innovationandtrust.utils.keycloak.config.KeycloakProperties;
import com.innovationandtrust.utils.keycloak.constant.KeycloakUserConstant;
import com.innovationandtrust.utils.keycloak.exception.KeycloakException;
import com.innovationandtrust.utils.keycloak.mapper.KeycloakMappingHandler;
import com.innovationandtrust.utils.keycloak.model.KeycloakRoleResponse;
import com.innovationandtrust.utils.keycloak.model.KeycloakUserRequest;
import com.innovationandtrust.utils.keycloak.model.KeycloakUserResponse;
import com.innovationandtrust.utils.keycloak.model.ResetPasswordRequest;
import com.innovationandtrust.utils.keycloak.model.UserInfo;
import com.innovationandtrust.utils.keycloak.provider.IKeycloakProvider;
import jakarta.ws.rs.NotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.*;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

@Slf4j
public class KeycloakProvider implements IKeycloakProvider {

  private static final String DEFAULT_MESSAGE = "Keycloak service exception message: ";
  private final RealmResource realmResource;

  private final KeycloakProperties properties;

  public KeycloakProvider(Keycloak keycloak, String keycloakRealm, KeycloakProperties properties) {
    this.properties = properties;
    this.realmResource = keycloak.realm(keycloakRealm);
  }

  /**
   * To create a new user in keycloak.
   *
   * @param userRequest refers to an object for create a keycloak user
   * @return an object of {@link KeycloakUserResponse} after completed action
   */
  @Override
  public KeycloakUserResponse createUser(KeycloakUserRequest userRequest) {
    var user = KeycloakMappingHandler.toUserRepresentation(userRequest);
    if (StringUtils.hasText(userRequest.getPassword())) {
      var credential = KeycloakMappingHandler.toCredentialRepresentation(userRequest);
      user.setCredentials(Collections.singletonList(credential));
    }

    user.setEnabled(userRequest.isActive());
    try (var response = this.realmResource.users().create(user)) {
      switch (response.getStatus()) {
        case HttpResponseCodes.SC_CONFLICT -> {
          log.warn("The user creation already exists in Keycloak.");
          return this.realmResource.users().search(user.getEmail()).stream()
              .findFirst()
              .map(KeycloakUserResponse::new)
              .orElseThrow(() -> new KeycloakException("Could not create a user."));
        }
        case HttpResponseCodes.SC_BAD_REQUEST -> {
          log.error(DEFAULT_MESSAGE + response.getStatusInfo().getReasonPhrase() + ".");
          throw new KeycloakException("Bad request!");
        }
        case HttpResponseCodes.SC_FORBIDDEN -> {
          log.error(
              DEFAULT_MESSAGE
                  + response.getStatusInfo().getReasonPhrase()
                  + "."
                  + "Please assign `Service accounts roles` with `realm-admin` to the client!");
          throw new KeycloakException(
              "Access denied! Please check role assign of configured client!");
        }
        case HttpResponseCodes.SC_CREATED, HttpResponseCodes.SC_OK -> {
          var createdUser =
              this.realmResource
                  .users()
                  .get(CreatedResponseUtil.getCreatedId(response))
                  .toRepresentation();
          this.assignRealmRoleToUser(createdUser.getId(), userRequest.getRoles());
          return new KeycloakUserResponse(createdUser);
        }
        default -> {
          log.error(DEFAULT_MESSAGE + response.getStatusInfo().getReasonPhrase() + ".");
          throw new KeycloakException("Could not create a user.");
        }
      }
    }
  }

  /**
   * To updating a user in keycloak.
   *
   * @param userRequest refers to an object of {@link KeycloakUserRequest}
   */
  @Override
  public void updateUser(KeycloakUserRequest userRequest) {
    try {
      UserResource userResource = this.realmResource.users().get(userRequest.getId());
      var userRepresentation = userResource.toRepresentation();

      userRepresentation.setFirstName(userRequest.getFirstName());
      userRepresentation.setLastName(userRequest.getLastName());

      // re-assign roles to a user
      this.assignRealmRoleToUser(userRequest.getId(), userRequest.getRoles());
      userResource.update(userRepresentation);
    } catch (Exception exception) {
      log.error("Failed to update a user in keycloak", exception);
      throw new KeycloakException(
          "Unable to update a user in keycloak : "
              + userRequest.getId()
              + ", "
              + exception.getMessage());
    }
  }

  private Optional<UserRepresentation> findUserById(String userId) {
    try {
      return Optional.of(this.realmResource.users().get(userId).toRepresentation());
    } catch (Exception exception) {
      log.error("Unable to load user from keycloak", exception);
      return Optional.empty();
    }
  }

  /**
   * To retrieve a user from keycloak by identity of a user.
   *
   * @param userId refers the uuid of keycloak user
   * @return object of {@link UserRepresentation}
   */
  @Override
  public Optional<KeycloakUserResponse> getUserInfo(String userId) {
    return this.findUserById(userId).map(KeycloakUserResponse::new);
  }

  @Override
  public List<KeycloakUserResponse> getUsers() {
    return usersResource().list().stream().map(KeycloakUserResponse::new).toList();
  }

  @Override
  public void resetPassword(ResetPasswordRequest userKeycloak) {
    try {
      this.usersResource()
          .get(userKeycloak.getId())
          .resetPassword(KeycloakMappingHandler.toCredentialRepresentation(userKeycloak));
    } catch (Exception exception) {
      log.error("Failed to reset password", exception);
      throw new KeycloakException(
          "There is no result for " + userKeycloak.getId(), HttpStatus.BAD_REQUEST);
    }
  }

  @Override
  public void deleteUser(String userId) {
    try (var response = this.realmResource.users().delete(userId)) {
      log.info(
          "Keycloak user with id : {} successful deleted. Response status ", response.getStatus());
    }
  }

  @Override
  public void isActive(KeycloakUserRequest userRequest) {
    try {
      var userResource = this.realmResource.users().get(userRequest.getId());
      var userRepresentation = userResource.toRepresentation();

      userRepresentation.setEnabled(userRequest.isActive());

      userResource.update(userRepresentation);
    } catch (Exception e) {
      log.error("Failed to update status a user in keycloak", e);
      throw new KeycloakException("Unable to update status a user : " + userRequest.getId());
    }
  }

  @Override
  public void assignRealmRoleToUser(String userId, List<String> roles) {
    this.userResource(userId)
        .roles()
        .realmLevel()
        .add(
            roles.stream()
                .map(
                    role -> {
                      try {
                        return rolesResource().get(role).toRepresentation();
                      } catch (NotFoundException exception) {
                        this.addRealmRole(role);
                      }
                      return rolesResource().get(role).toRepresentation();
                    })
                .toList());
  }

  @Override
  public KeycloakRoleResponse addRealmRole(String newRoleName) {
    RolesResource roleScopeResource = this.realmResource.roles();
    if (!this.getRoles().stream()
        .map(KeycloakRoleResponse::getName)
        .toList()
        .contains(newRoleName)) {
      RoleRepresentation roleRepresentation = new RoleRepresentation();
      roleRepresentation.setName(newRoleName);
      roleScopeResource.create(roleRepresentation);
    }
    return new KeycloakRoleResponse(roleScopeResource.get(newRoleName).toRepresentation());
  }

  @Override
  public List<KeycloakRoleResponse> getRoles() {
    return this.realmResource.roles().list().stream().map(KeycloakRoleResponse::new).toList();
  }

  @Override
  public Optional<KeycloakRoleResponse> getRoleInfo(String name) {
    try {
      return Optional.of(
          new KeycloakRoleResponse(this.realmResource.roles().get(name).toRepresentation()));
    } catch (NotFoundException ex) {
      log.warn(ex.getMessage());
      return Optional.empty();
    }
  }

  private RolesResource rolesResource() {
    return this.realmResource.roles();
  }

  private UsersResource usersResource() {
    return this.realmResource.users();
  }

  private UserResource userResource(String userId) {
    return this.realmResource.users().get(userId);
  }

  @Override
  public void setUserAttributes(String keycloakUserId, UserInfo userInfo) {
    try {
      var userResource = this.realmResource.users().get(keycloakUserId);
      var user = userResource.toRepresentation();
      user.setAttributes(
          Map.of(
              KeycloakUserConstant.USER_ATTRIBUTE_IDENTITY,
              Collections.singletonList(userInfo.getUserId().toString()),
              KeycloakUserConstant.FIRST_LOGIN,
              Collections.singletonList(String.valueOf(userInfo.isFirstLogin())),
              KeycloakUserConstant.USER_COMPANY,
              Objects.nonNull(userInfo.getCompany())
                  ? Collections.singletonList(userInfo.getCompany().getStringCompany())
                  : new ArrayList<>(),
              KeycloakUserConstant.CORPORATE_UUID,
              StringUtils.hasText(userInfo.getCorporateId())
                  ? Collections.singletonList(userInfo.getCorporateId())
                  : Collections.emptyList()));
      userResource.update(user);
    } catch (Exception exception) {
      log.error("Failed to update a user in keycloak", exception);
      throw new KeycloakException("Unable to update a user : " + keycloakUserId);
    }
  }

  /**
   * To validate current password of user.
   *
   * @param username is the user unique username
   * @param currentPassword password to login
   */
  @Override
  public boolean isValidPassword(String username, String currentPassword) {
    try (var keycloak =
        KeycloakBuilder.builder()
            .serverUrl(properties.getAuthServerUrl())
            .realm(properties.getRealm())
            .clientId(properties.getResource())
            .clientSecret(properties.getSecret())
            .username(username)
            .password(currentPassword)
            .grantType(OAuth2Constants.PASSWORD)
            .build()) {
      keycloak.tokenManager().getAccessTokenString();
      return true;
    } catch (Exception e) {
      log.error("Invalid Username or Password", e);
      return false;
    }
  }

  @Override
  public void updateUserEmail(KeycloakUserRequest userRequest) {
    try {
      UserResource userResource = this.realmResource.users().get(userRequest.getId());
      var userRepresentation = userResource.toRepresentation();
      userRepresentation.setEmail(userRequest.getEmail());

      userResource.update(userRepresentation);
    } catch (Exception exception) {
      log.error("Failed to update a user email in keycloak", exception);
      throw new KeycloakException(
          "Unable to update a user email in keycloak : "
              + userRequest.getId()
              + ", "
              + exception.getMessage());
    }
  }
}
