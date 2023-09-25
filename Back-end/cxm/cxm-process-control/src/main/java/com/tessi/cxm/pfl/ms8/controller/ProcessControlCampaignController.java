package com.tessi.cxm.pfl.ms8.controller;

import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants.Message;
import com.cxm.tessi.pfl.shared.flowtreatment.model.request.CampaignDepositFlowLaunchRequest;
import com.cxm.tessi.pfl.shared.flowtreatment.model.request.DepositedFlowLaunchRequest;
import com.cxm.tessi.pfl.shared.flowtreatment.model.response.FlowProcessingResponse;
import com.tessi.cxm.pfl.ms8.service.ProcessControlCampaignService;
import com.tessi.cxm.pfl.shared.model.PrivilegeHelper;
import com.tessi.cxm.pfl.shared.service.restclient.FeignClientConstants;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint to handle process of prepare and launch a campaign SMS.
 *
 * @author Vichet CHANN
 * @version 1.5.0
 * @since 25 May 2022
 */
@RestController
@RequestMapping("/v1/process-control/portal")
@RequiredArgsConstructor
public class ProcessControlCampaignController {

  private final ProcessControlCampaignService campaignService;

  @PostMapping("/campaign/initialize")
  public ResponseEntity<FlowProcessingResponse<Object>> initializeCampaign(
      @RequestBody DepositedFlowLaunchRequest request,
      @RequestParam(FeignClientConstants.FUNCTIONALITY_KEY) String functionalityKey,
      @RequestParam(FeignClientConstants.PRIVILEGE_KEY) String privilegeKey,
      @RequestHeader HttpHeaders headers) {
    this.campaignService.initializeCampaign(request, functionalityKey, privilegeKey, headers);
    return ResponseEntity.ok(
        new FlowProcessingResponse<>(Message.INITIALIZE, HttpStatus.OK, Optional.empty()));
  }

  @PostMapping("/campaign/launch")
  public ResponseEntity<FlowProcessingResponse<Object>> launchCampaignSms(
      @RequestBody CampaignDepositFlowLaunchRequest request,
      @RequestParam(FeignClientConstants.FUNCTIONALITY_KEY) String functionalityKey,
      @RequestParam(FeignClientConstants.PRIVILEGE_KEY) String privilegeKey,
      @RequestHeader HttpHeaders headers) {
    PrivilegeHelper privilegeHelper = PrivilegeHelper.builder()
        .funcKey(functionalityKey)
        .privKey(privilegeKey)
        .build();
    this.campaignService.launchCampaign(request, privilegeHelper, headers);
    return ResponseEntity.ok(
        new FlowProcessingResponse<>(Message.LAUNCHED, HttpStatus.OK, Optional.empty()));
  }
}
