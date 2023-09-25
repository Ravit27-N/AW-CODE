package com.allweb.rms.exception;

public class MailTemplateNotFoundException extends RuntimeException {
  /** */
  private static final long serialVersionUID = -61577549232301820L;

  public MailTemplateNotFoundException(String message) {
    super(message);
  }
}
