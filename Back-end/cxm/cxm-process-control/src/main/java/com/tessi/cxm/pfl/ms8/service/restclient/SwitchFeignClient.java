package com.tessi.cxm.pfl.ms8.service.restclient;

import com.cxm.tessi.pfl.shared.flowtreatment.model.request.BatchSwitchingRequestDto;
import com.cxm.tessi.pfl.shared.flowtreatment.model.request.PortalSwitchRequestDto;
import com.cxm.tessi.pfl.shared.flowtreatment.model.response.FlowProcessingResponse;
import com.tessi.cxm.pfl.shared.service.restclient.FeignClientConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(FeignClientConstants.CXM_SWITCH)
public interface SwitchFeignClient {

  @PostMapping("/api/v1/switch/portal/doc")
  FlowProcessingResponse<Object> switchPortalProcessing(
      @RequestBody PortalSwitchRequestDto requestDto,
      @RequestParam(value = FeignClientConstants.FUNCTIONALITY_KEY, required = false) String functionalityKey,
      @RequestParam(value = FeignClientConstants.PRIVILEGE_KEY, required = false) String privilegeKey,
      @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader);

  @PostMapping("/api/v1/switch/portal/campaign")
  FlowProcessingResponse<Object> portalSwitchingCampaign(
      @RequestBody PortalSwitchRequestDto requestDto,
      @RequestParam(value = FeignClientConstants.FUNCTIONALITY_KEY, required = false) String functionalityKey,
      @RequestParam(value = FeignClientConstants.PRIVILEGE_KEY, required = false) String privilegeKey,
      @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader);

  @PostMapping("/api/v1/switch/batch")
  FlowProcessingResponse<Object> switchBatchProcessing(
      @RequestBody BatchSwitchingRequestDto requestDto,
      @RequestParam(value = FeignClientConstants.FUNCTIONALITY_KEY, required = false) String functionalityKey,
      @RequestParam(value = FeignClientConstants.PRIVILEGE_KEY, required = false) String privilegeKey,
      @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader);
}
