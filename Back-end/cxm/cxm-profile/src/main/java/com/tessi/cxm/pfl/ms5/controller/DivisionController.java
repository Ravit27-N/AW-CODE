package com.tessi.cxm.pfl.ms5.controller;

import com.tessi.cxm.pfl.ms5.dto.DivisionDto;
import com.tessi.cxm.pfl.ms5.service.DepartmentService;
import com.tessi.cxm.pfl.ms5.service.DivisionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/v1/divisions")
@RequiredArgsConstructor
@Tag(name = "Division Management", description = "Endpoints used to manage the Division.")
public class DivisionController {
  private final DivisionService divisionService;
  private final DepartmentService departmentService;

  @Operation(operationId = "findAll", summary = "To get all divisions.")
  @GetMapping
  public ResponseEntity<List<DivisionDto>> getAllDivision() {
    return new ResponseEntity<>(this.divisionService.findAll(), HttpStatus.OK);
  }

  @Operation(operationId = "createDivision", summary = "To add new division of a client.")
  @PostMapping
  public ResponseEntity<DivisionDto> createDivision(@RequestBody DivisionDto divisionDto) {
    return new ResponseEntity<>(this.divisionService.save(divisionDto), HttpStatus.CREATED);
  }

  @Operation(operationId = "updateDivision", summary = "To update existing division of a client.")
  @PutMapping
  public ResponseEntity<DivisionDto> updateDivision(@RequestBody DivisionDto divisionDto) {
    return new ResponseEntity<>(this.divisionService.update(divisionDto), HttpStatus.OK);
  }

  @Operation(operationId = "deleteDivision", summary = "To delete existing division of a client.")
  @DeleteMapping("/{id}")
  public ResponseEntity<HttpStatus> deleteDivision(@PathVariable long id) {
    this.divisionService.delete(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Operation(operationId = "getAllUser", summary = "Get all users in division.")
  @GetMapping("/{id}/users")
  public ResponseEntity<List<String>> getAllUser(@PathVariable long id) {
    return new ResponseEntity<>(this.divisionService.getAllUserInDivision(id), HttpStatus.OK);
  }

  @Operation(operationId = "getDivisionsByClient", summary = "Get all division by client.")
  @GetMapping("/{id}/client")
  public ResponseEntity<List<DivisionDto>> getDivisionsByClient(@PathVariable long id) {
    return new ResponseEntity<>(this.divisionService.getDivisionByClient(id), HttpStatus.OK);
  }

  @GetMapping("/is-duplicate/{clientId}")
  public ResponseEntity<Boolean> validateDuplicateName(
      @RequestParam(value = "name") String name,
      @RequestParam(value = "id", defaultValue = "0") long id, @PathVariable long clientId) {
    return ResponseEntity.ok(this.divisionService.validateDuplicateName(id, clientId, name));
  }

  // add new
  // updated
  @Operation(operationId = "getDivisionsByClient", summary = "Get all division by clients ids.")
  @GetMapping("/client/{id}")
  public ResponseEntity<List<DivisionDto>> getDivisionsByClient2(@PathVariable String id) {
    List<Long> clientIds = Arrays.asList(id.split(","))
        .stream()
        .map(Long::valueOf)
        .collect(Collectors.toList());

    List<DivisionDto> divisionList = new ArrayList<>();

    for (Long clientId : clientIds) {
      divisionList.addAll(this.divisionService.getDivisionByClient2(clientId));
    }

    return new ResponseEntity<>(divisionList, HttpStatus.OK);
  }

  // add new
  @GetMapping("/divisionInClientList")
  public ResponseEntity<List<DivisionDto>> getAllDivisionInClientList() {
    return ResponseEntity.ok(this.departmentService.getAllDivisionInClientList());
  }
}
