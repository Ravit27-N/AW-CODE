package com.allweb.rms.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class EmailConflictException extends RuntimeException {

  /** */
  private static final long serialVersionUID = -61577549232301820L;

  public EmailConflictException(String message) {
    super(message);
  }
}
