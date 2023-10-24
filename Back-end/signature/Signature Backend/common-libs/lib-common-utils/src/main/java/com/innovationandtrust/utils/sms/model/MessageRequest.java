package com.innovationandtrust.utils.sms.model;

import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MessageRequest implements Serializable {
  private Authentication authentication;
  private List<SmsRequest> msg;

  public MessageRequest(String productToken, String sender, String recipient, String message) {
    this(new Authentication(productToken), List.of(new SmsRequest(sender, recipient, message)));
  }
}
