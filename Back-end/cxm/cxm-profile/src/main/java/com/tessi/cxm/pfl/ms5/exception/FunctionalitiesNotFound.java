package com.tessi.cxm.pfl.ms5.exception;

public class FunctionalitiesNotFound extends RuntimeException {
  public FunctionalitiesNotFound(String message) {
    super(message);
  }

  public FunctionalitiesNotFound(long id) {
    super(String.format("Functionalities id not found %s", id));
  }
}
