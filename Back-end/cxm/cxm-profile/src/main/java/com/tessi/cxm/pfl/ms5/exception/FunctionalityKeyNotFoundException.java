package com.tessi.cxm.pfl.ms5.exception;

public class FunctionalityKeyNotFoundException extends RuntimeException {
  public FunctionalityKeyNotFoundException(String functionalityKey) {
    super("Functionality key is not found: " + functionalityKey);
  }
}
