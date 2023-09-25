package com.tessi.cxm.pfl.ms3.exception;

public class ChannelNotFoundException extends RuntimeException {
  public ChannelNotFoundException(String message) {
    super(message);
  }

  public ChannelNotFoundException() {
    super("Channel not found.");
  }
}
