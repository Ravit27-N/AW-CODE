package com.allweb.rms.exception;

public class InterviewStatusInactiveException extends RuntimeException {
  /** */
  private static final long serialVersionUID = -61577549232301820L;

  public InterviewStatusInactiveException(int id) {
    super(String.format("Status id %s is inactive", id));
  }
}
