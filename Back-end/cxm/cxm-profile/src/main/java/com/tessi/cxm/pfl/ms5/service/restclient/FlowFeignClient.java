package com.tessi.cxm.pfl.ms5.service.restclient;

import com.tessi.cxm.pfl.shared.service.restclient.FeignClientConstants;
import feign.FeignException;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(FeignClientConstants.CXM_FLOW_TRACEABILITY)
public interface FlowFeignClient {

  @DeleteMapping("/api/v1/flow-traceability/remove")
  void removeFlow(
      @RequestParam(value = "usernames") List<String> usernames,
      @RequestParam("serviceIds") List<Long> serviceIds,
      @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String tokenHeader);

  @Retryable(value = FeignException.class, maxAttempts = 5, backoff = @Backoff(delay = 30000))
  default void removeFlowHandler(List<String> usernames, List<Long> serviceIds,
      String tokenHeader) {
    FlowFeignClient.this.removeFlow(usernames, serviceIds, tokenHeader);
  }
}
