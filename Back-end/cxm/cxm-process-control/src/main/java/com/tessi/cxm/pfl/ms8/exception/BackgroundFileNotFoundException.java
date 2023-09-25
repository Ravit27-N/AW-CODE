package com.tessi.cxm.pfl.ms8.exception;

public class BackgroundFileNotFoundException extends RuntimeException {

  public BackgroundFileNotFoundException(String message) {
    super(message);
  }

  public BackgroundFileNotFoundException() {
    super("Background not found");
  }
}
