package com.allweb.rms.controller;

import com.allweb.rms.entity.dto.Group;
import com.allweb.rms.entity.dto.UserKeycloak;
import com.allweb.rms.service.GroupService;
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
@RequestMapping("/api/v1/group")
public class GroupController {

  private final GroupService groupService;

  @Autowired
  public GroupController(GroupService groupService) {
    this.groupService = groupService;
  }

  @Operation(
      operationId = "createGroup",
      description = "create a new group",
      tags = {"Group"})
  @PostMapping
  public ResponseEntity<Void> createGroup(@RequestBody @Valid Group group) {
    groupService.createGroup(group);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Operation(
      operationId = "updateGroup",
      description = "update a group",
      tags = {"Group"})
  @PutMapping
  public ResponseEntity<Void> updateGroup(@RequestBody @Valid Group group) {
    groupService.updateGroup(group);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Operation(
      operationId = "findGroupById",
      description = "find a group by id",
      tags = {"Group"},
      parameters = {@Parameter(in = ParameterIn.PATH, name = "id", description = "group id")})
  @GetMapping("/{id}")
  public ResponseEntity<Group> findGroupById(@PathVariable String id) {
    return new ResponseEntity<>(groupService.findGroupById(id), HttpStatus.OK);
  }

  @Operation(
      operationId = "getAllGroups",
      description = "get all groups",
      tags = {"Group"},
      parameters = {
        @Parameter(in = ParameterIn.QUERY, name = "page", description = "page index"),
        @Parameter(
            in = ParameterIn.QUERY,
            name = "pageSize",
            description = "size of page or limit"),
        @Parameter(in = ParameterIn.QUERY, name = "filter", description = "Filter on the table")
      })
  @GetMapping
  public ResponseEntity<EntityResponseHandler<Group>> getAllGroups(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int pageSize,
      @RequestParam(required = false) String filter) {
    return new ResponseEntity<>(groupService.getAllGroups(page, pageSize, filter), HttpStatus.OK);
  }

  @Operation(
      operationId = "deleteGroupById",
      description = "delete a group by id",
      tags = {"Group"},
      parameters = {@Parameter(in = ParameterIn.PATH, name = "id", description = "group id")})
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteGroupById(@PathVariable String id) {
    groupService.deleteGroupById(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Operation(
      operationId = "viewMembersByGroupId",
      description = "view member in group by id",
      tags = {"Group"},
      parameters = {@Parameter(in = ParameterIn.PATH, name = "id", description = "group id")})
  @GetMapping("{id}/view/member")
  public ResponseEntity<EntityResponseHandler<UserKeycloak>> viewMembersByGroupId(
      @PathVariable String id) {
    return new ResponseEntity<>(groupService.viewMembersByGroupId(id), HttpStatus.OK);
  }

  @Operation(
      operationId = "addClientRole",
      description = "add role into group",
      tags = {"Group"},
      parameters = {
        @Parameter(in = ParameterIn.PATH, name = "id", description = "group id"),
        @Parameter(in = ParameterIn.PATH, name = "roleName", description = "role name")
      })
  @PostMapping("/{id}/client-role/{roleName}")
  public ResponseEntity<Void> addClientRole(
      @PathVariable String id, @PathVariable String roleName) {
    groupService.addClientRole(id, roleName);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Operation(
      operationId = "removeClientRole",
      description = "remove role from group",
      tags = {"Group"},
      parameters = {
        @Parameter(in = ParameterIn.PATH, name = "id", description = "group id"),
        @Parameter(in = ParameterIn.PATH, name = "roleName", description = "role name")
      })
  @DeleteMapping("/{id}/client-role/{roleName}")
  public ResponseEntity<Void> removeClientRole(
      @PathVariable String id, @PathVariable String roleName) {
    groupService.removeClientRole(id, roleName);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
