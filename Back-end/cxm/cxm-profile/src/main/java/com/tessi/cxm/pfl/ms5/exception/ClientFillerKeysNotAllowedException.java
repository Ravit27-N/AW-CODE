package com.tessi.cxm.pfl.ms5.exception;

public class ClientFillerKeysNotAllowedException extends RuntimeException {

  public ClientFillerKeysNotAllowedException(String noneMatch) {
    super("Client filler keys is not allowed: " + noneMatch);
  }
}
