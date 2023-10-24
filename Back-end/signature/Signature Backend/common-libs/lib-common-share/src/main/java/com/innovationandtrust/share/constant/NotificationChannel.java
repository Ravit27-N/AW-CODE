package com.innovationandtrust.share.constant;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.Getter;

@Getter
public enum NotificationChannel {
  SMS(1, NotificationConstant.SMS),
  EMAIL(2, NotificationConstant.EMAIL),
  SMS_AND_EMAIL(3, NotificationConstant.SMS_EMAIL);

  private static final Map<Integer, NotificationChannel> BY_CHANNEL = new HashMap<>();

  static {
    Arrays.stream(values()).forEach(v -> BY_CHANNEL.put(v.channel, v));
  }

  private final Integer channel;

  @Getter private final String name;

  NotificationChannel(Integer channel, String name) {
    this.channel = channel;
    this.name = name;
  }

  public static NotificationChannel getByChannel(Integer channel) {
    return Optional.of(BY_CHANNEL.get(channel))
        .orElseThrow(() -> new IllegalArgumentException("Invalid channel"));
  }
}
