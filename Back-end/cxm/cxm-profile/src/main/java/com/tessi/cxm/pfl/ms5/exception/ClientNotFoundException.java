package com.tessi.cxm.pfl.ms5.exception;

public class ClientNotFoundException extends RuntimeException {

  public ClientNotFoundException(String clientName) {
    super("Client not found: " + clientName);
  }

  public ClientNotFoundException(long id) {
    super("Client not found " + id);
  }
}
