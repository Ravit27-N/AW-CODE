package com.tessi.cxm.pfl.ms8.service.restclient;

import com.cxm.tessi.pfl.shared.flowtreatment.ProcessCtrlPreProcessing;
import com.cxm.tessi.pfl.shared.flowtreatment.model.request.CampaignPreProcessingRequest;
import com.cxm.tessi.pfl.shared.flowtreatment.model.request.PreProcessingRequest;
import com.cxm.tessi.pfl.shared.flowtreatment.model.request.ProcessingRequest;
import com.cxm.tessi.pfl.shared.flowtreatment.model.response.CampaignPreProcessingResponse;
import com.cxm.tessi.pfl.shared.flowtreatment.model.response.FlowProcessingResponse;
import com.cxm.tessi.pfl.shared.flowtreatment.model.response.PortalDocumentResponse;
import com.cxm.tessi.pfl.shared.flowtreatment.model.response.ProcessCtrlIdentificationResponse;
import com.tessi.cxm.pfl.shared.model.ResponseDocumentProcessingPortal;
import com.tessi.cxm.pfl.shared.service.restclient.FeignClientConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

@FeignClient(FeignClientConstants.CXM_PROCESSING)
public interface ProcessingFeignClient {

  /* Processing endpoint */
  @PostMapping("/api/v1/cxm-processing")
  FlowProcessingResponse<ResponseDocumentProcessingPortal> processing(
      @RequestParam(value = "idCreator") Long idCreator,
      @RequestParam(value = "composedFileId") String composedFileId,
      @RequestBody ProcessingRequest processingRequest,
      @RequestParam(value = FeignClientConstants.FUNCTIONALITY_KEY, required = false) String functionalityKey,
      @RequestParam(value = FeignClientConstants.PRIVILEGE_KEY, required = false) String privilegeKey,
      @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String tokenHeader);

  /* Identification endpoint */
  @GetMapping("/api/v1/identification/process-ctrl/{fileId}/{idCreator}")
  FlowProcessingResponse<ProcessCtrlIdentificationResponse> getChannelAndSubChannel(
      @PathVariable("fileId") String fileId,
      @RequestParam("flowType") String flowType,
      @PathVariable("idCreator") Long idCreator,
      @RequestParam(value = "flowName", defaultValue = "") String flowName,
      @RequestParam(value = FeignClientConstants.FUNCTIONALITY_KEY, required = false) String functionalityKey,
      @RequestParam(value = FeignClientConstants.PRIVILEGE_KEY, required = false) String privilegeKey,
      @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String tokenHeader);

  /* Pre-processing endpoint */
  @GetMapping("/api/v1/pre-processing/document")
  FlowProcessingResponse<ProcessCtrlPreProcessing> getDocument(
      @RequestParam("modelName") String modelName,
      @RequestParam("flowType") String flowType,
      @RequestParam("fileId") String fileId,
      @RequestParam("idCreator") Long idCreator,
      @RequestParam("channel") String channel,
      @RequestParam("subChannel") String subChannel,
      @RequestParam(value = FeignClientConstants.FUNCTIONALITY_KEY, required = false) String functionalityKey,
      @RequestParam(value = FeignClientConstants.PRIVILEGE_KEY, required = false) String privilegeKey,
      @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String tokenHeader);

  @GetMapping(value = "/api/v1/pre-processing/document/portal")
  FlowProcessingResponse<PortalDocumentResponse> getPortalDocument(
      @RequestBody PreProcessingRequest preProcessingRequest,
      @RequestParam("modelName") String modelName,
      @RequestParam("flowType") String flowType,
      @RequestParam("fileId") String fileId,
      @RequestParam("idCreator") Long idCreator,
      @RequestParam(value = FeignClientConstants.FUNCTIONALITY_KEY, required = false)
          String functionalityKey,
      @RequestParam(value = FeignClientConstants.PRIVILEGE_KEY, required = false)
          String privilegeKey,
      @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String tokenHeader);

  @PostMapping(value = "/api/v1/pre-processing/document/portal/campaign")
  FlowProcessingResponse<CampaignPreProcessingResponse> getPortalCampaignDocument(
      @RequestBody @Valid CampaignPreProcessingRequest request,
      @RequestParam(value = FeignClientConstants.FUNCTIONALITY_KEY, required = false) String functionalityKey,
      @RequestParam(value = FeignClientConstants.PRIVILEGE_KEY, required = false) String privilegeKey,
      @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String tokenHeader);
}
