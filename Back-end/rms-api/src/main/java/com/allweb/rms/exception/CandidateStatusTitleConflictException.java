package com.allweb.rms.exception;

public class CandidateStatusTitleConflictException extends RuntimeException {

  private static final long serialVersionUID = -61577549232301850L;

  public CandidateStatusTitleConflictException() {
    super("This title is already exists!");
  }
}
