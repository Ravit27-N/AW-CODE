package com.allweb.rms.exception;

public class EmailNotFoundException extends RuntimeException {
  private static final long serialVersionUID = -81577545342301820L;

  public EmailNotFoundException() {
    super(
        "Could not found your email address in the token. Please make sure your registration must have an email!");
  }
}
