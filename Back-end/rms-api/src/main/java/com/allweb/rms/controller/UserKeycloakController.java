package com.allweb.rms.controller;

import com.allweb.rms.entity.dto.Group;
import com.allweb.rms.entity.dto.PasswordCredential;
import com.allweb.rms.entity.dto.UserKeycloak;
import com.allweb.rms.entity.dto.UserRole;
import com.allweb.rms.service.UserKeycloakService;
import com.allweb.rms.utils.EntityResponseHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/user")
public class UserKeycloakController {

  private final UserKeycloakService userKeycloakService;

  @Autowired
  public UserKeycloakController(UserKeycloakService userKeycloakService) {
    this.userKeycloakService = userKeycloakService;
  }

  @Operation(
      operationId = "createUser",
      description = "create a new user keycloak",
      tags = {"User"})
  @PostMapping
  public ResponseEntity<Void> createUser(@RequestBody @Valid UserKeycloak userKeycloak) {
    userKeycloakService.createUser(userKeycloak);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Operation(
      operationId = "updateUser",
      description = "update a new user keycloak",
      tags = {"User"})
  @PutMapping
  public ResponseEntity<Void> updateUser(@RequestBody @Valid UserKeycloak userKeycloak) {
    userKeycloakService.updateUser(userKeycloak);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Operation(
      operationId = "resetPassword",
      description = "reset password user on keycloak",
      tags = {"User"},
      parameters = {@Parameter(in = ParameterIn.PATH, name = "userId")})
  @PutMapping("/{userId}/password")
  public ResponseEntity<Void> resetPassword(
      @PathVariable String userId, @RequestBody PasswordCredential passwordCredential) {
    userKeycloakService.resetPassword(userId, passwordCredential);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Operation(
      operationId = "findAllUsers",
      description = "get all user from keycloak",
      tags = {"User"},
      parameters = {
        @Parameter(in = ParameterIn.QUERY, name = "page", description = "page index"),
        @Parameter(
            in = ParameterIn.QUERY,
            name = "pageSize",
            description = "size of page or limit"),
        @Parameter(in = ParameterIn.QUERY, name = "filter", description = "Filter on the table")
      })
  @GetMapping
  public ResponseEntity<EntityResponseHandler<UserKeycloak>> findAllUsers(
      @RequestParam(defaultValue = "1", required = false) int page,
      @RequestParam(defaultValue = "10", required = false) int pageSize,
      @RequestParam(required = false) String filter) {
    return new ResponseEntity<>(
        userKeycloakService.findAllUsers(page, pageSize, filter), HttpStatus.OK);
  }

  @Operation(
      operationId = "findUserById",
      description = "find user on keycloak by id",
      tags = {"User"},
      parameters = {@Parameter(in = ParameterIn.PATH, name = "id")})
  @GetMapping("/{id}")
  public ResponseEntity<UserKeycloak> findUserById(@PathVariable String id) {
    return new ResponseEntity<>(userKeycloakService.findUserById(id), HttpStatus.OK);
  }

  @Operation(
      operationId = "deleteUserById",
      description = "delete a user on keycloak by id",
      tags = {"User"},
      parameters = {@Parameter(in = ParameterIn.PATH, name = "id")})
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteUserById(@PathVariable String id) {
    userKeycloakService.deleteUser(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Operation(
      operationId = "updateEnabled",
      description = "update enabled a user on keycloak by id",
      tags = {"User"},
      parameters = {
        @Parameter(in = ParameterIn.PATH, name = "userId"),
        @Parameter(in = ParameterIn.PATH, name = "enabled"),
      })
  @PatchMapping("/{userId}/enabled/{enabled}")
  public ResponseEntity<Void> updateEnabled(
      @PathVariable String userId, @PathVariable boolean enabled) {
    userKeycloakService.updateEnabled(userId, enabled);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Operation(
      operationId = "joinGroup",
      description = "join group user by id",
      tags = {"User"},
      parameters = {
        @Parameter(in = ParameterIn.PATH, name = "id"),
        @Parameter(in = ParameterIn.PATH, name = "groupId"),
      })
  @PutMapping("/{id}/groups/{groupId}")
  public ResponseEntity<Void> joinGroup(@PathVariable String id, @PathVariable String groupId) {
    userKeycloakService.joinGroup(id, groupId);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Operation(
      operationId = "leaveGroup",
      description = "leave group user by id",
      tags = {"User"},
      parameters = {
        @Parameter(in = ParameterIn.PATH, name = "id"),
        @Parameter(in = ParameterIn.PATH, name = "groupId"),
      })
  @DeleteMapping("/{id}/groups/{groupId}")
  public ResponseEntity<Void> leaveGroup(@PathVariable String id, @PathVariable String groupId) {
    userKeycloakService.leaveGroup(id, groupId);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Operation(
      operationId = "joinGroupByUserName",
      description = "join group user by userName",
      tags = {"User"},
      parameters = {
        @Parameter(in = ParameterIn.PATH, name = "userName"),
        @Parameter(in = ParameterIn.PATH, name = "groupId"),
      })
  @PutMapping("/username/{userName}/groups/{groupId}")
  public ResponseEntity<Void> joinGroupByUserName(
      @PathVariable String userName, @PathVariable String groupId) {
    userKeycloakService.joinGroupByUserName(userName, groupId);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Operation(
      operationId = "leaveGroupByUserName",
      description = "leave group user by userName",
      tags = {"User"},
      parameters = {
        @Parameter(in = ParameterIn.PATH, name = "userName"),
        @Parameter(in = ParameterIn.PATH, name = "groupId"),
      })
  @DeleteMapping("/username/{userName}/groups/{groupId}")
  public ResponseEntity<Void> leaveGroupByUserName(
      @PathVariable String userName, @PathVariable String groupId) {
    userKeycloakService.leaveGroupByUserName(userName, groupId);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Operation(
      operationId = "groups",
      description = "join group user by id",
      tags = {"User"},
      parameters = {@Parameter(in = ParameterIn.PATH, name = "id")})
  @GetMapping("/{id}/groups")
  public ResponseEntity<EntityResponseHandler<Group>> groups(@PathVariable String id) {
    return new ResponseEntity<>(userKeycloakService.groups(id), HttpStatus.OK);
  }

  @Operation(
      operationId = "addClientRole",
      description = "add role to user",
      tags = {"User"},
      parameters = {
        @Parameter(in = ParameterIn.PATH, name = "id", description = "user id"),
        @Parameter(in = ParameterIn.PATH, name = "roleName", description = "role name")
      })
  @PostMapping("/{id}/role/{roleName}")
  public ResponseEntity<Void> addClientRole(
      @PathVariable String id, @PathVariable String roleName) {
    userKeycloakService.addClientRole(id, roleName);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Operation(
      operationId = "removeClientRole",
      description = "remove role from user",
      tags = {"User"},
      parameters = {
        @Parameter(in = ParameterIn.PATH, name = "id", description = "user id"),
        @Parameter(in = ParameterIn.PATH, name = "roleName", description = "role name")
      })
  @DeleteMapping("/{id}/role/{roleName}")
  public ResponseEntity<Void> removeClientRole(
      @PathVariable String id, @PathVariable String roleName) {
    userKeycloakService.removeClientRole(id, roleName);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Operation(
      operationId = "getClientRoleByUserId",
      description = "get client role or role by user id",
      tags = {"User"},
      parameters = {@Parameter(in = ParameterIn.PATH, name = "id", description = "user id")})
  @GetMapping("/{id}/roles")
  public ResponseEntity<EntityResponseHandler<UserRole>> getClientRoleByUserId(
      @PathVariable String id) {
    return new ResponseEntity<>(userKeycloakService.getClientRoleByUserId(id), HttpStatus.OK);
  }

  @Operation(
      operationId = "getDetailUser",
      description = "detail information of user",
      tags = {"User"},
      parameters = {
        @Parameter(in = ParameterIn.PATH, name = "userName", description = "user name")
      })
  @GetMapping("/{userName}/detail")
  public ResponseEntity<UserRole> getDetailUser(@PathVariable String userName) {
    return new ResponseEntity<>(userKeycloakService.getDetailUser(userName), HttpStatus.OK);
  }
}
