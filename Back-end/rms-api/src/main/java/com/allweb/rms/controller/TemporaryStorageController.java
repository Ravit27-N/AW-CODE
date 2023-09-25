package com.allweb.rms.controller;

import com.allweb.rms.service.TemporaryStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/temporary")
public class TemporaryStorageController {

  private final TemporaryStorageService temporaryStorageService;

  public TemporaryStorageController(TemporaryStorageService temporaryStorageService) {
    this.temporaryStorageService = temporaryStorageService;
  }

  @Operation(
      operationId = "viewFile",
      description = "view file from temporary storage",
      tags = {"Temporary Storage"},
      parameters = {@Parameter(in = ParameterIn.PATH, name = "filename")})
  @GetMapping("/view/{filename}")
  public ResponseEntity<Resource> viewFile(
      @PathVariable String filename, HttpServletRequest request) throws IOException {

    Resource resource = temporaryStorageService.loadFile(filename);
    String contentType =
        request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
    // Fallback to the default content type if type could not be determined
    if (contentType == null) {
      contentType = "application/octet-stream"; // unknown binary file
    }
    return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)).body(resource);
  }

  @Operation(
      operationId = "deleteFileFromTemporary",
      description = "delete file from temporary storage",
      tags = {"Temporary Storage"},
      parameters = {@Parameter(in = ParameterIn.PATH, name = "filename")})
  @DeleteMapping("/{filename}")
  public ResponseEntity<Void> deleteFileFromTemporary(@PathVariable String filename)
      throws IOException {
    temporaryStorageService.deleteFromTemporaryStorage(filename);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
