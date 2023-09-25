package com.allweb.rms.controller;

import com.allweb.rms.entity.dto.UniversityDTO;
import com.allweb.rms.service.UniversityService;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/university")
public class UniversityController {
  private final UniversityService universityService;

  @Autowired
  public UniversityController(UniversityService universityService) {
    this.universityService = universityService;
  }

  @Operation(
      operationId = "createUniversity",
      description = "create a new university",
      tags = {"University"})
  @PostMapping
  public ResponseEntity<UniversityDTO> createUniversity(
      @RequestBody @Valid UniversityDTO universityDTO) {
    return new ResponseEntity<>(universityService.createUniversity(universityDTO), HttpStatus.OK);
  }

  @Operation(
      operationId = "updateUniversity",
      description = "update a university by request body",
      tags = {"University"})
  @PutMapping
  public ResponseEntity<UniversityDTO> updateUniversity(
      @RequestBody @Valid UniversityDTO universityDTO) {
    return new ResponseEntity<>(universityService.updateUniversity(universityDTO), HttpStatus.OK);
  }

  @Operation(
      operationId = "getUniversityById",
      description = "get university by id",
      tags = {"University"},
      parameters = {@Parameter(in = ParameterIn.PATH, name = "id", description = "university id")})
  @GetMapping("/{id}")
  public ResponseEntity<UniversityDTO> getUniversityById(@PathVariable int id) {
    return new ResponseEntity<>(universityService.getUniversityById(id), HttpStatus.OK);
  }

  @Operation(
      operationId = "deleteUniversityById",
      description = "delete a university by id",
      tags = {"University"},
      parameters = {@Parameter(in = ParameterIn.PATH, name = "id", description = "university id")})
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteUniversityById(@PathVariable int id) {
    universityService.deleteUniversityById(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Operation(
      operationId = "getAllUniversity",
      description = "get all university",
      tags = {"University"},
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
            name = "filter",
            description = "filter data on the table"),
      })
  @GetMapping
  public EntityResponseHandler<EntityModel<UniversityDTO>> getAllUniversity(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int pageSize,
      @RequestParam(defaultValue = "desc") String sortDirection,
      @RequestParam(defaultValue = "createdAt") String sortByField,
      @RequestParam(required = false) String filter) {
    return universityService.getAllUniversity(filter, page, pageSize, sortDirection, sortByField);
  }
}
