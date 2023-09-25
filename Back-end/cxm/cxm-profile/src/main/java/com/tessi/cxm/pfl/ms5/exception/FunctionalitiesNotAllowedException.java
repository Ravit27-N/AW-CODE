package com.tessi.cxm.pfl.ms5.exception;

public class FunctionalitiesNotAllowedException extends RuntimeException {

  public FunctionalitiesNotAllowedException(String funcIgnored) {
    super(funcIgnored + " functionalities not allowed for your client!");
  }
}
