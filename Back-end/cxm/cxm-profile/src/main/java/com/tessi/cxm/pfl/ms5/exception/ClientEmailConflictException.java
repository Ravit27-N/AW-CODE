package com.tessi.cxm.pfl.ms5.exception;

public class ClientEmailConflictException extends RuntimeException {

  public ClientEmailConflictException() {
    super("Client's email is already exist!");
  }

  public ClientEmailConflictException(String message) {
    super(message);
  }
}
