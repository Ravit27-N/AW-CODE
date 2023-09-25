package com.tessi.cxm.pfl.ms32.constant;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public enum DateType {
  TODAY("0", "TODAY"),
  YESTERDAY("1", "YESTERDAY"),
  LAST_7_DAYS("2", "LAST_7_DAYS"),
  LAST_30_DAYS("3", "LAST_30_DAYS"),
  THIS_MONTH("4", "THIS_MONTH"),
  LAST_MONTH("5", "LAST_MONTH"),
  LAST_3_MONTHS("6", "LAST_3_MONTHS"),
  CUSTOM_RANGES("7", "CUSTOM_RANGES");
  private final String key;
  private final String value;

  private static final Map<String, DateType> BY_VALUE = new HashMap<>();
  private static final Map<String, DateType> BY_KEY = new HashMap<>();

  DateType(String key, String value) {
    this.key = key;
    this.value = value;
  }

  static {
    for (var dateType : values()) {
      BY_VALUE.put(dateType.value.toLowerCase(), dateType);
      BY_KEY.put(dateType.key.toLowerCase(), dateType);
    }
  }

  public String getKey() {
    return key;
  }

  public String getValue() {
    return value;
  }

  public static DateType valueOfKey(String key) {
    return BY_KEY.get(key.toLowerCase(Locale.ROOT));
  }

  public static DateType valueOfLabel(String label) {
    return BY_VALUE.get(label.toLowerCase(Locale.ROOT));
  }
}
