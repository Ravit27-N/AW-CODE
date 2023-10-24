package com.innovationandtrust.share.constant;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NotificationConstant {

  //Notification reminder option
  public static final String NOTIFY_ONCE_PER_DAY = "ONCE_PER_DAY";

  public static final String NOTIFY_EVERY_2_DAYS = "EVERY_2_DAYS";

  public static final String NOTIFY_ONCE_PER_WEEK = "ONCE_PER_WEEK";

  public static final String NOTIFY_EVERY_2_WEEK = "EVERY_2_WEEK";

  //Notification channel
  public static final String SMS = "sms";

  public static final String EMAIL = "email";
  public static final String SMS_EMAIL = "sms_email";

  public static Set<String> getTypes() {
    return Stream.of(SMS, EMAIL, SMS_EMAIL).collect(Collectors.toSet());
  }
}
