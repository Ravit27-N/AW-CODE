package com.allweb.rms.core.scheduler.batch.exception;

import com.allweb.rms.entity.jpa.Reminder;

public class ReminderInvalidException extends RuntimeException {
  private static final long serialVersionUID = 5089325810992350139L;

  public ReminderInvalidException(Reminder reminder) {
    super(buildErrorMessage(reminder));
  }

  private static String buildErrorMessage(Reminder reminder) {
    String message = String.format("Reminder with id \"%d\" is ", reminder.getId());
    if (!reminder.isActive()) {
      message += "is not active.";
    } else if (reminder.isDeleted()) {
      message += "is deleted.";
    } else if (reminder.isSend()) {
      message += "is already send.";
    }
    return message;
  }
}
