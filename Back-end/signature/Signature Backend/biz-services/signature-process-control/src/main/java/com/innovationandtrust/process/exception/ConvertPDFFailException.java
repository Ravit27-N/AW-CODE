package com.innovationandtrust.process.exception;

public class ConvertPDFFailException extends RuntimeException {
  public ConvertPDFFailException(String message) {
    super(message, null);
  }

  public ConvertPDFFailException(String message, Throwable throwable) {
    super(message, throwable);
  }
}
