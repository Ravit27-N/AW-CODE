package com.tessi.cxm.pfl.ms11.constant;

import com.tessi.cxm.pfl.shared.exception.BadRequestException;
import java.util.Arrays;
import java.util.stream.Stream;

public enum Language {

  EN("en"),
  FR("fr");

  private final String value;

  Language(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public static String getValue(String name) {
    return valueOf(name).getValue();
  }

  public static String contain(String name) {
    return Arrays.stream(values())
        .filter(v -> v.getValue().equalsIgnoreCase(name))
        .findFirst()
        .map(Language::getValue)
        .orElseThrow(() -> {
          throw new BadRequestException("Language not supported");
        });
  }

  public static Language find(String value) {
    return Stream.of(Language.values())
        .filter(resourceType -> value.equals(resourceType.value))
        .findFirst()
        .orElseThrow(IllegalArgumentException::new);
  }

}
