package com.allweb.rms.controller;

import com.allweb.rms.entity.dto.SystemConfigDTO;
import com.allweb.rms.service.SystemConfigurationService;
import com.allweb.rms.utils.EntityResponseHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/systemProperty")
public class SystemPropertyController {
  private final SystemConfigurationService systemConfigurationService;

  public SystemPropertyController(SystemConfigurationService systemConfigurationService) {
    this.systemConfigurationService = systemConfigurationService;
  }

  @Operation(
      operationId = "updateSystemProperty",
      description = "insert and update system properties",
      tags = "System Property")
  @PutMapping("/systemProperty")
  public ResponseEntity<SystemConfigDTO> updateSystemProperty(
      @Valid @RequestBody SystemConfigDTO systemConfigDTO) {
    return new ResponseEntity<>(systemConfigurationService.save(systemConfigDTO), HttpStatus.OK);
  }

  @Operation(
      operationId = "getSystemProperties",
      description = "Get System Properties",
      tags = "System Property",
      parameters = {
        @Parameter(in = ParameterIn.QUERY, name = "page", description = "Page index"),
        @Parameter(in = ParameterIn.QUERY, name = "pageSize", description = "Page Size"),
        @Parameter(in = ParameterIn.QUERY, name = "sortDirection", description = "Direction Sort"),
        @Parameter(
            in = ParameterIn.QUERY,
            name = "sortByField",
            description = "Name field that to sort")
      })
  @GetMapping("/getSystemProperties")
  public ResponseEntity<EntityResponseHandler<SystemConfigDTO>> getSystemProperties(
      @RequestParam(value = "page", required = false, defaultValue = "1") int page,
      @RequestParam(value = "pageSize", required = false, defaultValue = "10") int size,
      @RequestParam(value = "filter", required = false) String filter,
      @RequestParam(value = "sortDirection", required = false, defaultValue = "asc")
          String sortDirection,
      @RequestParam(value = "sortByField", required = false, defaultValue = "createdAt")
          String sortByField) {
    return new ResponseEntity<>(
        systemConfigurationService.getSystemMailConfigs(
            page, size, sortDirection, sortByField, filter),
        HttpStatus.OK);
  }

  @Operation(
      operationId = "getSystemProperty",
      description = "get System Property by Id",
      tags = "System Property",
      parameters = {
        @Parameter(in = ParameterIn.PATH, name = "id", description = "System Property Id")
      })
  @GetMapping("/getSystemProperty/{id}")
  public ResponseEntity<SystemConfigDTO> getSystemProperty(@PathVariable("id") int id) {
    return new ResponseEntity<>(
        systemConfigurationService.getSystemPropertyById(id), HttpStatus.OK);
  }

  @Operation(
      operationId = "deleteSystemProperty",
      description = "delete System Property",
      tags = "System Property",
      parameters = {
        @Parameter(in = ParameterIn.PATH, name = "id", description = "System Property id")
      })
  @DeleteMapping("/systemProperty/{id}")
  public ResponseEntity<SystemConfigDTO> deleteSystemProperty(@PathVariable("id") int id) {
    systemConfigurationService.delete(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
