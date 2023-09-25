package com.tessi.cxm.pfl.ms5.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum DataReaderValidationType {
  ADMIN("admin"),
  CLIENT_ADMIN("client-admin");

  private final String value;
}
