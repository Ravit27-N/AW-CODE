package com.innovationandtrust.utils.notification.feignclient;

import com.innovationandtrust.utils.feignclient.FeignClientFacadeConfiguration;
import com.innovationandtrust.utils.mail.model.MailRequest;
import com.innovationandtrust.utils.notification.feignclient.model.SmsRequest;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "signature-notification",
    url = "${signature.feign-client.clients.notification-url}",
    path = "${signature.feign-client.contexts.notification-context-path}",
    configuration = FeignClientFacadeConfiguration.class)
public interface NotificationFeignClient {
  @PostMapping("/v1/mails")
  void sendMail(
      @RequestBody MailRequest mailRequest,
      @RequestParam(value = "logo", required = false) String logo);

  @PostMapping("/v1/mails/multiple")
  void sendMultiple(
      @RequestBody List<MailRequest> mailRequests,
      @RequestParam(value = "logo", required = false) String logo);

  @PostMapping("/v1/sms")
  void sendSms(
      @RequestParam("recipient") String recipient, @RequestParam("message") String message);

  @PostMapping("/v1/sms/multiple")
  void sendSmsMultiple(@RequestBody List<SmsRequest> smsRequests);
}
