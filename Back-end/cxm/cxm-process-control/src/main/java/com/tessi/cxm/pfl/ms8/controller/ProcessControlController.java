package com.tessi.cxm.pfl.ms8.controller;

import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants.Message;
import com.cxm.tessi.pfl.shared.flowtreatment.model.request.DepositedFlowLaunchRequest;
import com.cxm.tessi.pfl.shared.flowtreatment.model.request.ValidationFlowDocumentRequest;
import com.cxm.tessi.pfl.shared.flowtreatment.model.request.ValidationFlowRequest;
import com.cxm.tessi.pfl.shared.flowtreatment.model.response.FlowProcessingResponse;
import com.cxm.tessi.pfl.shared.flowtreatment.model.response.PortalDocumentResponse;
import com.cxm.tessi.pfl.shared.flowtreatment.model.response.ProcessCtrlIdentificationResponse;
import com.tessi.cxm.pfl.ms8.model.DepositStepResponse;
import com.tessi.cxm.pfl.ms8.model.FlowAnalyzeRequest;
import com.tessi.cxm.pfl.ms8.model.FlowTreatmentFlowRequestWrapper;
import com.tessi.cxm.pfl.shared.model.ResponseDocumentProcessingPortal;
import com.tessi.cxm.pfl.ms8.model.SwitchFlowResponse;
import com.tessi.cxm.pfl.ms8.service.FlowUnloadingService;
import com.tessi.cxm.pfl.ms8.service.ProcessControlService;
import com.tessi.cxm.pfl.ms8.service.UnloadingSchedulerService;
import com.tessi.cxm.pfl.ms8.service.manual.ProcessControlServiceManual;
import com.tessi.cxm.pfl.shared.exception.BadRequestException;
import com.tessi.cxm.pfl.shared.model.SharedClientUnloadDetailsDTO;
import com.tessi.cxm.pfl.shared.model.SharedUnloadDate;
import com.tessi.cxm.pfl.shared.utils.BearerAuthentication;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/process-control")
public class ProcessControlController {

  private final ProcessControlService processControlService;
  private final ProcessControlServiceManual processControlServiceManuallyImp;

  private final UnloadingSchedulerService flowUnloadingService;

  private final FlowUnloadingService forceClientUnloading;

  @GetMapping
  public ResponseEntity<String> welcome() {
    return ResponseEntity.ok("Hello from Process-Control (:");
  }

  @PostMapping("/launch")
  public ResponseEntity<FlowProcessingResponse<Object>> launch(
      @RequestBody DepositedFlowLaunchRequest requestDto) {
    log.info("Call by acquisition");
    this.processControlService.launch(requestDto);
    return ResponseEntity.ok(
        new FlowProcessingResponse<>(Message.LAUNCHED, HttpStatus.OK, Optional.empty()));
  }

  @PostMapping("/launch/manual")
  public ResponseEntity<FlowProcessingResponse<ProcessCtrlIdentificationResponse>> manualLaunch(
      @RequestBody DepositedFlowLaunchRequest requestDto, @RequestHeader HttpHeaders headers) {
    log.info("Call by acquisition");
    return ResponseEntity.ok(
        new FlowProcessingResponse<>(
            Message.FINISHED,
            HttpStatus.OK,
            this.processControlServiceManuallyImp.identityLauncher(
                requestDto, headers.getFirst(HttpHeaders.AUTHORIZATION))));
  }

  @PostMapping("/identify-flow")
  public ResponseEntity<FlowProcessingResponse<ProcessCtrlIdentificationResponse>>
      identifyDepositedFlow(
          @RequestBody DepositedFlowLaunchRequest requestDto, @RequestHeader HttpHeaders headers) {
    var authorizationToken = this.getAuthorizationToken(headers);

    var result = this.processControlService.identifyDepositedFlow(requestDto, authorizationToken);
    if (result != null) {
      return ResponseEntity.ok(
          new FlowProcessingResponse<>(Message.FINISHED, HttpStatus.OK, result));
    }
    return ResponseEntity.ok(
        new FlowProcessingResponse<>(Message.FAILED, HttpStatus.INTERNAL_SERVER_ERROR));
  }

  @PutMapping("/re-identify-flow/{uuid}")
  public ResponseEntity<FlowProcessingResponse<Map<String, Boolean>>> reIdentifyDepositedFlow(
      @PathVariable String uuid, @RequestHeader HttpHeaders headers) {
    var authorizationToken = this.getAuthorizationToken(headers);

    var result = this.processControlService.reIdentifyDepositedFlow(uuid, authorizationToken);
    if (result != null) {
      return ResponseEntity.ok(
          new FlowProcessingResponse<>(Message.FINISHED, HttpStatus.OK, result));
    }
    return ResponseEntity.ok(
        new FlowProcessingResponse<>(Message.FAILED, HttpStatus.INTERNAL_SERVER_ERROR));
  }

  @PostMapping("/analyse-flow")
  public ResponseEntity<FlowProcessingResponse<PortalDocumentResponse>> analyzeFlow(
      FlowAnalyzeRequest analyzeRequest,
      @RequestParam(value = "isModify", defaultValue = "false") boolean isModify,
      @RequestHeader HttpHeaders headers) {
    var authorizationToken = this.getAuthorizationToken(headers);

    var analyzedResult =
        this.processControlService.analyzeDepositedFlow(
            analyzeRequest, authorizationToken, isModify);

    if (analyzedResult != null) {
      return ResponseEntity.ok(
          new FlowProcessingResponse<>(Message.FINISHED, HttpStatus.OK, analyzedResult));
    }
    return ResponseEntity.ok(
        new FlowProcessingResponse<>(Message.FAILED, HttpStatus.INTERNAL_SERVER_ERROR));
  }

