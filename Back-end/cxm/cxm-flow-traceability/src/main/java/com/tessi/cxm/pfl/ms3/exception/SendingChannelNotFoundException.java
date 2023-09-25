package com.tessi.cxm.pfl.ms3.exception;

public class SendingChannelNotFoundException extends RuntimeException {
  public SendingChannelNotFoundException(String message) {
    super(message);
  }

  public SendingChannelNotFoundException() {
    super("Sending channel not found.");
  }
}
