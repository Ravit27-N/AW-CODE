package com.innovationandtrust.share.enums;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum SignatureType {
  ENVELOPED(1),
  ENVELOPING(2),
  DETACHED(3);

  private static final Map<Integer, SignatureType> BY_VALUE = new HashMap<>();

  static {
    Arrays.stream(values()).forEach(v -> BY_VALUE.put(v.numVal, v));
  }

  private final int numVal;

  SignatureType(int value) {
    this.numVal = value;
  }

  public int getVal() {
    return numVal;
  }

  public SignatureType getByValue(int numVal) {
    return BY_VALUE.get(numVal);
  }
}
