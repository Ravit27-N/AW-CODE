package com.allweb.rms.exception;

public class CandidateIDConflictException extends RuntimeException {

  private static final long serialVersionUID = -61577549232301850L;

  public CandidateIDConflictException() {
    super("This candidate id is already exists!");
  }
}
