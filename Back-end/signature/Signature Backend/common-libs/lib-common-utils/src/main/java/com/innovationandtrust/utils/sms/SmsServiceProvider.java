package com.innovationandtrust.utils.sms;

import com.innovationandtrust.utils.sms.model.MessageRequest;
import com.innovationandtrust.utils.sms.restclient.SmsFeignClient;
import java.util.Map;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SmsServiceProvider {

  private static final String MESSAGE_KEY = "messages";
  private final SmsFeignClient smsFeignClient;
  private final SMSProperty smsProperty;

  /**
   * Handling the process of sending sms.
   *
   * @param recipient refers to the mobile number of the receiver
   * @param message refers the text to be sent
   */
  public void sendSMS(String recipient, String message) {
    recipient = recipient.replace("+", "00");
    var msg =
        new MessageRequest(
            smsProperty.getProductToken(), smsProperty.getSender(), recipient, message);
    this.smsFeignClient.sendSMS(Map.of(MESSAGE_KEY, msg));
  }
}
