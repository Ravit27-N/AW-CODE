package com.allweb.rms.exception;

public class InterviewNotFoundException extends RuntimeException {
  /** */
  private static final long serialVersionUID = -61577549232301820L;

  public InterviewNotFoundException(String message) {
    super(message);
  }

  public InterviewNotFoundException(int interviewId) {
    super("Could not find Interview: " + interviewId);
  }
}
