package com.tessi.cxm.pfl.ms8.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WorkflowValidation {
  VALIDATE("Validate", 1),
  NO("NO", 0);

  private final String key;
  private final int value;
}
