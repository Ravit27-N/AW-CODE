package com.tessi.cxm.pfl.ms3.service.restclient;

import com.tessi.cxm.pfl.shared.service.restclient.FeignClientConstants;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * To handle the process of requesting service gateway to retrieve the available service registered
 * in service discovery.
 *
 * @author Chamrong THOR
 */
@FeignClient(FeignClientConstants.SERVICE_GATEWAY)
public interface ServiceGatewayFeignClient {

  @GetMapping("/microservice-info")
  List<Map<String, Object>> getMicroserviceInfo(
      @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String tokenHeader);

  /**
   * To verify the service with the provided name is available on service-discovery or not.
   *
   * @param serviceName refers to the name of service to check
   * @param tokenHeader refers to the valid token header
   * @return true if it is available nor false
   */
  default boolean isServiceAvailable(String serviceName, String tokenHeader) {
    return this.getMicroserviceInfo(tokenHeader).stream()
        .anyMatch(obj -> Objects.equals(String.valueOf(obj.get("serviceName")).toLowerCase(),
            serviceName.toLowerCase()));
  }
}
