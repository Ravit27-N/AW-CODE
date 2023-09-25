package com.tessi.cxm.pfl.ms3.exception;

public class SubChannelNotFoundException extends RuntimeException {
  public SubChannelNotFoundException(String message) {
    super(message);
  }

  public SubChannelNotFoundException() {
    super("Sub channel not found.");
  }
}
