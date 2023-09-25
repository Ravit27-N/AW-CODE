package com.tessi.cxm.pfl.ms8.service.restclient;

import com.cxm.tessi.pfl.shared.flowtreatment.FlowFileControl;
import com.cxm.tessi.pfl.shared.flowtreatment.ProcessCtrlIdentificationStep;
import com.cxm.tessi.pfl.shared.flowtreatment.ProcessCtrlPreProcessing;
import com.cxm.tessi.pfl.shared.flowtreatment.Processing;
import com.cxm.tessi.pfl.shared.flowtreatment.model.response.FileManagerResponse;
import com.tessi.cxm.pfl.shared.filectrl.model.portal.PortalFlowFileControl;
import com.tessi.cxm.pfl.shared.filectrl.model.portal.PortalFlowFileTracing;
import com.tessi.cxm.pfl.shared.service.restclient.FeignClientConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(FeignClientConstants.CXM_FILE_CTRL_MANAGEMENT)
public interface FileCtrlMngtFeignClient {

  @PostMapping("/api/v1/file-control-management/create-json-file")
  void createJsonFile(
      FlowFileControl create,
      @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String tokenHeader);

  @PostMapping("/api/v1/file-control-management/portal/json-file")
  PortalFlowFileControl createPortalJsonFile(
      PortalFlowFileControl portalFlowFileControl,
      @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String tokenHeader);

  @PutMapping("/api/v1/file-control-management/portal/json-file")
  PortalFlowFileControl updatePortalJsonFile(
      PortalFlowFileControl portalFlowFileControl,
      @RequestParam("step") String step,
      @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String tokenHeader);

  @PatchMapping("/api/v1/file-control-management/update-server-name-json-file/{uuid}")
  FlowFileControl updateServerNameFromFileManager(
      FileManagerResponse update, @PathVariable(value = "uuid") String uuid);

  @PutMapping("/api/v1/file-control-management/update-json-after-identification-step/{uuid}")
  void updateJsonAfterIdentificationStep(
      @RequestBody ProcessCtrlIdentificationStep request,
      @PathVariable(value = "uuid") String uuid,
      @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String tokenHeader);

  @PutMapping("/api/v1/file-control-management/update-portal-json-after-identification-step/{uuid}")
  void updatePortalJsonAfterIdentificationStep(
      @RequestBody ProcessCtrlIdentificationStep request,
      @PathVariable(value = "uuid") String uuid,
      @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String tokenHeader);

  @PutMapping("/api/v1/file-control-management/update-json-after-pre-treatment-step/{uuid}")
  void updateJsonAfterFinishedPreTreatment(
      @RequestBody ProcessCtrlPreProcessing request,
      @PathVariable(value = "uuid") String uuid,
      @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String tokenHeader);

  @PutMapping("/api/v1/file-control-management/update-portal-json-after-pre-treatment-step/{uuid}")
  void updateJsonAfterFinishedPreTreatmentPortal(
      @RequestBody PortalFlowFileTracing request,
      @PathVariable(value = "uuid") String uuid,
      @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String tokenHeader);

  @PutMapping("/api/v1/file-control-management/campaign-sms/json-file/pre-treatment/{uuid}")
  void updateJsonAfterFinishedPreTreatmentCampaignSMS(
      @RequestBody PortalFlowFileTracing request,
      @PathVariable(value = "uuid") String uuid,
      @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String tokenHeader);

  @PutMapping("/api/v1/file-control-management/update-portal-json-after-treatment-step/{uuid}")
  void updateJsonAfterFinishedTreatmentPortal(
      @RequestBody PortalFlowFileTracing request,
      @PathVariable(value = "uuid") String uuid,
      @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String tokenHeader);

  @PutMapping("/api/v1/file-control-management/update-json-after-composition-step/{uuid}")
  void updateJsonAfterFinishedComposition(
      @RequestBody Processing request,
      @PathVariable(value = "uuid") String uuid,
      @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String tokenHeader);

  @GetMapping("/api/v1/file-control-management/get-json-file/{uuid}")
  com.tessi.cxm.pfl.shared.filectrl.model.FlowFileControl getJsonFileControl(
      @PathVariable("uuid") String uuid,
      @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String tokenHeader);

  @GetMapping("/api/v1/file-control-management/portal/json-file/{uuid}")
  com.tessi.cxm.pfl.shared.filectrl.model.portal.PortalFlowFileControl getPortalJsonFileControl(
      @PathVariable("uuid") String uuid,
      @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String tokenHeader);
}
