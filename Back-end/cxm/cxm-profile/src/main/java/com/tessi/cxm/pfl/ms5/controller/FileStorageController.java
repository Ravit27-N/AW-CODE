package com.tessi.cxm.pfl.ms5.controller;

import com.tessi.cxm.pfl.ms5.dto.FileMetadata;
import com.tessi.cxm.pfl.ms5.service.StorageFileService;
import com.tessi.cxm.pfl.shared.model.FilePropertiesHandling;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/v1/storage")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "File Storage", description = "Manage server resource files.")
public class FileStorageController {

  private final StorageFileService storageFileService;

  /**
   * Endpoint to upload a file.
   *
   * @param file refer to {@link MultipartFile}
   * @return the properties of file
   */
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
  public ResponseEntity<FilePropertiesHandling> singleUpload(
      @RequestParam("file") MultipartFile file, @RequestHeader HttpHeaders headers) {

    return ResponseEntity.ok(
        this.storageFileService.uploadSingleFile(file, headers.getFirst(HttpHeaders.AUTHORIZATION)));
  }


  /**
   * Get a file content as text in UTF-8 format.
   *
   * @return File content as text in UTF-8 format.
   */
  @GetMapping(
      value = "/download",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<FileMetadata> getFileContent() {
    var fileContent = this.storageFileService.getFileContent();

    return fileContent != null ? ResponseEntity.ok(
        fileContent)
        : ResponseEntity.noContent().build();
  }

  @DeleteMapping("/delete/{fileId}")
  public ResponseEntity<Void> deleteFile(@PathVariable("fileId") String fileId, @RequestHeader HttpHeaders headers) {
    this.storageFileService.deleteFile(fileId, headers.getFirst(HttpHeaders.AUTHORIZATION));
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
