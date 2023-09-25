package com.tessi.cxm.pfl.ms8.controller;

import com.tessi.cxm.pfl.ms8.dto.ResourceFileMetaDataResponse;
import com.tessi.cxm.pfl.ms8.dto.ResourceFileMetaDataRequestDto;
import com.tessi.cxm.pfl.ms8.dto.ResourceFileMetaDataResponseDto;
import com.tessi.cxm.pfl.ms8.service.ResourceFileService;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/resource-file")
@RequiredArgsConstructor
@Tag(name = "Resource file", description = "Manage resource file")
public class ResourceFileController {

  private final ResourceFileService resourceFileService;

  @PostMapping
  public ResponseEntity<ResourceFileMetaDataRequestDto> saveResourceFile(
      @RequestBody ResourceFileMetaDataRequestDto dto) {
    ResourceFileMetaDataRequestDto response;
    if (Objects.isNull(dto.getId()) || dto.getId() == 0) {
      response = this.resourceFileService.save(dto);
    } else {
      response = this.resourceFileService.update(dto);
    }
    return ResponseEntity.ok(response);
  }

  @GetMapping("{id}")
  public ResponseEntity<ResourceFileMetaDataResponseDto> getResourceFile(
      @PathVariable("id") long id) {
    return ResponseEntity.ok(this.resourceFileService.getResourceFile(id));
  }

  @DeleteMapping("{id}")
  public ResponseEntity<Void> deleteResourceFile(@PathVariable("id") long id) {
    this.resourceFileService.deleteResourceFile(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @GetMapping("/{flowId}/all")
  public ResponseEntity<ResourceFileMetaDataResponse> getResourceFileByFlowId(
      @PathVariable("flowId") String flowId) {
    return new ResponseEntity<>(
        this.resourceFileService.getResourceFilesByFlowId(flowId), HttpStatus.OK);
  }
}
