package com.tessi.cxm.pfl.ms5.exception;

public class DivisionNameConflictException extends RuntimeException {

  public DivisionNameConflictException() {
    super("Division's name is already exist!");
  }

  public DivisionNameConflictException(String name) {
    super("Division's name is already exist: " + name + ".");
  }
}
