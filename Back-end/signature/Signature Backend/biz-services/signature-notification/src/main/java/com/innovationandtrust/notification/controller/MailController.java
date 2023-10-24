package com.innovationandtrust.notification.controller;

import com.innovationandtrust.notification.service.MailService;
import com.innovationandtrust.utils.mail.model.MailRequest;
import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/mails")
@RequiredArgsConstructor
public class MailController {
  private final MailService mailService;

  @PostMapping
  @Tag(name = "Send mail request", description = "To send mail request")
  public void send(
      @RequestBody MailRequest mailRequest,
      @RequestParam(value = "logo", required = false) String logo) {
    this.mailService.send(mailRequest, logo);
  }

  @PostMapping("/multiple")
  @Tag(name = "Send multiple mails request", description = "To send multiple mails request")
  public void sendMultiple(
      @RequestBody List<MailRequest> mailRequests,
      @RequestParam(value = "logo", required = false) String logo) {
    this.mailService.sendMultiple(mailRequests, logo);
  }
}
