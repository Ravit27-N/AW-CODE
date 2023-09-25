package com.tessi.cxm.pfl.ms3.exception;

public class StatusNotFoundException extends RuntimeException {
  public StatusNotFoundException(String message) {
    super(message);
  }

  public StatusNotFoundException() {
    super("Status not found.");
  }
}
