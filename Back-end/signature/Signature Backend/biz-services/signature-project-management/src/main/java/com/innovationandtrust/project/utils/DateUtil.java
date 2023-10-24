package com.innovationandtrust.project.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** Date util for project. */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DateUtil {
  /**
   * For getting date midnight time.
   *
   * @param date is the date time.
   * @return Date object
   */
  public static Date removeTime(Date date) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    return calendar.getTime();
  }

  /**
   * For adding days to date.
   *
   * @param date is the date time
   * @param days amount of days to add
   * @return Date object
   */
  public static Date plushDays(Date date, int days) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.add(Calendar.DATE, days);
    return calendar.getTime();
  }

  public static String convertToUtc(Date date) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    return sdf.format(date);
  }

  public static Date convertFrom(String dateStr, String format) {
    SimpleDateFormat sdf = new SimpleDateFormat(format);
    try {
      return sdf.parse(dateStr);
    } catch (ParseException e) {
      log.error("Cannot parse string: {} to Date with format {}", dateStr, format, e);
      throw new com.innovationandtrust.utils.exception.exceptions.ParseException(
          "Cannot parse date from string" + dateStr);
    }
  }
}
