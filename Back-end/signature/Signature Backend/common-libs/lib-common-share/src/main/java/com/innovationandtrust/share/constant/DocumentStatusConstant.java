package com.innovationandtrust.share.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DocumentStatusConstant {

  //En cours
  public static final Integer IN_PROGRESS = 1;

  public static final Integer SIGNED = 2;

  public static final Integer APPROVED = 3;
  public static final Integer RECEIVED = 4;

  public static final Integer REFUSED = 5;

  public static final Integer READ = 6;
}
