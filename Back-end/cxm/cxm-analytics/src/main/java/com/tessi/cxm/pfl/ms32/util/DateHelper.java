package com.tessi.cxm.pfl.ms32.util;

import com.tessi.cxm.pfl.ms32.dto.UserFilterPreferenceDto;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import org.apache.commons.lang3.time.DateUtils;

public class DateHelper {

  private DateHelper() {
  }

  public static Date startOfDate(Date date) {
    return DateUtils.truncate(date, Calendar.DATE);
  }

  public static Date endOfDate(Date date) {
    return DateUtils.addMilliseconds(DateUtils.ceiling(date, Calendar.DATE), 0);
  }

  public static Date today() {
    final Calendar calender = Calendar.getInstance();
    return calender.getTime();
  }

  public static Date yesterday() {
    final Calendar calender = Calendar.getInstance();
    calender.add(Calendar.DATE, -1);
    return calender.getTime();
  }

  public static Date startOfLast7days() {
    final Calendar calender = Calendar.getInstance();
    calender.add(Calendar.DAY_OF_MONTH, -6);
    return calender.getTime();
  }

  public static Date startOfLast30days() {
    final Calendar calender = Calendar.getInstance();
    calender.add(Calendar.DAY_OF_MONTH, -29);
    return calender.getTime();
  }

  public static Date firstDayOfThisMonth() {
    final Calendar calender = Calendar.getInstance();
    calender.add(Calendar.MONTH, 0);
    calender.set(Calendar.DATE, 1);

    return DateUtils.truncate(calender.getTime(), Calendar.MONTH);
  }

  public static Date lastDayOfThisMonth() {
    final Calendar calender = Calendar.getInstance();
    calender.add(Calendar.MONTH, 0);
    calender.set(Calendar.DATE, calender.getActualMaximum(Calendar.DAY_OF_MONTH));
    return DateUtils.addMilliseconds(DateUtils.ceiling(calender.getTime(), Calendar.MONTH), -1);
  }

  public static Date firstDayOfLastMonth() {
    final Calendar calender = Calendar.getInstance();
    calender.add(Calendar.MONTH, -1);
    calender.set(Calendar.DATE, 1);
    return calender.getTime();
  }

  public static Date lastDayOfLastMonth() {
    final Calendar calender = Calendar.getInstance();
    calender.add(Calendar.MONTH, -1);
    calender.set(Calendar.DATE, calender.getActualMaximum(Calendar.DAY_OF_MONTH));
    return DateUtils.addMilliseconds(DateUtils.ceiling(calender.getTime(), Calendar.MONTH), -1);
  }

  public static Date firstDayOfLast3Months() {
    final Calendar calender = Calendar.getInstance();
    calender.add(Calendar.MONTH, -3);
    calender.set(Calendar.DATE, 1);
    return calender.getTime();
  }

  public static Date lastDayOfLast3Months() {
    final Calendar calender = Calendar.getInstance();
    calender.add(Calendar.MONTH, -1);
    calender.set(Calendar.DATE, calender.getActualMaximum(Calendar.DAY_OF_MONTH));
    return calender.getTime();
  }

  /**
   * Mapping object of {@link UserFilterPreferenceDto} by replacing value of (startDate, endDate) by
   * user selectDateType.
   *
   * @param dto - object of {@link UserFilterPreferenceDto}.
   * @return - object of {@link UserFilterPreferenceDto}.
   */
  public static UserFilterPreferenceDto mappingUserFilterPreferenceDto(
      UserFilterPreferenceDto dto) {
    Date startDate;
    Date endDate;
    switch (dto.getSelectDateType()) {
      case "0": // TODAY
        startDate = endDate = today();
        break;
      case "1": // YESTERDAY
        startDate = endDate = yesterday();
        break;
      case "2": // LAST_7_DAYS
        startDate = startOfLast7days();
        endDate = today();
        break;
      case "3": // LAST_30_DAYS
        startDate = startOfLast30days();
        endDate = today();
        break;
      case "4": // THIS_MONTH
        startDate = firstDayOfThisMonth();
        endDate = lastDayOfThisMonth();
        break;
      case "5": // LAST_MONTH
        startDate = firstDayOfLastMonth();
        endDate = lastDayOfLastMonth();
        break;
      case "6": // LAST_3_MONTHS
        startDate = firstDayOfLast3Months();
        endDate = lastDayOfLastMonth();
        break;
      default: // CUSTOM_RANGES
        startDate = new Date(dto.getCustomStartDate().getTime());
        endDate = new Date(dto.getCustomEndDate().getTime());
        break;
    }

    // Set final data(startDate, endDate).
    dto.setCustomStartDate(startDate);
    dto.setCustomEndDate(endDate);
    return dto;
  }

  public static ZonedDateTime getByTimeZone(Date date, ZoneId targetZoneId) {
    ZoneId defZoneId = ZoneId.systemDefault();
    ZonedDateTime date1 = ZonedDateTime.ofInstant(date.toInstant(), defZoneId);
    return date1.withZoneSameInstant(targetZoneId);
  }
}
