package com.tessi.cxm.pfl.ms3.service.restclient;

import com.tessi.cxm.pfl.shared.model.SharedStatusInfoDto;
import com.tessi.cxm.pfl.shared.service.restclient.FeignClientConstants;
import com.tessi.cxm.pfl.shared.service.restclient.HubDigitalFlow;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = FeignClientConstants.CXM_HUB_DIGITALFLOW)
public interface HubDigitalFlowFeignClient extends HubDigitalFlow {

  @GetMapping("/api/v1/status-info/{jobUuid}")
  SharedStatusInfoDto getStatusInfo(
      @PathVariable("jobUuid") String jobUuid,
      @RequestParam(value = "locale", defaultValue = "fr") String locale,
      @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization);
}
