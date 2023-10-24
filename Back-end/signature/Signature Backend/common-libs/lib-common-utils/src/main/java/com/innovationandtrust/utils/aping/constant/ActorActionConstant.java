package com.innovationandtrust.utils.aping.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ActorActionConstant {
  public static final String STATUS_KEY = "actorStatus";
  public static final int DEFAULT = 0;
  public static final int IN_PROGRESS = 1;
  public static final int DONE = 2;
}
