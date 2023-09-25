package com.allweb.rms.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UniversityNameConflictException extends RuntimeException {

  private static final long serialVersionUID = -65577549232301820L;

  public UniversityNameConflictException(String message) {
    super(message);
  }
}
