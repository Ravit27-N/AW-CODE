package com.innovationandtrust.share.enums;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.innovationandtrust.share.constant.NotificationConstant;
import lombok.Getter;

@Getter
public enum NotificationReminderOption {
  EVERY_FIVE_MINUTE(0, "For testing"),
  ONCE_A_DAY(1, NotificationConstant.NOTIFY_ONCE_PER_DAY),
  EVERY_2_DAYS(2, NotificationConstant.NOTIFY_ONCE_PER_DAY),

  ONCE_A_WEEK(3, NotificationConstant.NOTIFY_ONCE_PER_DAY),

  EVERY_2_WEEKS(4, NotificationConstant.NOTIFY_ONCE_PER_DAY);

  private static final Map<Integer, NotificationReminderOption> BY_OPTION = new HashMap<>();

  static {
    Arrays.stream(values()).forEach(v -> BY_OPTION.put(v.option, v));
  }

  private final int option;

  @Getter private final String name;

  NotificationReminderOption(int option, String name) {
    this.option = option;
    this.name = name;
  }

  public static NotificationReminderOption getByOption(Integer option) {
    return BY_OPTION.get(option);
  }
}
