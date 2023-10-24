package com.innovationandtrust.sftp.restclient;

import com.innovationandtrust.share.model.profile.CorporateUser;
import com.innovationandtrust.share.model.profile.Template;
import com.innovationandtrust.utils.feignclient.InternalFeignClientConfiguration;
import java.util.Optional;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    value = "signature-profile",
    url = "${signature.feign-client.clients.profile-url}",
    path = "${signature.feign-client.contexts.profile-context-path}",
    configuration = InternalFeignClientConfiguration.class)
public interface ProfileFeignClient {
  @GetMapping("/v1/corporate/users/uuid/{id}")
  CorporateUser findCorporateByUuid(
      @PathVariable String id,
      @RequestParam(name = "userId", defaultValue = "") String userId,
      @RequestHeader(HttpHeaders.AUTHORIZATION) String token);

  @GetMapping("/v1/templates/get/{id}")
  Optional<Template> findTemplateById(
      @PathVariable Long id, @RequestHeader(HttpHeaders.AUTHORIZATION) String token);
}
