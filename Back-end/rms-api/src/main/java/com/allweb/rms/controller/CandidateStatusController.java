package com.allweb.rms.controller;

import com.allweb.rms.entity.dto.CandidateStatusDTO;
import com.allweb.rms.service.CandidateStatusService;
import com.allweb.rms.utils.EntityResponseHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/api/v1/candidate/status")
public class CandidateStatusController {

  private final CandidateStatusService statusCandidateService;

  @Autowired
  public CandidateStatusController(CandidateStatusService statusCandidateService) {
    this.statusCandidateService = statusCandidateService;
  }

  /**
   * Create a new status candidate
   *
   * @param statusCandidate
   * @return
   */
  @Operation(
      operationId = "createStatusCandidate",
      description = "create a new status candidate",
      tags = {"Candidate Status"})
  @PostMapping
  public ResponseEntity<CandidateStatusDTO> createStatusCandidate(
      @RequestBody @Valid CandidateStatusDTO statusCandidate) {
    return new ResponseEntity<>(
        statusCandidateService.createStatusCandidate(statusCandidate), HttpStatus.CREATED);
  }

  /**
   * get all status candidates
   *
   * @return
   */
  @Operation(
      operationId = "getStatusCandidates",
      description = "get all status candidates",
      tags = {"Candidate Status"},
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
        @Parameter(
            in = ParameterIn.QUERY,
            name = "status",
            description = "Filter by status",
            schema =
                @Schema(
                    type = "String",
                    name = "status",
                    allowableValues = {"active", "inactive"}))
      })
  @GetMapping
  public ResponseEntity<EntityResponseHandler<EntityModel<CandidateStatusDTO>>> getCandidateStatus(
      @RequestParam(required = false) String status,
      @RequestParam(value = "page", defaultValue = "1") int page,
      @RequestParam(value = "pageSize", defaultValue = "0") int pageSize,
      @RequestParam(defaultValue = "desc") String sortDirection,
      @RequestParam(defaultValue = "createdAt") String sortByField,
      @RequestParam(value = "filter", required = false) String filter) {
    return ResponseEntity.ok(
        statusCandidateService.getCandidateStatus(
            status, page, pageSize, sortDirection, sortByField, filter));
  }

  @Operation(
      operationId = "updateCandidateStatus",
      description = "update a status candidates",
      tags = {"Candidate Status"})
  @PutMapping
  public ResponseEntity<CandidateStatusDTO> updateCandidateStatus(
      @Valid @RequestBody CandidateStatusDTO statusCandidate) {
    CandidateStatusDTO statusCandidate1 =
        statusCandidateService.updateCandidateStatus(statusCandidate);
    return new ResponseEntity<>(statusCandidate1, HttpStatus.OK);
  }

  /**
   * Get status candidate by id
   *
   * @param id
   * @return
   */
  @Operation(
      operationId = "getCandidateStatusById",
      description = "get a status candidates by id",
      tags = {"Candidate Status"},
      parameters = {
        @Parameter(in = ParameterIn.PATH, name = "id", description = "statusCandidate id")
      })
  @GetMapping("/{id}")
  public ResponseEntity<CandidateStatusDTO> getCandidateStatusById(@PathVariable int id) {
    return new ResponseEntity<>(statusCandidateService.getCandidateStatusById(id), HttpStatus.OK);
  }

  /**
   * Delete status candidate
   *
   * @param id
   * @param isDelete
   * @return
   */
  @Operation(
      operationId = "deleteStatusCandidate",
      description = "delete a status candidate",
      tags = {"Candidate Status"},
      parameters = {
        @Parameter(in = ParameterIn.PATH, name = "id", description = "candidate status id"),
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
  public ResponseEntity<CandidateStatusDTO> deleteStatusCandidate(
      @PathVariable("id") int id, @PathVariable boolean isDelete) {
    CandidateStatusDTO statusCandidate = statusCandidateService.deleteCandidateStatus(id, isDelete);
    return new ResponseEntity<>(statusCandidate, HttpStatus.OK);
  }

  /**
   * update active status candidate
   *
   * @param id
   * @param active
   * @return
   */
  @Operation(
      operationId = "updateActiveStatusCandidate",
      description = "update active a status candidate",
      tags = {"Candidate Status"},
      parameters = {
        @Parameter(in = ParameterIn.PATH, name = "id", description = "status Candidate id"),
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
  public ResponseEntity<CandidateStatusDTO> updateActiveStatusCandidate(
      @PathVariable int id, @PathVariable boolean active) {
    CandidateStatusDTO statusCandidate =
        statusCandidateService.updateActiveCandidateStatus(id, active);
    return new ResponseEntity<>(statusCandidate, HttpStatus.OK);
  }

  /**
   * Update deletable status candidate by id
   *
   * @param id
   * @param isDeletable
   * @return
   */
  @Operation(
      operationId = "updateDeletableCandidateStatus",
      description = "update deletable a status candidate",
      tags = {"Candidate Status"},
      parameters = {
        @Parameter(in = ParameterIn.PATH, name = "id", description = "status Candidate id"),
        @Parameter(
            in = ParameterIn.PATH,
            name = "isDeletable",
            description = "change value of isDeletable column to true or false",
            schema =
                @Schema(
                    name = "isDeletable",
                    allowableValues = {"true", "false"}))
      })
  @PatchMapping("/{id}/deletable/{isDeletable}")
  public ResponseEntity<CandidateStatusDTO> updateDeletableCandidateStatus(
      @PathVariable int id, @PathVariable boolean isDeletable) {
    CandidateStatusDTO statusCandidate =
        statusCandidateService.updateDeletableCandidateStatus(id, isDeletable);
    return new ResponseEntity<>(statusCandidate, HttpStatus.OK);
  }

  @Operation(
      operationId = "findAllByByMailConfigurationNotUsed",
      description = "get all status that not used with mail configuration",
      tags = {"Candidate Status"},
      parameters = {
        @Parameter(
            in = ParameterIn.QUERY,
            name = "filter",
            description = "filter on title of status")
      })
  @GetMapping("/mailConfigurationNotUsed")
  public ResponseEntity<Map<String, Object>> findAllByMailConfigurationNotUsed(
      @RequestParam(required = false) String filter) {
    return new ResponseEntity<>(
        statusCandidateService.findAllByMailConfigurationNotUsed(filter), HttpStatus.OK);
  }
}
