package com.innovationandtrust.utils.tinyurl;

import com.innovationandtrust.utils.feignclient.TinyUrlFeignClientConfiguration;
import com.innovationandtrust.utils.tinyurl.model.TinyUrlCriterion;
import com.innovationandtrust.utils.tinyurl.model.TinyUrlResponse;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    name = "tinyurl-service",
    configuration = TinyUrlFeignClientConfiguration.class,
    url = "${signature.tiny-url.url}")
public interface TinyUrlFeignClient {
  @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
  @Headers({"Content-Type", "application/json"})
  TinyUrlResponse shortenUrl(@RequestBody TinyUrlCriterion tinyUrlCriterion);
}
