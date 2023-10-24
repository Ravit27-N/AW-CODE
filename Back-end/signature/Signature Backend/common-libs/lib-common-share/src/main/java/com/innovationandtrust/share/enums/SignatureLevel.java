package com.innovationandtrust.share.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum SignatureLevel {
  B(1),
  T(2),
  LT(3),
  LTA(4);

  private static final Map<Integer, SignatureLevel> BY_VALUE = new HashMap<>();

  static {
    Arrays.stream(values()).forEach(v -> BY_VALUE.put(v.numVal, v));
  }

  private final int numVal;

  SignatureLevel(int value) {
    this.numVal = value;
  }
  @JsonValue
  public int getValue() {
    return numVal;
  }

  public static SignatureLevel getByValue(int value) {
    return BY_VALUE.get(value);
  }
}
