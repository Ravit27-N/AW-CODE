package com.allweb.rms.controller;

import com.allweb.rms.entity.dto.ModuleDTO;
import com.allweb.rms.service.ModuleService;
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
@RequestMapping("/api/v1/module")
public class ModuleController {

  private final ModuleService moduleService;

  @Autowired
  public ModuleController(ModuleService moduleService) {
    this.moduleService = moduleService;
  }

  @Operation(
      operationId = "createModule",
      description = "create a new module",
      tags = {"Module"})
  @PostMapping
  public ResponseEntity<ModuleDTO> createModule(@RequestBody @Valid ModuleDTO moduleDTO) {
    return new ResponseEntity<>(moduleService.createModule(moduleDTO), HttpStatus.OK);
  }

  @Operation(
      operationId = "updateModule",
      description = "update a module",
      tags = {"Module"})
  @PutMapping
  public ResponseEntity<ModuleDTO> updateModule(@RequestBody @Valid ModuleDTO moduleDTO) {
    return new ResponseEntity<>(moduleService.updateModule(moduleDTO), HttpStatus.OK);
  }

  @Operation(
      operationId = "findModuleById",
      description = "find a module by id",
      tags = {"Module"},
      parameters = {@Parameter(in = ParameterIn.PATH, name = "id", description = "module id")})
  @GetMapping("/{id}")
  public ResponseEntity<ModuleDTO> findModuleById(@PathVariable int id) {
    return new ResponseEntity<>(moduleService.findModuleById(id), HttpStatus.OK);
  }

  @Operation(
      operationId = "updateActive",
      description = "update active a module by id",
      tags = {"Module"},
      parameters = {
        @Parameter(in = ParameterIn.PATH, name = "id", description = "module id"),
        @Parameter(
            in = ParameterIn.PATH,
            name = "isActive",
            schema =
                @Schema(
                    name = "isActive",
                    type = "boolean",
                    allowableValues = {"true", "false"}))
      })
  @PatchMapping("/{id}/active/{isActive}")
  public ResponseEntity<ModuleDTO> updateActive(
      @PathVariable int id, @PathVariable boolean isActive) {
    return new ResponseEntity<>(moduleService.updateActive(id, isActive), HttpStatus.OK);
  }

  @Operation(
      operationId = "getAllModules",
      description = "get all module",
      tags = {"Module"},
      parameters = {
        @Parameter(in = ParameterIn.QUERY, name = "page", description = "page index"),
        @Parameter(
            in = ParameterIn.QUERY,
            name = "pageSize",
            description = "size of page or limit"),
        @Parameter(
            in = ParameterIn.QUERY,
            name = "sortDirection",
            description = "type of order data or record",
            schema =
                @Schema(
                    name = "sortDirection",
                    type = "string",
                    allowableValues = {"asc", "desc"})),
        @Parameter(
            in = ParameterIn.QUERY,
            name = "sortByField",
            description = "field name that wanna sort"),
        @Parameter(in = ParameterIn.QUERY, name = "filter", description = "Filter on the table")
      })
  @GetMapping
  public EntityResponseHandler<EntityModel<ModuleDTO>> getAllModules(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int pageSize,
      @RequestParam(defaultValue = "desc") String sortDirection,
      @RequestParam(defaultValue = "createdAt") String sortByField,
      @RequestParam(required = false) String filter) {
    return moduleService.getAllModules(page, pageSize, sortDirection, sortByField, filter);
  }

  @Operation(
      operationId = "deleteModuleById",
      description = "delete a module by id",
      tags = {"Module"},
      parameters = {@Parameter(in = ParameterIn.PATH, name = "id", description = "module id")})
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteModuleById(@PathVariable int id) {
    moduleService.deleteModuleById(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
