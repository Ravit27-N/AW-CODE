package com.tessi.cxm.pfl.ms5.controller;

import com.tessi.cxm.pfl.ms5.dto.DepartmentDto;
import com.tessi.cxm.pfl.ms5.repository.DepartmentRepository;
import com.tessi.cxm.pfl.ms5.service.DepartmentService;
import com.tessi.cxm.pfl.shared.model.DepartmentProjection;
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
@RequiredArgsConstructor
@RequestMapping("/v1/services")
@Tag(name = "Service Management", description = "Endpoints to manage the services of a division.")
public class ServiceController {
  private final DepartmentService departmentService;
  private final DepartmentRepository departmentRepository;

  @Operation(operationId = "findAll", summary = "To get all services.")
  @GetMapping
  public ResponseEntity<List<DepartmentDto>> getAllService() {
    return new ResponseEntity<>(this.departmentService.findAll(), HttpStatus.OK);
  }

  @Operation(operationId = "createService", summary = "To create a service of a division.")
  @PostMapping
  public ResponseEntity<DepartmentDto> createService(@RequestBody DepartmentDto dto) {
    return new ResponseEntity<>(this.departmentService.save(dto), HttpStatus.CREATED);
  }

  @Operation(operationId = "updateService", summary = "To update existing service of a division.")
  @PutMapping
  public ResponseEntity<DepartmentDto> updateService(@RequestBody DepartmentDto dto) {
    return new ResponseEntity<>(this.departmentService.update(dto), HttpStatus.OK);
  }

  @Operation(operationId = "deleteService", summary = "To delete existing service of a division.")
  @DeleteMapping("/{id}")
  public ResponseEntity<HttpStatus> deleteService(@PathVariable long id) {
    this.departmentService.delete(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Operation(operationId = "getUsers", summary = "To get all users service")
  @GetMapping("/{id}/users")
  public ResponseEntity<List<String>> getUsers(@PathVariable long id) {
    return ResponseEntity.ok(this.departmentService.getUsersInService(id));
  }

  @Operation(operationId = "getServicesOfUser", summary = "To get services by user.")
  @GetMapping("/services/{functionKey}/{privKey}")
  public List<DepartmentProjection> getServicesOfUser(
      @PathVariable String functionKey,
      @PathVariable String privKey,
      @RequestParam("isVisibility") boolean isVisibility) {
    return departmentService.getServicesByUser(isVisibility, functionKey, privKey);
  }

  /**
   * To validate duplicate service's name into division.
   *
   * @see DepartmentService#isDuplicateNameInDivision(String, long, long)
   */
  @Operation(operationId = "isDuplicateNameInDivision", summary = "To validate duplicate service's name into division.")
  @GetMapping("/duplicate/{name}/{divisionId}")
  public ResponseEntity<Boolean> isDuplicateNameInDivision(
      @PathVariable long divisionId,
      @PathVariable String name,
      @RequestParam(defaultValue = "0") long serviceId) {
    return ResponseEntity.ok(
        departmentService.isDuplicateNameInDivision(name, serviceId, divisionId));
  }

  @GetMapping("/services-in-client")
  public ResponseEntity<List<Long>> getAllServiceInCurrentClient() {
    return ResponseEntity.ok(this.departmentService.getAllServicesInClient());
  }

  // add new

  @GetMapping("/servicesInClientList")
  public ResponseEntity<List<DepartmentDto>> getAllServicesInClientList() {
    return ResponseEntity.ok(this.departmentService.getAllServicesInClientList());
  }

  // // add new
  // @Operation(operationId = "findAllByDivisionInClientId", summary = "To get all
  // service by client id.")
  // @GetMapping("/client/{id}")
  // public ResponseEntity<List<DepartmentProjection>>
  // findAllByDivisionInClientId(@PathVariable String id) {
  // List<Long> clientIds = Arrays.asList(id.split(","))
  // .stream()
  // .map(Long::valueOf)
  // .collect(Collectors.toList());
  // List<DepartmentProjection> departementList = new ArrayList<>();
  // for (Long clientId : clientIds) {
  // if (clientId == 0) {
  // departementList.addAll(this.departmentRepository.findAllByDivision());
  // } else {
  // departementList.addAll(this.departmentRepository.findAllByDivisionInClientId(clientId));
  // }
  // }
  // return new ResponseEntity<>(departementList, HttpStatus.OK);
  // }
  @Operation(operationId = "findAllByDivisionInClientId", summary = "To get all service by client id.")
  @GetMapping("/client/{id}")
  public ResponseEntity<List<DepartmentProjection>> findAllByDivisionInClientId(@PathVariable String id,
      @RequestParam("divisionId") String divisionIds) {

    List<Long> clientIds = Arrays.asList(id.split(","))
        .stream()
        .map(Long::valueOf)
        .collect(Collectors.toList());
    List<Long> divisionId = Arrays.asList(divisionIds.split(","))
        .stream()
        .map(Long::valueOf)
        .collect(Collectors.toList());
    List<DepartmentProjection> departementList = new ArrayList<>();
    for (Long clientId : clientIds) {
      for (Long divIds : divisionId) {
        if (clientId == 0 && divIds == 0) {
          departementList.addAll(this.departmentRepository.findAllByDivision());
        } else if (clientId == 0 && divIds != 0) {
          departementList.addAll(this.departmentRepository.findAllByDivisionId(divIds));
        } else if (clientId != 0 && divIds == 0) {
          departementList.addAll(this.departmentRepository.findAllByDivisionInClientId(clientId));
        } else if (clientId != 0 && divIds != 0) {
          departementList.addAll(this.departmentRepository.findAllByDivisionId(divIds));
        }
      }

    }
    return new ResponseEntity<>(departementList, HttpStatus.OK);
  }

}
