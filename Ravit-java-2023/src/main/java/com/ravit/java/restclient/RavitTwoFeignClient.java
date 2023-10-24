package com.ravit.java.restclient;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;


@FeignClient(
    value = "ravit-two-api",
    url = "http://10.2.50.11:9091")
//    path = "/ravit-two"
public interface RavitTwoFeignClient {

  @GetMapping("/")
  String getRavit(
      @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization);

}
