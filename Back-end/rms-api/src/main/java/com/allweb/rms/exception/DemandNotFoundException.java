package com.allweb.rms.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class DemandNotFoundException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public DemandNotFoundException(String msg) {
    super(msg);
  }
}
