package com.innovationandtrust.process.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EIDSmsTemplate {

  public static final String MESSAGE = "Electronic signature Passcode *|challengeCode|* . Valid within *|ttl|*.";
  public static final String MESSAGE_FROM = "CERTIGNA";
}
