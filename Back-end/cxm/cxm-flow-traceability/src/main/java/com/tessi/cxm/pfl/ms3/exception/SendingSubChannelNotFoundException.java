package com.tessi.cxm.pfl.ms3.exception;

public class SendingSubChannelNotFoundException extends RuntimeException {
  public SendingSubChannelNotFoundException(String message) {
    super(message);
  }

  public SendingSubChannelNotFoundException() {
    super("Sending sub-channel not found.");
  }
}
