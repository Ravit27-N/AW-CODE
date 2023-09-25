package com.tessi.cxm.pfl.ms15.service.restclient;

import com.tessi.cxm.pfl.ms15.model.DocumentInstructions;
import com.cxm.tessi.pfl.shared.flowtreatment.model.response.FlowProcessingResponse;
import com.cxm.tessi.pfl.shared.flowtreatment.model.response.SettingResponse;
import com.tessi.cxm.pfl.shared.service.restclient.FeignClientConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(FeignClientConstants.CXM_SETTING)
public interface SettingFeignClient {

  @GetMapping("/api/v1/setting/pre-processing/{idCreator}")
  DocumentInstructions getDocumentInstructions(
      @RequestParam("flowType") String flowType,
      @RequestParam(value = "modelName", defaultValue = "") String modelName,
      @PathVariable("idCreator") Long idCreator,
      @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String tokenHeader);

  @GetMapping("/api/v1/setting/identification")
  FlowProcessingResponse<SettingResponse> extractSetting(
      @RequestParam(value = "flowName", defaultValue = "") String flowName,
      @RequestParam(value = "idCreator") Long idCreator,
      @RequestParam(value = "flowType") String flowType,
      @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false)
      String authorizationHeader);
}
