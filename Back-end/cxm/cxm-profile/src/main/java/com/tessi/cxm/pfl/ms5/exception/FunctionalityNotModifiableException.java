package com.tessi.cxm.pfl.ms5.exception;

public class FunctionalityNotModifiableException extends RuntimeException {

  public FunctionalityNotModifiableException() {
    super("Functionality doesn't allow to modify for your role!");
  }
}
