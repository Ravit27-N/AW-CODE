package com.tessi.cxm.pfl.ms11.controller;

import com.tessi.cxm.pfl.shared.model.ResourceLibraryDto;
import com.tessi.cxm.pfl.ms11.dto.ResourceParam;
import com.tessi.cxm.pfl.ms11.service.ResourceLibraryService;
import com.tessi.cxm.pfl.shared.utils.EntityResponseHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/resources")
@RequiredArgsConstructor
public class ResourceLibraryController {

  private final ResourceLibraryService resourceLibraryService;

  @GetMapping("")
  public ResponseEntity<EntityResponseHandler<ResourceLibraryDto>> findAll(
      @RequestParam(required = false) Optional<Integer> page,
      @RequestParam(required = false) Optional<Integer> pageSize,
      @RequestParam(defaultValue = "desc") String sortDirection,
      @RequestParam(defaultValue = "") String filter,
      @RequestParam(value = "language", required = false) Optional<String> language,
      @RequestParam(defaultValue = "") List<String> types,
      @RequestParam(defaultValue = "createdAt") String sortByField) {

    if (page.isPresent() && pageSize.isPresent()) {
      ResourceParam resourceParam =
          new ResourceParam(
              page.get() - 1,
              pageSize.get(),
              sortDirection,
              filter,
              language.orElse(""),
              types,
              sortByField);
      return ResponseEntity.ok(
          new EntityResponseHandler<>(this.resourceLibraryService.findAll(resourceParam)));
    }

    return ResponseEntity.ok(
        new EntityResponseHandler<>(
            this.resourceLibraryService.findAll(
                Sort.by(Sort.Direction.fromString(sortDirection), sortByField), filter, types)));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ResourceLibraryDto> findById(@PathVariable Long id) {
    return ResponseEntity.ok(this.resourceLibraryService.findById(id));
  }

  @PostMapping
  public ResponseEntity<ResourceLibraryDto> save(@Validated @RequestBody ResourceLibraryDto dto) {
    return ResponseEntity.ok(resourceLibraryService.save(dto));
  }

  @GetMapping("/duplicate/{label}")
  public ResponseEntity<Boolean> checkDuplicateLabel(
      @PathVariable String label, @RequestParam(value = "type") String type) {
    return ResponseEntity.ok(resourceLibraryService.checkDuplicateLabel(label, type));
  }

  @DeleteMapping("/{fileId}")
  public ResponseEntity<HttpStatus> deleteResourceLibrary(
      @PathVariable String fileId, @RequestHeader HttpHeaders headers) {
    this.resourceLibraryService.deleteResourceByFileId(
        fileId, headers.getFirst(HttpHeaders.AUTHORIZATION));
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Get a file content as text in UTF-8 format.
   *
   * @return File content as text in UTF-8 format.
   */
  @Operation(
      operationId = "getFileContent",
      summary = "Get file content.",
      description = "Get file content.",
      parameters = {
        @Parameter(name = "fileId", in = ParameterIn.PATH, schema = @Schema(type = "string")),
      },
      responses =
          @ApiResponse(
              responseCode = "200",
              headers = {
                @Header(
                    name = HttpHeaders.CONTENT_DISPOSITION,
                    schema =
                        @Schema(type = "file", example = "attachment", defaultValue = "attachment"))
              },
              content =
                  @Content(
                      mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE,
                      schema =
                          @Schema(
                              type = "file",
                              format = "binary",
                              description = "File Contents."))))
  @GetMapping(value = "/file/{fileId}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public ResponseEntity<String> getFileContent(
      @PathVariable("fileId") String fileId, @RequestHeader HttpHeaders headers) {
    return ResponseEntity.ok(
        this.resourceLibraryService.getFileContent(
            fileId, headers.getFirst(HttpHeaders.AUTHORIZATION)));
  }

  @GetMapping("/{fileId}/resource")
  public ResponseEntity<ResourceLibraryDto> findById(@PathVariable String fileId) {
    return ResponseEntity.ok(this.resourceLibraryService.getResourceByFileId(fileId));
  }

  @GetMapping("/technicalName/{fileId}")
  public ResponseEntity<Map<String, String>> getTechnicalName(@PathVariable String fileId) {
    return ResponseEntity.ok(this.resourceLibraryService.getResourceTechnicalName(fileId));
  }

  @GetMapping("/fileIds")
  public ResponseEntity<List<ResourceLibraryDto>> getAllByFileIds(
      @RequestParam("fileIds") List<String> fileIds) {
    return ResponseEntity.ok(this.resourceLibraryService.findAll(fileIds));
  }
}
