package com.allweb.rms.exception;

public class NbRequiredSmallerThanNbCandidateException extends RuntimeException {

  private static final long serialVersionUID = -61577549232301850L;

  public NbRequiredSmallerThanNbCandidateException() {
    super("NbRequired Smaller Than NbCandidate");
  }
}
