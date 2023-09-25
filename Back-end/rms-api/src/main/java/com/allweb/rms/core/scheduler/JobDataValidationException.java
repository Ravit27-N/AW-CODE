package com.allweb.rms.core.scheduler;

public class JobDataValidationException extends RuntimeException {
  private static final long serialVersionUID = 4612054971937484402L;

  public JobDataValidationException() {}

  public JobDataValidationException(String message) {
    super(message);
  }
}
