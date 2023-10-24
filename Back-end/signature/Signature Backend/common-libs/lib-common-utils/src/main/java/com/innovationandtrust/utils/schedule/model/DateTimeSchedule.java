package com.innovationandtrust.utils.schedule.model;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import lombok.Getter;

public class DateTimeSchedule implements Serializable {

  @Getter private LocalDateTime baseDateTime;

  @Getter private ZoneId zoneId;

  public DateTimeSchedule(Date baseDateTime) {
    this.zoneId = ZoneId.of("UTC");
    this.baseDateTime = baseDateTime.toInstant().atZone(this.zoneId).toLocalDateTime();
  }

  public DateTimeSchedule(Date baseDateTime, ZoneId zoneId) {
    this.zoneId = zoneId;
    this.baseDateTime = baseDateTime.toInstant().atZone(zoneId).toLocalDateTime();
  }

  public DateTimeSchedule(LocalDateTime baseDateTime, ZoneId zoneId) {
    this.baseDateTime = baseDateTime.atZone(zoneId).toLocalDateTime();
  }

  public DateTimeSchedule(int year, int month, int day, int hour, int minute, int second) {
    this.baseDateTime = LocalDateTime.of(year, month, day, hour, minute, second);
  }

  public void plus(Duration duration) {
    this.baseDateTime = this.baseDateTime.plus(duration);
  }

  public void minus(Duration duration) {
    this.baseDateTime = this.baseDateTime.minus(duration);
  }

  /**
   * Get the {@link Date} represented by this {@link DateTimeSchedule}.
   *
   * @return the date of {@link Date} after converted
   */
  public Date toDate() {
    return Date.from(this.baseDateTime.atZone(this.zoneId).toInstant());
  }

  /**
   * Get the {@link Date} represented by this {@link DateTimeSchedule}.
   *
   * @param zoneId refers to the specific zone to convert
   * @return the date of {@link Date} after converted
   */
  public Date toDate(ZoneId zoneId) {
    return Date.from(this.baseDateTime.atZone(zoneId).toInstant());
  }
}
