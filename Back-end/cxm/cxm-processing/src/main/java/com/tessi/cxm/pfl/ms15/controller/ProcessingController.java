package com.tessi.cxm.pfl.ms15.controller;

import com.cxm.tessi.pfl.shared.flowtreatment.model.request.ProcessingRequest;
import com.tessi.cxm.pfl.ms15.model.ResponseDocumentWrapper;
import com.tessi.cxm.pfl.ms15.service.ProcessingService;
import com.cxm.tessi.pfl.shared.flowtreatment.model.response.FlowProcessingResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/cxm-processing")
@AllArgsConstructor
@Tag(name = "Processing", description = "Manage the document processing of the Flow")
public class ProcessingController {
  private final ProcessingService processingService;

  /**
   * Endpoint use for transform composedFile.
   *
   * @return document/processing with composedId
   */
  @PostMapping
  public ResponseEntity<FlowProcessingResponse<ResponseDocumentWrapper>> processingComposed(
      @RequestParam(value = "idCreator") Long idCreator,
      @RequestParam(value = "composedFileId") String composedFileId,
      @RequestBody ProcessingRequest processingRequest,
      @RequestParam(value = "funcKey", required = false) String funcKey,
      @RequestParam(value = "privKey", required = false) String privKey,
      @RequestHeader HttpHeaders headers) {
    return ResponseEntity.ok(
        this.processingService.process(
            composedFileId,
            idCreator,
            processingRequest,
            funcKey,
            privKey,
            headers.getFirst(HttpHeaders.AUTHORIZATION)));
  }
}
