package com.innovationandtrust.share.enums;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;

/** Signature setting level. */
@Getter
public enum SignatureSettingLevel {
  SIMPLE(1, "SIMPLE"),
  ADVANCE(2, "ADVANCE"),
  QUALIFY(3, "QUALIFY");

  private final Integer order;
  @Getter private final String value;

  SignatureSettingLevel(int order, String value) {
    this.order = order;
    this.value = value;
  }

  public static Set<String> getLevels() {
    return Stream.of(SIMPLE.value, ADVANCE.value, QUALIFY.value).collect(Collectors.toSet());
  }
}
