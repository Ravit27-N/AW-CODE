package com.allweb.rms.exception;

public class MailConfigurationNotFoundException extends RuntimeException {
  /** */
  private static final long serialVersionUID = -61577549232301820L;

  public MailConfigurationNotFoundException(String message) {
    super(message);
  }
}
