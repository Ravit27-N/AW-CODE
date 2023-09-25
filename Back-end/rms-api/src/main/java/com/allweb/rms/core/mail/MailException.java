package com.allweb.rms.core.mail;

public class MailException extends Exception {
  private static final long serialVersionUID = 2562758203904789024L;

  public MailException(String message) {
    super(message);
  }

  public MailException(String message, Throwable cause) {
    super(message, cause);
  }

  public MailException(Throwable cause) {
    super(cause);
  }
}
