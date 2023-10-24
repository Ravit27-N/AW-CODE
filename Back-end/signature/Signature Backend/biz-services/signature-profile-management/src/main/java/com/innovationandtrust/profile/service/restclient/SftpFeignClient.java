package com.innovationandtrust.profile.service.restclient;

import com.innovationandtrust.utils.feignclient.FeignClientFacadeConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    value = "sftp",
    url = "${signature.feign-client.clients.sftp-url}",
    path = "${signature.feign-client.contexts.sftp-context-path}",
    configuration = FeignClientFacadeConfiguration.class)
public interface SftpFeignClient {
  @PostMapping("v1/folders")
  void createCorporateFolder(@RequestParam String corporateUuid);
}
