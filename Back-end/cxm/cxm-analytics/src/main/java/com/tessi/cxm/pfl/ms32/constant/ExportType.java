package com.tessi.cxm.pfl.ms32.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ExportType {
  GLOBAL("global"),
  SPECIFIC("specific");

  @Getter private final String value;

  public boolean equalsIgnoreCase(String value) {
    return getValue().equalsIgnoreCase(value);
  }
}
