package com.allweb.rms.controller;

import com.allweb.rms.entity.dto.JobDescriptionDTO;
import com.allweb.rms.service.JobDescriptionService;
import com.allweb.rms.utils.EntityResponseHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/jobDescription")
public class JobDescriptionController {

  /* *
   *
   * JobDescription class controller
   *
   * > Do business logic ....
   *
   */

  private final JobDescriptionService service;

  @Autowired // Object Injection by constructor
  public JobDescriptionController(JobDescriptionService service) {
    this.service = service;
  }

  @Operation(
      operationId = "getJobDescription",
      description = "Get Job Description By Id",
      tags = "Job Description",
      parameters = {
        @Parameter(in = ParameterIn.PATH, name = "id", description = "Job Description id")
      })
  @GetMapping("/{id}")
  public ResponseEntity<JobDescriptionDTO> getJobDescription(@PathVariable("id") int id) {
    return new ResponseEntity<>(service.getJobDescription(id), HttpStatus.OK);
  }

  @Operation(
      operationId = "getJobDescriptions",
      description = "Get all Job Description",
      tags = {"Job Description"},
      parameters = {
        @Parameter(in = ParameterIn.QUERY, name = "page", description = "Page index"),
        @Parameter(in = ParameterIn.QUERY, name = "pageSize", description = "Page size"),
        @Parameter(in = ParameterIn.QUERY, name = "filter", description = "Filter"),
        @Parameter(in = ParameterIn.QUERY, name = "sortDirection", description = "Direction Sort"),
        @Parameter(
            in = ParameterIn.QUERY,
            name = "sortByField",
            description = "Name field that to sort")
      })
  @GetMapping
  public ResponseEntity<EntityResponseHandler<EntityModel<JobDescriptionDTO>>> getJobDescriptions(
      @RequestParam(value = "page", required = false, defaultValue = "1") int page,
      @RequestParam(value = "pageSize", required = false, defaultValue = "10") int size,
      @RequestParam(value = "filter", required = false) String filter,
      @RequestParam(value = "sortDirection", required = false, defaultValue = "desc")
          String sortDirection,
      @RequestParam(value = "sortByField", required = false, defaultValue = "createdAt")
          String sortByField,
      @RequestParam(value = "active", required = false) List<Boolean> active) {

    return new ResponseEntity<>(
        service.getJobDescriptions(
            page,
            size,
            filter,
            sortDirection,
            sortByField,
            (active == null ? new ArrayList<>() : active)),
        HttpStatus.OK);
  }

  @Operation(
      operationId = "saveJobDescription",
      description = "Save Job Description",
      tags = "Job Description")
  @PostMapping
  public ResponseEntity<JobDescriptionDTO> saveJobDescription(
      @Valid @RequestBody JobDescriptionDTO jobDescription) {
    return new ResponseEntity<>(service.save(jobDescription), HttpStatus.CREATED);
  }

  @Operation(
      operationId = "updateJonDescription",
      description = "Update Job Description",
      tags = "Job Description")
  @PutMapping
  public ResponseEntity<JobDescriptionDTO> updateJonDescription(
      @Valid @RequestBody JobDescriptionDTO jobDescriptionDTO) {
    return new ResponseEntity<>(service.save(jobDescriptionDTO), HttpStatus.CREATED);
  }

  @Operation(
      operationId = "updateActive",
      description = "Update Active",
      tags = "Job Description",
      parameters = {
        @Parameter(in = ParameterIn.PATH, name = "id", description = "Job Description id"),
        @Parameter(
            in = ParameterIn.PATH,
            name = "active",
            description = "Job Description active status"),
      })
  @PatchMapping("/{id}/active/{active}")
  public ResponseEntity<JobDescriptionDTO> updateActive(
      @PathVariable("id") int id, @PathVariable("active") boolean active) {

    return new ResponseEntity<>(service.updateActive(id, active), HttpStatus.CREATED);
  }

  @Operation(
      operationId = "delete",
      description = "Delete Job Description",
      tags = "Job Description",
      parameters = {
        @Parameter(in = ParameterIn.PATH, name = "id", description = "Job Description id")
      })
  @DeleteMapping("/{id}")
  public ResponseEntity<HttpStatus> delete(@PathVariable int id) {
    service.delete(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Operation(
      operationId = "uploadJobFile",
      description = "upload file JobDescription",
      tags = "Job Description")
  @PostMapping(value = "/file/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<Map<String, String>> uploadJobFile(@RequestPart MultipartFile file) {
    return new ResponseEntity<>(service.uploadFile(file), HttpStatus.OK);
  }

  @Operation(
      operationId = "remove",
      description = "remove file from job description by id",
      tags = "Job Description",
      parameters = {
        @Parameter(in = ParameterIn.PATH, name = "id", description = "jobDescription id"),
        @Parameter(in = ParameterIn.PATH, name = "filename", description = "filename")
      })
  @DeleteMapping("/{id}/remove/{filename}")
  public ResponseEntity<Void> removeAttachFile(
      @PathVariable int id, @PathVariable String filename) {
    service.removeFile(id, filename);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Operation(
      operationId = "uploadProfileOnUpdate",
      description = "upload profile job description, when update candidate",
      tags = "Job Description",
      parameters = {
        @Parameter(in = ParameterIn.PATH, name = "id", description = "jobDescription id")
      })
  @PutMapping(value = "/{id}/profile/onUpdate", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<Map<String, String>> uploadProfileOnUpdate(
      @PathVariable int id, @RequestPart MultipartFile filename) {
    return new ResponseEntity<>(service.uploadProfileOnUpdate(id, filename), HttpStatus.OK);
  }

  @Operation(
      operationId = "loadFile",
      description = "load or view content of file",
      tags = "Job Description",
      parameters = {
        @Parameter(in = ParameterIn.PATH, name = "id", description = "jobDescription id"),
        @Parameter(in = ParameterIn.PATH, name = "filename", description = "filename")
      })
  @SneakyThrows
  @GetMapping("/{id}/view/{filename}")
  public ResponseEntity<Resource> loadFile(
      @PathVariable String id, @PathVariable String filename, HttpServletRequest request) {
    Map<String, Object> resourceMap = service.loadFile(id, filename, request);
    Resource resource = (Resource) resourceMap.get("resource");
    return ResponseEntity.ok()
        .contentType(MediaType.parseMediaType((String) resourceMap.get("contentType")))
        .body(resource);
  }

  @Operation(
      operationId = "downloadFile",
      description = "download or view content of file",
      tags = "Job Description",
      parameters = {
        @Parameter(in = ParameterIn.PATH, name = "id", description = "jobDescription id"),
        @Parameter(in = ParameterIn.PATH, name = "filename", description = "filename")
      })
  @SneakyThrows
  @GetMapping("/{id}/download/{filename}")
  public ResponseEntity<Resource> downloadFile(
      @PathVariable String id, @PathVariable String filename, HttpServletRequest request) {
    Map<String, Object> resourceMap = service.loadFile(id, filename, request);
    Resource resource = (Resource) resourceMap.get("resource");
    return ResponseEntity.ok()
        .contentType(MediaType.parseMediaType((String) resourceMap.get("contentType")))
        .header(
            HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=\"" + resource.getFilename() + "\"")
        .body(resource);
  }
}
