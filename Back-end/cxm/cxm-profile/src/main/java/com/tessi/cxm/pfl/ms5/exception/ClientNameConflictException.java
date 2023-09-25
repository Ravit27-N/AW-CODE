package com.tessi.cxm.pfl.ms5.exception;

public class ClientNameConflictException extends RuntimeException {

  public ClientNameConflictException(String clientName) {
    super("Client's name is already exist: " + clientName + ".");
  }
}
