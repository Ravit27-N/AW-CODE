package com.allweb.rms.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class PushNotificationRequest {
  private String title;
  private String message;
  private String topic;
  private String token;

  public PushNotificationRequest(String title, String message, String topic) {
    this.title = title;
    this.message = message;
    this.topic = topic;
  }
}
