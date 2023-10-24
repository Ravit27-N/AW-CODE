package com.innovationandtrust.process.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RegexPatternConstant {

  public static final String LAST_DIGIT = "\\d{%d}$";

  public static final String CRON_EXPRESSION = "%s %s %s %s %s %s %s";
}
