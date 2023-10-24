package com.innovationandtrust.process.exception;

public class InvalidOtpException extends RuntimeException {

  public InvalidOtpException() {
    super("Invalid OTP code!");
  }

  public InvalidOtpException(String message) {
    super(message);
  }
}
