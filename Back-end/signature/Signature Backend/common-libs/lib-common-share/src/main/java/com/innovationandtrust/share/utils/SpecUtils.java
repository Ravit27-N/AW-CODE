package com.innovationandtrust.share.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SpecUtils {

  public static String likeQuery(String search) {
    return String.format("%s%s%s", "%", search.trim().toLowerCase().replaceAll("\\s", ""), "%");
  }
}
