package com.tessi.cxm.pfl.ms5.service.restclient;

import com.tessi.cxm.pfl.shared.model.SharedClientUnloadDetailsDTO;
import com.tessi.cxm.pfl.shared.service.restclient.FeignClientConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(FeignClientConstants.CXM_PROCESS_CONTROL)
public interface ProcessControlFeignClient {

  @PostMapping("/api/v1/process-control/set-client-unload-schedule")
  void produceClientUnloads(
      @RequestBody SharedClientUnloadDetailsDTO clientUnloads,
      @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String tokenHeader);

  @DeleteMapping("/api/v1/process-control/flow-unloading/{clientId}")
  void deleteClientUnloads(@PathVariable(value = "clientId") long clientId,
      @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String tokenHeader);
}
