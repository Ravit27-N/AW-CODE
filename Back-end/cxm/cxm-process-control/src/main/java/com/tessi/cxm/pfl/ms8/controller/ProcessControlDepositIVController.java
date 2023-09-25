package com.tessi.cxm.pfl.ms8.controller;

import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants;
import com.cxm.tessi.pfl.shared.flowtreatment.model.request.DepositedFlowLaunchRequest;
import com.cxm.tessi.pfl.shared.flowtreatment.model.response.FlowProcessingResponse;
import com.cxm.tessi.pfl.shared.flowtreatment.model.response.ProcessCtrlIdentificationResponse;
import com.tessi.cxm.pfl.ms8.service.ProcessControlService;
import com.tessi.cxm.pfl.shared.utils.BearerAuthentication;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint to handle process of deposit IV.
 *
 * @author Piseth KHON
 * @version 1.11.0
 * @since 26 oct 2022
 */
@RestController
@RequestMapping("/v1/process-control/iv")
@RequiredArgsConstructor
public class ProcessControlDepositIVController {

  private final ProcessControlService processControlService;

  @PostMapping("/identify-flow")
  public ResponseEntity<FlowProcessingResponse<ProcessCtrlIdentificationResponse>>
  identifyDepositedFlow(
      @RequestBody DepositedFlowLaunchRequest requestDto, @RequestHeader HttpHeaders headers) {

    var authorizationToken = this.getAuthorizationToken(headers);

    var result = this.processControlService.identifyDepositedFlow(requestDto, authorizationToken);
    if (result != null) {
      return ResponseEntity.ok(
          new FlowProcessingResponse<>(
              FlowTreatmentConstants.Message.FINISHED, HttpStatus.OK, result));
    }
    return ResponseEntity.ok(
        new FlowProcessingResponse<>(
            FlowTreatmentConstants.Message.FAILED, HttpStatus.INTERNAL_SERVER_ERROR));
  }

  private String getAuthorizationToken(HttpHeaders headers) {
    var authorizationToken = headers.getFirst(HttpHeaders.AUTHORIZATION);
    if (StringUtils.isNotBlank(authorizationToken)) {
      authorizationToken = authorizationToken.replace(BearerAuthentication.PREFIX_TOKEN, "");
    }
    return authorizationToken;
  }
}
