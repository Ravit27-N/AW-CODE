package com.allweb.rms.exception;

public class ReminderNotFoundException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public ReminderNotFoundException(int id) {
    super("Could not find Reminder " + id);
  }
}
