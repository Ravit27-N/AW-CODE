package com.tessi.cxm.pfl.ms5.exception;

public class ClientNameNotModifiableException extends
    RuntimeException {

  public ClientNameNotModifiableException() {
    super("Client name cannot be modified.");
  }
}