  @PostMapping("/treatment-flow")
  public ResponseEntity<FlowProcessingResponse<ResponseDocumentProcessingPortal>> processingFlow(
      @Validated @RequestBody FlowTreatmentFlowRequestWrapper treatmentFlowRequest,
      @RequestParam(value = "isModify", defaultValue = "false") boolean isModify,
      @RequestHeader HttpHeaders headers) {
    var authorizationToken = this.getAuthorizationToken(headers);
    final ResponseDocumentProcessingPortal portalDocumentResponse =
        this.processControlService.processingFlow(
            treatmentFlowRequest, authorizationToken, isModify);
    if (portalDocumentResponse != null) {
      return ResponseEntity.ok(
          new FlowProcessingResponse<>(Message.FINISHED, HttpStatus.OK, portalDocumentResponse));
    }
    return ResponseEntity.ok(
        new FlowProcessingResponse<>(Message.FAILED, HttpStatus.INTERNAL_SERVER_ERROR));
  }

  @GetMapping(value = "/send-flow", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<SwitchFlowResponse> sendFlow(
      @RequestParam(value = "uuid") String uuid,
      @RequestParam(value = "validation", defaultValue = "false") boolean validation,
      @RequestParam(value = "isModify", defaultValue = "false") boolean isModify,
      @RequestParam(value = "composedFileId") String composedFileId) {
    return ResponseEntity.ok(
        this.processControlService.sendFlow(uuid, composedFileId, validation, isModify));
  }

  private String getAuthorizationToken(HttpHeaders headers) {
    var authorizationToken = headers.getFirst(HttpHeaders.AUTHORIZATION);
    if (StringUtils.isNotBlank(authorizationToken)) {
      authorizationToken = authorizationToken.replace(BearerAuthentication.PREFIX_TOKEN, "");
    }
    return authorizationToken;
  }

  @PostMapping("/leave-deposit/{uuid}/{step}")
  public ResponseEntity<Void> updateStep(
      @PathVariable("uuid") String uuid,
      @PathVariable("step") int step,
      @RequestParam(value = "composedFileId", required = false) String composedFileId,
      @RequestParam(value = "validation", required = false) boolean validation) {
    this.processControlService.updateDepositPortalFlowStep(
        uuid, step, Optional.ofNullable(composedFileId), validation);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/leave-campaign/{uuid}/{step}")
  public ResponseEntity<Void> updateStepByCampaignProcess(
      @PathVariable("uuid") String uuid,
      @PathVariable("step") int step,
      @RequestParam(value = "composedFileId", required = false) String composedFileId,
      @RequestParam(value = "type", required = false) String type) {
    this.processControlService.updateDepositPortalFlowStep(
        uuid, step, Optional.ofNullable(composedFileId), false);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/deposit/{uuid}/{step}")
  public ResponseEntity<DepositStepResponse> getDepositStep(
      @PathVariable("uuid") String uuid,
      @PathVariable("step") int step,
      @RequestHeader HttpHeaders headers) {
    /*    final PortalFlowFileControl depositStep =
        this.processControlService.getDepositStep(
            uuid, headers.getFirst(HttpHeaders.AUTHORIZATION));
    return ResponseEntity.ok(new DepositStepResponse(depositStep, step));*/

    return new ResponseEntity<>(
        this.processControlService.getDepositStepResponse(
            uuid, headers.getFirst(HttpHeaders.AUTHORIZATION), step),
        HttpStatus.OK);
  }

  /**
   * Endpoint to produce message to cancel a flow in cxm-flow-traceability.
   *
   * @param uuid refer the uuid of a JSON file control
   * @return OK status if there are not any exceptions occur
   */
  @PutMapping("/cancel/{uuid}")
  public ResponseEntity<Void> cancelFlow(
      @PathVariable("uuid") String uuid) {
    this.processControlService.cancelFlow(uuid);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/validation-flow")
  public ResponseEntity<String> validateFlow(
      @RequestBody ValidationFlowRequest validationFlowRequest) {
    return ResponseEntity.ok(this.processControlService.validationFlow(validationFlowRequest));
  }

  @PostMapping("/validate-flow-document")
  public ResponseEntity<String> validateFlowDocument(
      @RequestBody ValidationFlowDocumentRequest validationFlowDocumentRequest,
      @RequestHeader(required = false) HttpHeaders headers) {
    return ResponseEntity.ok(
        this.processControlService.validateFlowDocument(validationFlowDocumentRequest, headers));
  }

  @PostMapping("/set-client-unload-schedule")
  public ResponseEntity<Void> setClientScheduleUnloads(
      @RequestBody SharedClientUnloadDetailsDTO clientUnload) {
    this.flowUnloadingService.scheduleFlowUnloading(clientUnload);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @DeleteMapping("/flow-unloading/{clientId}")
  public ResponseEntity<Object> unscheduleFlowUnloading(@PathVariable long clientId) {
    this.flowUnloadingService.unscheduleFlowUnloading(clientId);

    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/flow-unloading/{clientId}/force-schedule")
  public ResponseEntity<Void> forceUnloadingSchedule(@PathVariable("clientId") long clientId) {
    if (clientId <= 0) {
      throw new BadRequestException("Client's id is invalid with id: " + clientId);
    }

    this.forceClientUnloading.forceUnloadFlow(clientId, new Date());
    return ResponseEntity.ok().build();
  }

  @GetMapping("/get-nearest-client-unloading")
  public ResponseEntity<SharedUnloadDate> getNearestClientUnloadingDate(
      @RequestHeader HttpHeaders headers) {
    return new ResponseEntity<>(
        flowUnloadingService.getNearestClientUnloadingDate(
            BearerAuthentication.getTokenWithPrefix(headers)),
        HttpStatus.OK);
  }
}
