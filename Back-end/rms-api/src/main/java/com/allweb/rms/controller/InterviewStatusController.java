package com.allweb.rms.controller;

import com.allweb.rms.entity.dto.InterviewStatusDTO;
import com.allweb.rms.entity.jpa.InterviewStatus;
import com.allweb.rms.service.InterviewStatusService;
import com.allweb.rms.utils.EntityResponseHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/interview/status")
public class InterviewStatusController {

  private final InterviewStatusService interviewStatusService;

  @Autowired
  public InterviewStatusController(InterviewStatusService interviewStatusService) {
    this.interviewStatusService = interviewStatusService;
  }

  @Operation(
      operationId = "getStatus",
      description = "Get status by id",
      tags = {"Interview Status"},
      parameters = {@Parameter(in = ParameterIn.PATH, name = "id", description = "Status id")})
  @GetMapping("/{id}")
  public ResponseEntity<InterviewStatus> getStatusById(@PathVariable("id") int id) {
    return new ResponseEntity<>(interviewStatusService.getStatusById(id), HttpStatus.OK);
  }

  @Operation(
      operationId = "getStatus",
      description = "Get all Status",
      tags = {"Interview Status"},
      parameters = {
        @Parameter(in = ParameterIn.QUERY, name = "page", description = "Page index"),
        @Parameter(in = ParameterIn.QUERY, name = "pageSize", description = "Page size"),
        @Parameter(in = ParameterIn.QUERY, name = "filter", description = "Filter"),
        @Parameter(in = ParameterIn.QUERY, name = "sortDirection", description = "Direction Sort"),
        @Parameter(in = ParameterIn.QUERY, name = "active", description = "status active"),
        @Parameter(
            in = ParameterIn.QUERY,
            name = "sortByField",
            description = "Name field that to sort")
      })
  @GetMapping
  public ResponseEntity<EntityResponseHandler<EntityModel<InterviewStatusDTO>>> getStatusList(
      @RequestParam(value = "page", required = false, defaultValue = "1") int page,
      @RequestParam(value = "pageSize", required = false, defaultValue = "10") int size,
      @RequestParam(value = "filter", required = false) String filter,
      @RequestParam(value = "active", required = false) boolean active,
      @RequestParam(value = "sortDirection", required = false, defaultValue = "desc")
          String sortDirection,
      @RequestParam(value = "sortByField", required = false, defaultValue = "createdAt")
          String sortByField) {
    return new ResponseEntity<>(
        interviewStatusService.getStatusList(
            page, size, filter, sortDirection, sortByField, active),
        HttpStatus.OK);
  }

  @Operation(
      operationId = "createStatus",
      description = "Create status ",
      tags = {"Interview Status"})
  @PostMapping
  public ResponseEntity<InterviewStatusDTO> saveStatus(
      @Valid @RequestBody InterviewStatusDTO status) {
    return new ResponseEntity<>(interviewStatusService.save(status), HttpStatus.CREATED);
  }

  @Operation(
      operationId = "updateStatus",
      description = "Update status ",
      tags = {"Interview Status"})
  @PatchMapping
  public ResponseEntity<InterviewStatusDTO> updateStatus(
      @Valid @RequestBody InterviewStatusDTO status) {
    return new ResponseEntity<>(interviewStatusService.update(status), HttpStatus.OK);
  }

  @Operation(
      operationId = "updateStatusActive",
      description = "Update status active ",
      tags = {"Interview Status"},
      parameters = {
        @Parameter(in = ParameterIn.PATH, name = "id", description = "Status id"),
        @Parameter(in = ParameterIn.PATH, name = "isActive", description = "Status active")
      })
  @PatchMapping("/{id}/active/{isActive}")
  public ResponseEntity<HttpStatus> updateStatusActive(
      @PathVariable("id") int id, @PathVariable("isActive") boolean isActive) {
    interviewStatusService.updateActive(id, isActive);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Operation(
      operationId = "deleteStatus",
      description = "Delete status ",
      tags = {"Interview Status"},
      parameters = {@Parameter(in = ParameterIn.PATH, name = "id", description = "Status id")})
  @DeleteMapping("/{id}")
  public ResponseEntity<HttpStatus> deleteStatus(@PathVariable("id") int id) {
    interviewStatusService.delete(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
