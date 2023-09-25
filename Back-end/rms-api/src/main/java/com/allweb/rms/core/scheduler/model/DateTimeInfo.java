package com.allweb.rms.core.scheduler.model;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import lombok.Getter;

public class DateTimeInfo implements Serializable {

  private static final long serialVersionUID = 1L;

  @Getter private LocalDateTime baseDateTime;

  public DateTimeInfo(Date baseDateTime) {
    this.baseDateTime = baseDateTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
  }

  public DateTimeInfo(LocalDateTime baseDateTime) {
    this.baseDateTime = baseDateTime;
  }

  public DateTimeInfo(int year, int month, int day, int hour, int minute, int second) {
    this.baseDateTime = LocalDateTime.of(year, month, day, hour, minute, second);
  }

  public void plus(Duration duration) {
    this.baseDateTime = this.baseDateTime.plus(duration);
  }

  public void minus(Duration duration) {
    this.baseDateTime = this.baseDateTime.minus(duration);
  }

  /**
   * Get the {@link Date} represented by this {@link DateTimeInfo}.
   *
   * @return
   */
  public Date toDate() {
    return Date.from(this.baseDateTime.atZone(ZoneId.systemDefault()).toInstant());
  }
}
