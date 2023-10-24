package com.innovationandtrust.utils.sms.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SmsRequest implements Serializable {

  @JsonProperty(value = "from")
  private final String sender;

  @JsonProperty(value = "to")
  private final List<Recipient> recipients;

  @JsonProperty(value = "body")
  private final Message body;

  private int minimumNumberOfMessageParts;

  private int maximumNumberOfMessageParts;

  @JsonProperty(value = "alowedChannels")
  private String[] allowedChannels;

  public SmsRequest(String sender, String recipient, String message) {
    this.sender = sender;
    this.recipients = List.of(Recipient.builder().number(recipient).build());
    this.body = Message.builder().content(message).type("auto").build();
    this.minimumNumberOfMessageParts = 1;
    this.maximumNumberOfMessageParts = 8;
    this.allowedChannels = new String[] {"SMS"};
  }

  @Getter
  @Setter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  static class Recipient implements Serializable {
    private String number;
  }

  @Getter
  @Setter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  static class Message implements Serializable {
    private String content;
    private String type;
  }
}
