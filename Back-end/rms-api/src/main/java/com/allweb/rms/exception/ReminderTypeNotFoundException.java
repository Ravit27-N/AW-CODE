package com.allweb.rms.exception;

public class ReminderTypeNotFoundException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public ReminderTypeNotFoundException(String reminderType) {
    super(String.format("Reminder type \"%s\" is not found.", reminderType));
  }
}
