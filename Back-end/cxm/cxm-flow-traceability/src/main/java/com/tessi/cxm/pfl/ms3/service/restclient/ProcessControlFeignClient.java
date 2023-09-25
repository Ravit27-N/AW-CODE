package com.tessi.cxm.pfl.ms3.service.restclient;

import com.cxm.tessi.pfl.shared.flowtreatment.model.request.ValidationFlowDocumentRequest;
import com.cxm.tessi.pfl.shared.flowtreatment.model.request.ValidationFlowRequest;
import com.tessi.cxm.pfl.shared.auth.AuthenticationUtils;
import com.tessi.cxm.pfl.shared.model.SharedUnloadDate;
import com.tessi.cxm.pfl.shared.service.restclient.FeignClientConstants;
import javax.validation.Valid;

import com.tessi.cxm.pfl.shared.utils.BearerAuthentication;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Date;

@FeignClient(FeignClientConstants.CXM_PROCESS_CONTROL)
public interface ProcessControlFeignClient {

  @PostMapping("/api/v1/process-control/validation-flow")
  String validateFlow(
      @RequestBody ValidationFlowRequest request,
      @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String tokenHeader);

  @PostMapping("/api/v1/process-control/validate-flow-document")
  String validateFlowDocument(@RequestBody @Valid ValidationFlowDocumentRequest request,
      @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String tokenHeader);

  @GetMapping("/api/v1/process-control/get-nearest-client-unloading")
  SharedUnloadDate getNearestClientUnloadingDate(
      @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String tokenHeader);

  default Date getNearestClientUnloadingDate() {
    return getNearestClientUnloadingDate(
            BearerAuthentication.PREFIX_TOKEN.concat(AuthenticationUtils.getAuthToken()))
        .getUnloadDate();
  }
}
