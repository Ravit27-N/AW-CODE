package com.allweb.rms.controller;

import com.allweb.rms.entity.dto.DemandDTO;
import com.allweb.rms.entity.dto.DemandDTO_List;
import com.allweb.rms.entity.dto.DemandResponse;
import com.allweb.rms.entity.elastic.DemandElasticsearchDocument;
import com.allweb.rms.entity.jpa.Demand;
import com.allweb.rms.service.DemandService;
import com.allweb.rms.utils.EntityResponseHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

import java.io.IOException;

@RequestMapping("/api/v1/demand")
@RestController
public class DemandController {

  private final DemandService demandService;

  @Autowired
  public DemandController(DemandService demandService) {
    this.demandService = demandService;
  }
  private static final String[] SORTABLE_FIELDS =
          new String[] {"project", "jobDescription", "deadLine", "createdAt","active"};
  @Operation(
      operationId = "createDemand",
      description = "create new Demand",
      tags = {"Candidate"})
  @PostMapping
  public ResponseEntity<DemandDTO> saveDemand(@RequestBody @Valid DemandDTO demandDTO) {
    return new ResponseEntity<>(demandService.saveDemand(demandDTO), HttpStatus.CREATED);
  }

  @GetMapping("/validateProject/{projectId}/validateJob/{jobDescriptionId}")
  public int validateProjectJob(
      @PathVariable("projectId") int projectId,
      @PathVariable("jobDescriptionId") int jobDescriptionId) {
    return demandService.validateProjectJob(projectId, jobDescriptionId);
  }

  @GetMapping("/{demandId}/validateProject/{projectId}/validateJob/{jobDescriptionId}")
  public int validateUpdateProjectJob(
      @PathVariable("projectId") int projectId,
      @PathVariable("jobDescriptionId") int jobDescriptionId,
      @PathVariable("demandId") int demandId) {
    return demandService.validateUpdateProjectJob(projectId, jobDescriptionId, demandId);
  }

  @GetMapping("/validateAddCandidate/{nbCandidate}/ToDemand/{nbRequired}")
  public boolean validateAddCandidateInToDemand(
      @PathVariable("nbCandidate") int nbCandidate, @PathVariable("nbRequired") int nbRequired) {
    return demandService.validateAddCandidateInToDemand(nbCandidate, nbRequired);
  }

  @Operation(
      operationId = "getDemandById",
      description = "Get demand by id this function use for response when demand update")
  @GetMapping("/{id}")
  public ResponseEntity<DemandDTO> getDemandById(@PathVariable("id") int id) {
    return new ResponseEntity<>(demandService.getDemandById(id), HttpStatus.OK);
  }

