package com.allweb.rms.utils;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true)
public enum ReminderType {
  NORMAL("NORMAL"),
  SPECIAL("SPECIAL"),
  INTERVIEW("INTERVIEW");

  private final String value;

  ReminderType(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
