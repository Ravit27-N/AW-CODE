package com.allweb.rms.exception;

public class CandidateIdLessThanOException extends RuntimeException {

  private static final long serialVersionUID = -61577549232301850L;

  public CandidateIdLessThanOException() {
    super("Candidate ID < 0");
  }
}
