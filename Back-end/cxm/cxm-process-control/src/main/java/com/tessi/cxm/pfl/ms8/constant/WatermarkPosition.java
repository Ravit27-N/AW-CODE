package com.tessi.cxm.pfl.ms8.constant;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WatermarkPosition {
  ALL(1, "ALL_PAGES", "All", "For all pages."),
  ONLY_DOC(2, "ONLY_DOC", "OnlyDoc", "For all pages excluding attachments and address labels."),
  OTHER(3, "OTHER", "Other", "For only attachments + address labels.");
  private final int order;
  private final String key;
  private final String value;
  private final String description;

  /**
   * If a key is present, returns {@code true}, otherwise {@code false}.
   *
   * @return {@code true} if a value is present, otherwise {@code false}
   */
  public static boolean isPresent(String key) {
    return Arrays.stream(values())
        .anyMatch(watermarkPosition -> watermarkPosition.key.equalsIgnoreCase(key));
  }

  public static String getValueByKey(String key) {
    return Arrays.stream(values())
        .filter(watermarkPosition -> watermarkPosition.key.equalsIgnoreCase(key))
        .map(WatermarkPosition::getValue)
        .findFirst()
        .orElseThrow(null);
  }
}
