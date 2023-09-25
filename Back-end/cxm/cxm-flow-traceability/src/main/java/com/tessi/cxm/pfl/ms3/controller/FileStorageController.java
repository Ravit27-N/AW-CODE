package com.tessi.cxm.pfl.ms3.controller;

import com.tessi.cxm.pfl.ms3.service.FlowTraceabilityStorageFileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/storage")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "File Storage", description = "Manage server resource files.")
public class FileStorageController {

  private final FlowTraceabilityStorageFileService flowTraceabilityStorageFileService;

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
  @GetMapping(
      value = "/file/{fileId}",
      produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public ResponseEntity<String> getFileContent(
      HttpServletRequest request,
      @PathVariable("fileId") String fileId,
      @RequestParam(value = "flowType", defaultValue = "") String flowType,
      @RequestParam(value = "type", defaultValue = "flowTraceability") String type,
      @RequestHeader HttpHeaders headers) {
    return ResponseEntity.ok(
        this.flowTraceabilityStorageFileService.getFileContent(fileId, flowType,
            headers.getFirst(HttpHeaders.AUTHORIZATION), type));
  }
}
