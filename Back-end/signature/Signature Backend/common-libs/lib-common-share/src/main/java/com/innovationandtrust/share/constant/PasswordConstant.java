package com.innovationandtrust.share.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PasswordConstant {
  public static final int MIN_LENGTH = 12;
  public static final int MAX_LENGTH = 12;
  public static final String REG =
      "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*_\\-+=<>,.{}?|])[a-zA-Z0-9!@#$%^&*_\\-+=<>,.{}?|]{12,}$";
  public static final String WRONG_REG =
      "Password require number, alphabet and some special characters:" + REG;
  public static final String WRONG_LENGTH = "Password must be only 12 characters.";
}
