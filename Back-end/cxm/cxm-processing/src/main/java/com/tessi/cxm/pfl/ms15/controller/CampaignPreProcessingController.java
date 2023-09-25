package com.tessi.cxm.pfl.ms15.controller;

import com.cxm.tessi.pfl.shared.flowtreatment.model.request.CampaignPreProcessingRequest;
import com.cxm.tessi.pfl.shared.flowtreatment.model.response.CampaignPreProcessingResponse;
import com.cxm.tessi.pfl.shared.flowtreatment.model.response.FlowProcessingResponse;
import com.tessi.cxm.pfl.ms15.service.CampaignPreProcessingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/pre-processing/document/portal/campaign")
@RequiredArgsConstructor
@Tag(name = "Pre-Treatment", description = "The API endpoints to manage parameter")
public class CampaignPreProcessingController {

  private final CampaignPreProcessingService service;

  @PostMapping
  public ResponseEntity<FlowProcessingResponse<CampaignPreProcessingResponse>> getDocuments(
      @RequestBody @Valid CampaignPreProcessingRequest request,
      @RequestParam(value = "funcKey", required = false) String funcKey,
      @RequestParam(value = "privKey", required = false) String privKey,
      @RequestHeader HttpHeaders headers) {
    return ResponseEntity.ok(
        this.service.getDocuments(
            request, headers.getFirst(HttpHeaders.AUTHORIZATION), funcKey, privKey));
  }
}
