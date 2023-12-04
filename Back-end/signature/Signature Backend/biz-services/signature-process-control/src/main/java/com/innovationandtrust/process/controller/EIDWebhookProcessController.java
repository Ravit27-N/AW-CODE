package com.innovationandtrust.process.controller;

import com.innovationandtrust.process.service.EIDWebhookProcessingService;
import com.innovationandtrust.utils.eid.model.VideoVerifiedDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/webhook")
public class EIDWebhookProcessController {

  private final EIDWebhookProcessingService eIDWebhookProcessingService;

  public EIDWebhookProcessController(EIDWebhookProcessingService eIDWebhookProcessingService) {
    this.eIDWebhookProcessingService = eIDWebhookProcessingService;
  }

  @PostMapping("/video/verified/callback")
  public void videoVerificationCallback(@RequestBody VideoVerifiedDto videoVerifiedDto) {
    this.eIDWebhookProcessingService.videoVerificationCallback(videoVerifiedDto);
  }
}
