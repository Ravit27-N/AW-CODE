package com.tessi.cxm.pfl.ms8.controller;

import com.tessi.cxm.pfl.ms8.dto.ResourceFileMetaDataResponseDto;
import com.tessi.cxm.pfl.ms8.service.FileStorageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/v1/storage")
@RequiredArgsConstructor
@Tag(name = "File storage", description = "Manage storage files.")
public class FileStorageController {

  private final FileStorageService fileStorageService;

  @PostMapping(value = "/store/resource", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<ResourceFileMetaDataResponseDto> uploadBackgroundFile(
      @RequestParam("file") MultipartFile file,
      @RequestParam("flowId") String flowId,
      @RequestParam("type") String type) {
    return ResponseEntity.ok(this.fileStorageService.uploadResource(file, flowId, type));
  }

  @DeleteMapping("/store/resource")
  public ResponseEntity<Void> removeFile(@RequestParam("fileId") String fileId,
      @RequestParam("flowId") String flowId) {
    this.fileStorageService.removeFile(fileId, flowId);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
