package com.tessi.cxm.pfl.ms5.exception;

import java.util.List;

public class PublicHolidayNotFoundException extends RuntimeException {

  public PublicHolidayNotFoundException(List<Long> publicHolidayId) {
    super("Public holiday is not found: " + publicHolidayId);
  }
}