  @Operation(
      operationId = "updateActiveDemand",
      description = "update active a demand",
      tags = {"Demand"},
      parameters = {
        @Parameter(in = ParameterIn.PATH, name = "id", description = "Demand id"),
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
  public ResponseEntity<DemandDTO> updateActiveDemand(
      @PathVariable("id") int id, @PathVariable("active") boolean active) {
    return new ResponseEntity<>(demandService.updateActiveDemand(id, active), HttpStatus.OK);
  }

  @Operation(
      operationId = "softDeleteDemand",
      description = "soft delete a demand",
      tags = {"Demand"},
      parameters = {
        @Parameter(in = ParameterIn.PATH, name = "id", description = "Demand id"),
        @Parameter(
            in = ParameterIn.PATH,
            name = "isDeleted",
            description = "change value of isDeleted column to true or false",
            schema =
                @Schema(
                    name = "isDelete",
                    allowableValues = {"true", "false"}))
      })
  @PatchMapping("/{id}/delete/{isDelete}")
  public ResponseEntity<DemandDTO> softDeleteDemand(
      @PathVariable("id") int id, @PathVariable("isDelete") boolean isDelete) {
    return new ResponseEntity<>(demandService.softDeleteDemand(id, isDelete), HttpStatus.OK);
  }

  @Operation(
      operationId = "hardDeleteById",
      description = "Hard delete demand by id",
      tags = {"Demand"},
      parameters = @Parameter(name = "id", in = ParameterIn.PATH))
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> hardDeleteDemand(@PathVariable("id") int id) {
    demandService.hardDeleteDemand(id);
    return ResponseEntity.ok().build();
  }

  @Operation(
      operationId = "updateDemand",
      description = "update a demand",
      tags = {"Demand"})
  @PutMapping()
  public ResponseEntity<DemandDTO> updateDemand(@RequestBody DemandDTO demandDTO) {
    return new ResponseEntity<>(demandService.updateDemand(demandDTO), HttpStatus.OK);
  }

  @Operation(
      operationId = "get demand ",
      description = "Get all Demands",
      tags = {"demand"},
      parameters = {
        @Parameter(in = ParameterIn.QUERY, name = "page", description = "Page index"),
        @Parameter(in = ParameterIn.QUERY, name = "pageSize", description = "Page size"),
        @Parameter(
            in = ParameterIn.QUERY,
            name = "isDeleted",
            description = "this param is used for get candidates by deleted true or false"),
        @Parameter(in = ParameterIn.QUERY, name = "filter", description = "Filter"),
        @Parameter(in = ParameterIn.QUERY, name = "sortDirection", description = "Direction Sort"),
        @Parameter(in = ParameterIn.QUERY, name = "active", description = "status active"),
        @Parameter(
            in = ParameterIn.QUERY,
            name = "sortByField",
            description = "Name field that to sort")
      })
  @GetMapping
  public ResponseEntity<EntityResponseHandler<DemandDTO_List>> getAllDemands(
      @RequestParam(value = "page", defaultValue = "1") int page,
      @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
      @RequestParam(defaultValue = "false") boolean isDeleted,
      @RequestParam(value = "filter", required = false) String filter,
      @RequestParam(value = "active", required = false) boolean active,
      @RequestParam(value = "sortDirection", defaultValue = "desc") String sortDirection,
      @RequestParam(value = "sortByField", defaultValue = "createdAt") String sortByField) {
    String lowerFilter;
    if (filter == null || filter == "") {
      lowerFilter = filter;
    } else {
      lowerFilter = filter.toLowerCase();
    }
    sortByField = SORTABLE_FIELDS[0].equals(sortByField)?"project.name" : sortByField;
    sortByField = SORTABLE_FIELDS[1].equals(sortByField)?"jobDescription.title" : sortByField;
    sortByField = SORTABLE_FIELDS[2].equals(sortByField)?"dead_line":sortByField;
    sortByField = SORTABLE_FIELDS[3].equals(sortByField)?"created_at":sortByField;
    sortByField = SORTABLE_FIELDS[4].equals(sortByField)?"active":sortByField;

    return new ResponseEntity<>(
        demandService.getAllDemandResponse(
            page, pageSize, isDeleted, lowerFilter, sortDirection, sortByField, active),
        HttpStatus.OK);
  }
//  @Operation(
//          operationId = "get demand ",
//          description = "Get all Demands",
//          tags = {"demand"},
//          parameters = {
//                  @Parameter(in = ParameterIn.QUERY, name = "page", description = "Page index"),
//                  @Parameter(in = ParameterIn.QUERY, name = "pageSize", description = "Page size"),
//                  @Parameter(
//                          in = ParameterIn.QUERY,
//                          name = "isDeleted",
//                          description = "this param is used for get candidates by deleted true or false"),
//                  @Parameter(in = ParameterIn.QUERY, name = "filter", description = "Filter"),
//                  @Parameter(in = ParameterIn.QUERY, name = "sortDirection", description = "Direction Sort"),
//                  @Parameter(in = ParameterIn.QUERY, name = "active", description = "status active"),
//                  @Parameter(
//                          in = ParameterIn.QUERY,
//                          name = "sortByField",
//                          description = "Name field that to sort")
//          })
//  @GetMapping("testing")
//  public ResponseEntity<EntityResponseHandler<DemandDTO_List>>  getall (@RequestParam(value = "page", defaultValue = "1") int page,
//                                                                       @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
//                                                                       @RequestParam(defaultValue = "false") boolean isDeleted,
//                                                                       @RequestParam(value = "filter", required = false) String filter,
//                                                                       @RequestParam(value = "active", required = false) boolean active,
//                                                                       @RequestParam(value = "sortDirection", defaultValue = "desc") String sortDirection,
//                                                                       @RequestParam(value = "sortByField", defaultValue = "createdAt") String sortByField){
//    String lowerFilter;
//    if (filter == null || filter == "") {
//      lowerFilter = filter;
//    } else {
//      lowerFilter = filter.toLowerCase();
//    }
//    return new ResponseEntity<>(
//            demandService.getAllDemandResponse(
//                    page, pageSize, isDeleted, lowerFilter, sortDirection, sortByField, active),
//            HttpStatus.OK);
//  }
  @GetMapping("idx")
  public String delectIndex(){
    demandService.deleteIndex();
    return "ok";
  }
  @GetMapping("idxC")
  public String createIdex(){
    demandService.createIndex();
    return "ok";
  }
  @PostMapping("testing/add")
  public DemandDTO addDemand(@RequestBody DemandResponse demand){
    return demandService.addDemandWithElastic(demand);
  }

  @Operation(
      operationId = "saveCandidateInToDemand",
      description = "save candidate into demand",
      tags = {"Demand"})
  @PatchMapping("{demandId}/candidates/{candidateId}")
  public ResponseEntity<DemandDTO> saveCandidateInToDemand(
      @PathVariable("demandId") int demandId, @PathVariable("candidateId") String candidateId) {
    return new ResponseEntity<>(
        demandService.saveCandidateInToDemand(demandId, candidateId), HttpStatus.OK);
  }

  @Operation(
      operationId = "deleteCandidateFromDemand",
      description = "candidate id from demand",
      tags = {"Demand"})
  @PatchMapping("{demandId}/delete-candidates/{candidateId}")
  public ResponseEntity<DemandDTO> deleteCandidateFromDemand(
      @PathVariable("demandId") int demandId, @PathVariable("candidateId") int candidateId) {
    return new ResponseEntity<>(
        demandService.deleteCandidateFromDemand(demandId, candidateId), HttpStatus.OK);
  }
}
