package com.allweb.rms.service;

import com.allweb.rms.entity.dto.Group;
import com.allweb.rms.entity.dto.UserKeycloak;
import com.allweb.rms.exception.GroupNotFoundException;
import com.allweb.rms.utils.EntityResponseHandler;
import com.allweb.shared.service.keycloak.KeycloakService;
import com.google.api.gax.rpc.NotFoundException;
import com.google.common.base.Strings;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.GroupsResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.GroupRepresentation;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class GroupService {
  private final ModelMapper modelMapper;

//  @Value("${keycloak.realm_name}")
//  private String realmName;
//
//  @Value("${keycloak.client_id}")
//  private String clientId;

  @Value("${keycloak.client_uuid}")
  private String clientUUID;

  private final KeycloakService keycloakService;

  @Autowired
  public GroupService(ModelMapper modelMapper, KeycloakService keycloakService) {
    this.modelMapper = modelMapper;
    this.keycloakService = keycloakService;
  }

  /**
   * get group Resource
   *
   * @return
   */
  public GroupsResource groupsResource() {
//    return keycloak.realm(realmName).groups();
    return this.keycloakService.getGroupResource();
  }

  public RolesResource rolesResource() {
//    ClientRepresentation client =
//        keycloak.realm(realmName).clients().findByClientId(clientId).get(0);
//    return keycloak.realm(realmName).clients().get(client.getId()).roles();
    return this.keycloakService.getRoleResource();
  }

  private Group convertGroupRepresentationToGroup(GroupRepresentation group) {
    return modelMapper.map(group, Group.class);
  }

  public void createGroup(Group group) {
    GroupRepresentation kcGroup = new GroupRepresentation();
    kcGroup.setName(group.getName());
    groupsResource().add(kcGroup);
  }

  public void updateGroup(Group group) {
    try {
      GroupRepresentation kcGroup = modelMapper.map(group, GroupRepresentation.class);
      groupsResource().group(group.getId()).update(kcGroup);
    } catch (NotFoundException e) {
      throw new GroupNotFoundException(group.getId());
    }
  }

  public Group findGroupById(String id) {
    try {
      return convertGroupRepresentationToGroup(groupsResource().group(id).toRepresentation());
    } catch (NotFoundException e) {
      throw new GroupNotFoundException(id);
    }
  }

  public void deleteGroupById(String id) {
    try {
      groupsResource().group(id).remove();
    } catch (NotFoundException e) {
      throw new GroupNotFoundException(id);
    }
  }

  public EntityResponseHandler<Group> getAllGroups(int page, int pageSize, String filter) {
    Map<String, Long> count;
    List<Group> response;
    if (Strings.isNullOrEmpty(filter)) {
      count = groupsResource().count();
      response =
          groupsResource().groups(null, (page - 1) * pageSize, pageSize, false).stream()
              .map(this::convertGroupRepresentationToGroup)
              .collect(Collectors.toList());
    } else {
      count = groupsResource().count(filter);
      response =
          groupsResource().groups(filter, (page - 1) * pageSize, pageSize, false).stream()
              .map(this::convertGroupRepresentationToGroup)
              .collect(Collectors.toList());
    }

    return new EntityResponseHandler<>(response, page, pageSize, count.get("count"));
  }

  /**
   * View Members by id
   *
   * @param id
   * @return
   */
  public EntityResponseHandler<UserKeycloak> viewMembersByGroupId(String id) {
    return new EntityResponseHandler<>(
        groupsResource().group(id).members().stream()
            .map(u -> modelMapper.map(u, UserKeycloak.class))
            .collect(Collectors.toList()));
  }

  /**
   * Join client role into group
   *
   * @param id
   * @param userRole
   */
  public void addClientRole(String id, String userRole) {
    groupsResource()
        .group(id)
        .roles()
        .clientLevel(clientUUID)
        .add(Collections.singletonList(rolesResource().get(userRole).toRepresentation()));
  }

  public void removeClientRole(String id, String userRole) {
    groupsResource()
        .group(id)
        .roles()
        .clientLevel(clientUUID)
        .remove(Collections.singletonList(rolesResource().get(userRole).toRepresentation()));
  }
}
