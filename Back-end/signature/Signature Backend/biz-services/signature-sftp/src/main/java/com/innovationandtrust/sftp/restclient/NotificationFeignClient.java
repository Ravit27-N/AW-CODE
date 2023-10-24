package com.innovationandtrust.sftp.restclient;

import com.innovationandtrust.utils.feignclient.InternalFeignClientConfiguration;
import com.innovationandtrust.utils.mail.model.MailRequest;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "signature-notification",
    url = "${signature.feign-client.clients.notification-url}",
    path = "${signature.feign-client.contexts.notification-context-path}",
    configuration = InternalFeignClientConfiguration.class)
public interface NotificationFeignClient {
  @PostMapping("/v1/mails/multiple")
  void sendMultiple(
      @RequestBody List<MailRequest> mailRequests,
      @RequestParam(value = "logo", required = false) String logo,
      @RequestHeader(HttpHeaders.AUTHORIZATION) String token);
}
