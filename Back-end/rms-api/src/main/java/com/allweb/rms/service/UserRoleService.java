package com.allweb.rms.service;

import com.allweb.rms.entity.dto.UserRole;
import com.allweb.rms.entity.jpa.UserRoleDetail;
import com.allweb.rms.exception.UserRoleConflictException;
import com.allweb.rms.exception.UserRoleNotFoundException;
import com.allweb.rms.repository.jpa.UserRoleDetailRepository;
import com.allweb.rms.utils.EntityResponseHandler;
import com.allweb.shared.service.keycloak.KeycloakService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import java.util.List;
import javax.transaction.Transactional;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.NotFoundException;
import lombok.extern.log4j.Log4j2;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Transactional
@Log4j2
public class UserRoleService {

//  private final Keycloak keycloak;
  private final ModelMapper modelMapper;
  private final UserRoleDetailRepository userRoleDetailRepository;
  private final ObjectMapper objectMapper;

//  @Value("${keycloak.realm_name}")
//  private String realmName;
//
//  @Value("${keycloak.client_id}")
//  private String clientId;

  private final KeycloakService keycloakService;

  @Autowired
  public UserRoleService(
      ModelMapper modelMapper,
      UserRoleDetailRepository userRoleDetailRepository,
      ObjectMapper objectMapper, KeycloakService keycloakService) {
    this.modelMapper = modelMapper;
    this.userRoleDetailRepository = userRoleDetailRepository;
    this.objectMapper = objectMapper;
    this.keycloakService = keycloakService;
  }

  public void createRole(UserRole userRole) {
    try {
      RoleRepresentation kcRole = modelMapper.map(userRole, RoleRepresentation.class);
      rolesResource().create(kcRole);
      userRoleDetailRepository.saveAll(
          objectMapper.readValue(
              userRole.getUserRoleDetails().toString(),
              new TypeReference<List<UserRoleDetail>>() {}));
    } catch (ClientErrorException | JsonProcessingException e) {
      throw new UserRoleConflictException("This name is already exist.");
    }
  }

  /**
   * get roles Resource
   *
   * @return
   */
  public RolesResource rolesResource() {
//    List<ClientRepresentation> clients =
//        keycloak.realm(realmName).clients().findByClientId(clientId);
//    if (!clients.isEmpty()) {
//      return keycloak.realm(realmName).clients().get(clients.get(0).getId()).roles();
//    }
//    return null;
    return this.keycloakService.getRoleResource();
  }

  public void updateRole(UserRole userRole, String roleName) {
    try {
      RoleRepresentation kcRole = modelMapper.map(userRole, RoleRepresentation.class);
      rolesResource().get(roleName).update(kcRole);
      userRoleDetailRepository.deleteUserRoleDetailByUserRoleId(roleName);
      userRoleDetailRepository.saveAll(
          objectMapper.readValue(
              userRole.getUserRoleDetails().toString(),
              new TypeReference<List<UserRoleDetail>>() {}));
    } catch (NotFoundException e) {
      throw new UserRoleNotFoundException(roleName);
    } catch (JsonProcessingException e) {
      log.error(e);
    }
  }

  @Transactional
  public EntityResponseHandler<UserRole> getAllUserRoles(int page, int pageSize, String filter) {
    int count;
    List<UserRole> response;

    if (Strings.isNullOrEmpty(filter)) {
      count = rolesResource().list(false).size();
      response =
          rolesResource().list((page - 1) * pageSize, pageSize, false).stream()
              .map(
                  role -> {
                    UserRole user = modelMapper.map(role, UserRole.class);
                    var userRoleDetails =
                        this.objectMapper.valueToTree(
                            userRoleDetailRepository.findByUserRoleId(user.getName()));
                    user.setUserRoleDetails(userRoleDetails);

                    return user;
                  })
              .toList();
    } else {
      count = rolesResource().list(filter, false).size();
      response =
          rolesResource().list(filter, (page - 1) * pageSize, pageSize, false).stream()
              .map(
                  role -> {
                    UserRole user = modelMapper.map(role, UserRole.class);
                    user.setUserRoleDetails(
                        this.objectMapper.valueToTree(
                            userRoleDetailRepository.findByUserRoleId(user.getName())));
                    return user;
                  })
              .toList();
    }

    return new EntityResponseHandler<>(response, page, pageSize, count);
  }

  @Transactional
  public UserRole findUserRoleByName(String roleName) {
    try {
      UserRole role =
          modelMapper.map(rolesResource().get(roleName).toRepresentation(), UserRole.class);
      role.setUserRoleDetails(
          this.objectMapper.valueToTree(userRoleDetailRepository.findByUserRoleId(roleName)));
      return role;
    } catch (NotFoundException e) {
      throw new UserRoleNotFoundException(roleName);
    }
  }

  public void deleteUserRole(String roleName) {
    try {
      rolesResource().deleteRole(roleName);
      userRoleDetailRepository.deleteUserRoleDetailByUserRoleId(roleName);
    } catch (NotFoundException e) {
      throw new UserRoleNotFoundException(roleName);
    }
  }
}
