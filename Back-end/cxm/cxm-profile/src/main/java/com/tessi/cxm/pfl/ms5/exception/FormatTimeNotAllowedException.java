package com.tessi.cxm.pfl.ms5.exception;

public class FormatTimeNotAllowedException extends RuntimeException {

  public FormatTimeNotAllowedException(String time) {
    super("Invalid value for HourOfDay (valid values 0 - 23): " + time);
  }
}
