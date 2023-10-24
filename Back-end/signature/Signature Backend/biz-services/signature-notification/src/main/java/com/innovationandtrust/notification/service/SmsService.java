package com.innovationandtrust.notification.service;

import com.innovationandtrust.utils.notification.feignclient.model.SmsRequest;
import com.innovationandtrust.utils.sms.SmsServiceProvider;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
public class SmsService {
  private SmsServiceProvider smsServiceProvider;

  @Autowired(required = false)
  public void setSmsServiceProvider(final SmsServiceProvider smsServiceProvider) {
    this.smsServiceProvider = smsServiceProvider;
  }

  public void send(String participant, String message) {
    log.info("Sending sms to: {}", participant);
    this.validateAndSendSms(participant, message);
    log.info("Sms has been sent...");
  }

  /**
   * To send sms to participants.
   *
   * @param smsRequests list of participants to send sms
   */
  public void sendMultiple(List<SmsRequest> smsRequests) {
    log.info("Sending multiple sms...");
    smsRequests.forEach(
        smsRequest -> {
          log.info("Sending sms to: {}", smsRequest.getParticipant());
          this.validateAndSendSms(smsRequest.getParticipant(), smsRequest.getMessage());
        });
    log.info("Sms have been sent...");
  }

  private void validateAndSendSms(String participant, String message) {
    if (!(StringUtils.hasText(participant) && StringUtils.hasText(message))) {
      log.error("Error while sending sms. Recipient could not be empty");
    } else if (Objects.nonNull(this.smsServiceProvider)) {
      this.smsServiceProvider.sendSMS(participant, message);
    } else {
      log.warn("SMS service is disabled");
    }
  }
}
