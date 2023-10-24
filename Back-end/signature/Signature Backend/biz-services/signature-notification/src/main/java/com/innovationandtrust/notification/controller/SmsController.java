package com.innovationandtrust.notification.controller;

import com.innovationandtrust.notification.service.SmsService;
import com.innovationandtrust.utils.notification.feignclient.model.SmsRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/sms")
@RequiredArgsConstructor
public class SmsController {
  private final SmsService smsService;

  @PostMapping
  @Tag(name = "Send sms", description = "To send sms")
  public void send(
      @RequestParam("recipient") String recipient, @RequestParam("message") String message) {
    this.smsService.send(recipient, message);
  }

  @PostMapping("/multiple")
  @Tag(name = "Send multiple sms", description = "To send multiple sms")
  public void sendMultiple(@RequestBody List<SmsRequest> smsRequests) {
    this.smsService.sendMultiple(smsRequests);
  }
}
