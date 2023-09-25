package com.allweb.rms.service;

import com.allweb.rms.entity.dto.Group;
import com.allweb.rms.entity.dto.PasswordCredential;
import com.allweb.rms.entity.dto.UserKeycloak;
import com.allweb.rms.entity.dto.UserRole;
import com.allweb.rms.exception.UserKeycloakNotFoundException;
import com.allweb.rms.utils.EntityResponseHandler;
import com.allweb.shared.service.keycloak.KeycloakService;
import com.google.common.base.Strings;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.ws.rs.NotFoundException;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserKeycloakService {

  private final ModelMapper modelMapper;
  private final UserRoleService userRoleService;

//  @Value("${keycloak.realm_name}")
//  private String realmName;

  @Value("${keycloak.client_id}")
  private String clientId;

  @Value("${keycloak.client_uuid}")
  private String clientUUID;

  private final KeycloakService keycloakService;

  @Autowired
  public UserKeycloakService(
      ModelMapper modelMapper, UserRoleService userRoleService,
      KeycloakService keycloakService) {
    this.modelMapper = modelMapper;
    this.userRoleService = userRoleService;
    this.keycloakService = keycloakService;
  }

  /**
   * get UserResource
   *
   * @return
   */
  public UsersResource usersResource() {
//    return keycloak.realm(realmName).users();
    return this.keycloakService.getUserResource();
  }

  public RolesResource rolesResource() {
//    keycloak.realm(realmName).clients().get(clientId);
//    ClientRepresentation client =
//        keycloak.realm(realmName).clients().findByClientId(clientId).get(0);
//    return keycloak.realm(realmName).clients().get(client.getId()).roles();
    return this.keycloakService.getRoleResource();
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
  public void createUser(UserKeycloak userKeycloak) {
    usersResource().create(modelMapper.map(userKeycloak, UserRepresentation.class));
  }

  /**
   * Update UserRepresentation
   *
   * @param userKeycloak
   */
  @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
  public void updateUser(UserKeycloak userKeycloak) {
    try {
      usersResource()
          .get(userKeycloak.getId())
          .update(modelMapper.map(userKeycloak, UserRepresentation.class));
    } catch (NotFoundException e) {
      throw new UserKeycloakNotFoundException(userKeycloak.getId());
    }
  }

  /**
   * Set password credential
   *
   * @param password
   * @return
   */
  @Transactional
  public CredentialRepresentation passwordCredential(PasswordCredential password) {
    CredentialRepresentation passwordCred = new CredentialRepresentation();
    passwordCred.setTemporary(password.isTemporary());
    passwordCred.setType(CredentialRepresentation.PASSWORD);
    passwordCred.setValue(password.getPassword());
    return passwordCred;
  }

  /**
   * Create password or update password
   *
   * @param userId
   * @param passwordCredential
   */
  @Transactional
  public void resetPassword(String userId, PasswordCredential passwordCredential) {
    try {
      usersResource().get(userId).resetPassword(passwordCredential(passwordCredential));
    } catch (NotFoundException e) {
      throw new UserKeycloakNotFoundException(userId);
    }
  }

  /**
   * Find all users from keycloak
   *
   * @param page
   * @param pageSize
   * @param filter
   * @return
   */
  @Transactional
  public EntityResponseHandler<UserKeycloak> findAllUsers(int page, int pageSize, String filter) {

    List<UserKeycloak> response;
    int count;

    if (Strings.isNullOrEmpty(filter)) {
      count = usersResource().count();
      response =
          usersResource().search(null, (page - 1) * pageSize, pageSize, true).stream()
              .map(user -> modelMapper.map(user, UserKeycloak.class))
              .collect(Collectors.toList());
    } else {
      count = usersResource().count(filter);
      response =
          usersResource().search(filter, (page - 1) * pageSize, pageSize, false).stream()
              .map(user -> modelMapper.map(user, UserKeycloak.class))
              .collect(Collectors.toList());
    }

    return new EntityResponseHandler<>(response, page, pageSize, count);
  }

  /**
   * Find a user by id
   *
   * @param userId
   * @return
   */
  @Transactional
  public UserKeycloak findUserById(String userId) {
    try {
      return modelMapper.map(usersResource().get(userId).toRepresentation(), UserKeycloak.class);
    } catch (NotFoundException ex) {
      throw new UserKeycloakNotFoundException(userId);
    }
  }

  /**
   * Delete a user by id
   *
   * @param id
   */
  @Modifying
  public void deleteUser(String id) {
    usersResource().delete(findUserById(id).getId());
  }

  /**
   * Update enabled of user by id.
   *
   * @param userId
   * @param isEnabled
   */
  public void updateEnabled(String userId, boolean isEnabled) {
    UserKeycloak kcUser = findUserById(userId);
    kcUser.setEnabled(isEnabled);
    usersResource().get(userId).update(modelMapper.map(kcUser, UserRepresentation.class));
  }

  /**
   * Join Group by userId
   *
   * @param id
   * @param groupId
   */
  public void joinGroup(String id, String groupId) {
    usersResource().get(id).joinGroup(groupId);
  }

  /**
   * LEAVE GROUP BY USER ID
   *
   * @param id
   * @param groupId
   */
  public void leaveGroup(String id, String groupId) {
    usersResource().get(id).leaveGroup(groupId);
  }

  /**
   * Join Group by userName
   *
   * @param userName
   * @param groupId
   */
  public void joinGroupByUserName(String userName, String groupId) {
    usersResource().get(usersResource().search(userName).get(0).getId()).joinGroup(groupId);
  }

  /**
   * Leave Group by userName
   *
   * @param userName
   * @param groupId
   */
  public void leaveGroupByUserName(String userName, String groupId) {
    usersResource().get(usersResource().search(userName).get(0).getId()).leaveGroup(groupId);
  }

  /**
   * Get all groups by userId
   *
   * @param id
   * @return
   */
  public EntityResponseHandler<Group> groups(String id) {
    return new EntityResponseHandler<>(
        usersResource().get(id).groups().stream()
            .map(g -> modelMapper.map(g, Group.class))
            .collect(Collectors.toList()));
  }

  public void addClientRole(String id, String roleName) {
    usersResource()
        .get(id)
        .roles()
        .clientLevel(clientUUID)
        .add(Collections.singletonList(rolesResource().get(roleName).toRepresentation()));
  }

  public void removeClientRole(String id, String roleName) {
    usersResource()
        .get(id)
        .roles()
        .clientLevel(clientUUID)
        .remove(Collections.singletonList(rolesResource().get(roleName).toRepresentation()));
  }

  public EntityResponseHandler<UserRole> getClientRoleByUserId(String id) {
    return new EntityResponseHandler<>(
        usersResource().get(id).roles().clientLevel(clientUUID).listAll().stream()
            .map(r -> modelMapper.map(r, UserRole.class))
            .collect(Collectors.toList()));
  }

  public UserRole getDetailUser(String userName) {
    String roleName =
        usersResource()
            .get(usersResource().search(userName).get(0).getId())
            .groups(null, null, false)
            .get(0)
            .getClientRoles()
            .get(clientId)
            .get(0);
    return userRoleService.findUserRoleByName(roleName);
  }
}
