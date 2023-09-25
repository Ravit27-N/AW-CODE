package com.allweb.rms.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ActivityNotFoundException extends RuntimeException {
  /** */
  private static final long serialVersionUID = -61577549232301820L;

  public ActivityNotFoundException(String message) {
    super(message);
  }
}
