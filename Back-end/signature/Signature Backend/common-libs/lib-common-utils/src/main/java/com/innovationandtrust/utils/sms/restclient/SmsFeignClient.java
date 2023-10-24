package com.innovationandtrust.utils.sms.restclient;

import com.innovationandtrust.utils.feignclient.FeignClientConfiguration;
import com.innovationandtrust.utils.sms.model.MessageRequest;
import feign.Headers;
import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    value = "sms-service",
    configuration = FeignClientConfiguration.class,
    url = "${signature.sms-service.url}")
public interface SmsFeignClient {

  @PostMapping(value = "/message", consumes = "application/json")
  @Headers("Content-Type: application/x-www-form-urlencoded")
  Object sendSMS(@RequestBody Map<String, MessageRequest> message);
}
