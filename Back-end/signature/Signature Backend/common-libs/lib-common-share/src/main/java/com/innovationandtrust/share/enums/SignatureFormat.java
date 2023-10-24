package com.innovationandtrust.share.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

public enum SignatureFormat {
  PA_DES(1, "PAdES"),
  XA_DES(2, "XAdES"),
  CA_DES(3, "CAdES");

  private static final Map<Integer, SignatureFormat> BY_VALUE = new HashMap<>();

  static {
    Arrays.stream(values()).forEach(v -> BY_VALUE.put(v.numVal, v));
  }

  @JsonValue
  @Getter private final Integer numVal;
  @Getter private final String stringVal;

  SignatureFormat(int value, String stringVal) {
    this.numVal = value;
    this.stringVal = stringVal;
  }

  public static SignatureFormat getByValue(Integer numVal) {
    return BY_VALUE.get(numVal);
  }
}
