package com.allweb.rms.controller;

import com.allweb.rms.entity.dto.UserRole;
import com.allweb.rms.service.UserRoleService;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user/role")
public class UserRoleController {

  private final UserRoleService userRoleService;

  @Autowired
  public UserRoleController(UserRoleService userRoleService) {
    this.userRoleService = userRoleService;
  }

  @Operation(
      operationId = "createUserRole",
      description = "create a new user role",
      tags = {"Role"})
  @PostMapping
  public ResponseEntity<Void> createUserRole(@RequestBody @Valid UserRole userRole) {
    userRoleService.createRole(userRole);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Operation(
      operationId = "updateUserRole",
      description = "update a user role",
      tags = {"Role"},
      parameters = {
        @Parameter(
            in = ParameterIn.PATH,
            name = "roleName",
            description = "role name before update or old role name. Not use id for update!")
      })
  @PutMapping("{roleName}")
  public ResponseEntity<Void> updateUserRole(
      @RequestBody @Valid UserRole userRole, @PathVariable String roleName) {
    userRoleService.updateRole(userRole, roleName);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Operation(
      operationId = "getAllUserRoles",
      description = "get all user role",
      tags = {"Role"},
      parameters = {
        @Parameter(in = ParameterIn.QUERY, name = "page", description = "page index"),
        @Parameter(
            in = ParameterIn.QUERY,
            name = "pageSize",
            description = "size of page or limit"),
        @Parameter(in = ParameterIn.QUERY, name = "filter", description = "Filter on the table")
      })
  @GetMapping
  public ResponseEntity<EntityResponseHandler<UserRole>> getAllUserRoles(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int pageSize,
      @RequestParam(required = false) String filter) {
    return new ResponseEntity<>(
        userRoleService.getAllUserRoles(page, pageSize, filter), HttpStatus.OK);
  }

  @Operation(
      operationId = "findUserRoleByName",
      description = "find a user role by name",
      tags = {"Role"},
      parameters = {
        @Parameter(in = ParameterIn.PATH, name = "roleName", description = "user role name")
      })
  @GetMapping("/{roleName}")
  public ResponseEntity<UserRole> findUserRoleByName(@PathVariable String roleName) {
    return new ResponseEntity<>(userRoleService.findUserRoleByName(roleName), HttpStatus.OK);
  }

  @Operation(
      operationId = "deleteUserRole",
      description = "delete user role by role name",
      tags = {"Role"},
      parameters = {
        @Parameter(in = ParameterIn.PATH, name = "roleName", description = "user role name")
      })
  @DeleteMapping("/{roleName}")
  public ResponseEntity<Void> deleteUserRole(@PathVariable String roleName) {
    userRoleService.deleteUserRole(roleName);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
