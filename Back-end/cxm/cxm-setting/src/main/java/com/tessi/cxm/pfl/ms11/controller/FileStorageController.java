package com.tessi.cxm.pfl.ms11.controller;

import com.tessi.cxm.pfl.shared.model.ResourceLibraryDto;
import com.tessi.cxm.pfl.ms11.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/v1/storage")
@RequiredArgsConstructor
@Tag(name = "File Storage", description = "Manage server resource files.")
public class FileStorageController {

  private final FileStorageService fileStorageService;

  @Operation(
      summary = "Upload single file.",
      description = "Upload single file.",
      tags = {"File Storage"},
      requestBody =
      @RequestBody(content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)))
  @PostMapping(
      value = "/store",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ResourceLibraryDto> uploadFile(@RequestParam("file") MultipartFile file,
      @RequestParam("type") String type) {
    return ResponseEntity.ok(this.fileStorageService.storeFile(file, type));
  }

  @DeleteMapping("/{fileId}")
  public ResponseEntity<HttpStatus> deleteFile(@PathVariable String fileId) {
    this.fileStorageService.deleteTemporaryFile(fileId);
    return ResponseEntity.ok(HttpStatus.NO_CONTENT);
  }
}
