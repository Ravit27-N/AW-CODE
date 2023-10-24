package com.innovationandtrust.process.exception;

/** Missing argument exception. */
public class MissingCompanyInfoException extends RuntimeException {
  public MissingCompanyInfoException(String message) {
    super("Company was null in project flowId: " + message);
  }
}
