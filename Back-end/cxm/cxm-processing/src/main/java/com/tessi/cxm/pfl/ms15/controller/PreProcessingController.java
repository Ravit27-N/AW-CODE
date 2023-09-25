package com.tessi.cxm.pfl.ms15.controller;

import com.cxm.tessi.pfl.shared.flowtreatment.model.request.PreProcessingRequest;
import com.tessi.cxm.pfl.ms15.service.PreProcessingService;
import com.cxm.tessi.pfl.shared.flowtreatment.model.response.FlowProcessingResponse;
import com.cxm.tessi.pfl.shared.flowtreatment.model.response.PortalDocumentResponse;
import com.tessi.cxm.pfl.shared.filectrl.model.Document;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/pre-processing")
@Tag(
    name = "Pre-Treatment",
    description = "Manage the preparation of document splitting and metadata of the Flow")
public class PreProcessingController {

  private final PreProcessingService preProcessingService;

  /**
   * Endpoint used to get documents from csv file with instruction of document.
   *
   * @param flowType refer to type of flow
   * @param fileId refer to identity of file
   * @param idCreator refer to identity of invoke user
   * @param channel refer to channel of document
   * @param subChannel refer to sub channel of document
   * @see PreProcessingService
   * @return {@link Document}
   */
  @GetMapping("/document")
  public ResponseEntity<FlowProcessingResponse<Document>> getDocument(
      @RequestParam("modelName") String modelName,
      @RequestParam("flowType") String flowType,
      @RequestParam("fileId") String fileId,
      @RequestParam("idCreator") Long idCreator,
      @RequestParam("channel") String channel,
      @RequestParam("subChannel") String subChannel,
      @RequestParam("funcKey") String funcKey,
      @RequestParam("privKey") String privKey) {
    return ResponseEntity.ok(
        this.preProcessingService.getDocuments(
            modelName, flowType, fileId, idCreator, channel, subChannel, funcKey, privKey));
  }

  /**
   * Endpoint used to get documents from PDF file with instruction of document.
   *
   * @param flowType refer to type of flow
   * @param fileId refer to identity of file
   * @param idCreator refer to identity of invoke user
   * @see PreProcessingService
   * @return {@link Document}
   */
  @GetMapping("/document/portal")
  public ResponseEntity<FlowProcessingResponse<PortalDocumentResponse>> getDocumentPortal(
      @RequestBody PreProcessingRequest preProcessingRequest,
      @RequestParam("modelName") String modelName,
      @RequestParam("flowType") String flowType,
      @RequestParam("fileId") String fileId,
      @RequestParam("idCreator") Long idCreator,
      @RequestParam(value = "funcKey", required = false) String funcKey,
      @RequestParam(value = "privKey", required = false) String privKey) {
    return ResponseEntity.ok(
        this.preProcessingService.getDocumentsPortal(
            preProcessingRequest,
            modelName,
            flowType,
            fileId,
            idCreator,
            funcKey,
            privKey));
  }
}
