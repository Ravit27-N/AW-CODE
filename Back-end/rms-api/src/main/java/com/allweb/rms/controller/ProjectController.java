package com.allweb.rms.controller;

import com.allweb.rms.entity.dto.ProjectDTO;
import com.allweb.rms.service.ProjectService;
import com.allweb.rms.utils.EntityResponseHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
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
@RequestMapping("/api/v1/demand/project")
public class ProjectController {
  private final ProjectService projectService;

  @Autowired
  public ProjectController(ProjectService projectService) {
    this.projectService = projectService;
  }

  @Operation(
      operationId = "createProject",
      description = "create a new project",
      tags = {"Project"})
  @PostMapping
  public ResponseEntity<ProjectDTO> createProject(@RequestBody @Valid ProjectDTO project) {
    return new ResponseEntity<>(projectService.createProject(project), HttpStatus.CREATED);
  }

  @Operation(
      operationId = "validateName",
      description = "validate name by name",
      tags = {"Project"},
      parameters = {@Parameter(in = ParameterIn.PATH, name = "name", description = "Project name")})
  @GetMapping("/validateName/{name}")
  public Long validateName(@PathVariable String name) {
    return projectService.validateName(name);
  }

  @Operation(
      operationId = "validateName",
      description = "validate name by name",
      tags = {"Project"},
      parameters = {@Parameter(in = ParameterIn.PATH, name = "name", description = "Project name")})
  @GetMapping("/{id}/validateUpdateName/{name}")
  public Long validateUpdateName(@PathVariable int id, @PathVariable String name) {
    return projectService.validateUpdateName(id, name);
  }

  @Operation(
      operationId = "getProjectById",
      description = "get a project by id",
      tags = {"Project"},
      parameters = {@Parameter(in = ParameterIn.PATH, name = "id", description = "project id")})
  @GetMapping("/{id}")
  public ResponseEntity<ProjectDTO> getProjectById(@PathVariable int id) {
    return new ResponseEntity<>(projectService.getProjectById(id), HttpStatus.OK);
  }

  @Operation(
      operationId = "updateProject",
      description = "update a project",
      tags = {"Project"})
  @PutMapping
  public ResponseEntity<ProjectDTO> updateProject(@Valid @RequestBody ProjectDTO projectDTO) {
    ProjectDTO project = projectService.updateProject(projectDTO);
    return new ResponseEntity<>(project, HttpStatus.OK);
  }

  @Operation(
      operationId = "updateActiveProject",
      description = "update active a project",
      tags = {"Project"},
      parameters = {
        @Parameter(in = ParameterIn.PATH, name = "id", description = "Project id"),
        @Parameter(
            in = ParameterIn.PATH,
            name = "active",
            description = "change value of active column to true or false",
            schema =
                @Schema(
                    name = "active",
                    allowableValues = {"true", "false"}))
      })
  @PatchMapping("/{id}/active/{active}")
  public ResponseEntity<ProjectDTO> updateActiveProject(
      @PathVariable int id, @PathVariable boolean active) {
    ProjectDTO project = projectService.updateActiveProject(id, active);
    return new ResponseEntity<>(project, HttpStatus.OK);
  }

  @Operation(
      operationId = "deleteStatusCandidate",
      description = "delete a status project",
      tags = {"Project"},
      parameters = {
        @Parameter(in = ParameterIn.PATH, name = "id", description = "project id"),
        @Parameter(
            in = ParameterIn.PATH,
            name = "isDelete",
            description = "change value of isDelete column to true or false",
            schema =
                @Schema(
                    name = "isDelete",
                    allowableValues = {"true", "false"}))
      })
  @PatchMapping("/{id}/delete/{isDelete}")
  public ResponseEntity<ProjectDTO> softDeleteProject(
      @PathVariable("id") int id, @PathVariable("isDelete") boolean isDelete) {
    ProjectDTO project = projectService.softDeleteProject(id, isDelete);
    return new ResponseEntity<>(project, HttpStatus.OK);
  }

  @Operation(
      operationId = "hardDeleteById",
      description = "Hard delete project by id",
      tags = {"Project"},
      parameters = @Parameter(name = "id", in = ParameterIn.PATH))
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> hardDeleteProject(@PathVariable("id") int id) {
    projectService.hardDeleteProject(id);
    return ResponseEntity.ok().build();
  }

  @Operation(
      operationId = "getStatus",
      description = "Get all Status",
      tags = {"Interview Status"},
      parameters = {
        @Parameter(in = ParameterIn.QUERY, name = "page", description = "Page index"),
        @Parameter(in = ParameterIn.QUERY, name = "pageSize", description = "Page size"),
        @Parameter(
            in = ParameterIn.QUERY,
            name = "isDeleted",
            description = "this param is used for get project by deleted true or false"),
        @Parameter(in = ParameterIn.QUERY, name = "filter", description = "Filter"),
        @Parameter(in = ParameterIn.QUERY, name = "sortDirection", description = "Direction Sort"),
        @Parameter(in = ParameterIn.QUERY, name = "active", description = "status active"),
        @Parameter(
            in = ParameterIn.QUERY,
            name = "sortByField",
            description = "Name field that to sort")
      })
  @GetMapping
  public ResponseEntity<EntityResponseHandler<EntityModel<ProjectDTO>>> getAllProject(
      @RequestParam(value = "page", required = false, defaultValue = "1") int page,
      @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
      @RequestParam(defaultValue = "false") boolean isDeleted,
      @RequestParam(value = "filter", required = false) String filter,
      @RequestParam(value = "active", required = false) boolean active,
      @RequestParam(value = "sortDirection", defaultValue = "desc", required = false)
          String sortDirection,
      @RequestParam(value = "sortByField", defaultValue = "createdAt") String sortByField) {
    return new ResponseEntity<>(
        projectService.getAllProject(
            page, pageSize, isDeleted, filter, sortDirection, sortByField, active),
        HttpStatus.OK);
  }
}
