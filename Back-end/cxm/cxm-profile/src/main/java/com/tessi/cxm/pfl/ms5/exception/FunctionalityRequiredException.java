package com.tessi.cxm.pfl.ms5.exception;

public class FunctionalityRequiredException extends RuntimeException {

  public FunctionalityRequiredException() {
    super("Functionalities are required and cannot null or empty");
  }
}
