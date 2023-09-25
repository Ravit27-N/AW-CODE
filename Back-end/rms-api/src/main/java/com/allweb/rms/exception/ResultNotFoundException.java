package com.allweb.rms.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ResultNotFoundException extends RuntimeException {
  private static final long serialVersionUID = -65577549232301820L;

  public ResultNotFoundException(int id) {
    super("Could not find Result " + id);
  }
}
