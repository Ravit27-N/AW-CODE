package com.innovationandtrust.process.utils;

import static java.time.ZoneId.SHORT_IDS;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DateUtil {

  // Zone of Europe/Paris, and convert to this zone
  public static final ZoneId DEFAULT_DATE_ZONE_ECT = ZoneId.of(SHORT_IDS.get("ECT"));

  public static final ZoneId DEFAULT_DATE_ZONE = ZoneId.of(("UTC"));

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

  public static Date removeTime(Date date) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    return calendar.getTime();
  }

  public static String getOffsetOfDate(Date date) {
    LocalDateTime dateTime = LocalDateTime.ofInstant(date.toInstant(), DEFAULT_DATE_ZONE_ECT);
    ZonedDateTime zdt = dateTime.atZone(DEFAULT_DATE_ZONE_ECT);
    ZoneOffset offset = zdt.getOffset();
    return String.format("UTC%s", offset);
  }

  /**
   * To convert {@link Date} to french date format.
   *
   * @param date refers to an object of {@link Date}
   * @return the date after convert as french format
   * @apiNote the example of the date after converted: mercredi 17 mai 2023 à 07:00:00
   */
  public static String toFrenchDateECT(Date date, String dateFormat) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat, Locale.FRANCE);
    LocalDateTime dateTime = LocalDateTime.ofInstant(date.toInstant(), DEFAULT_DATE_ZONE_ECT);
    return dateTime.format(formatter);
  }

  public static String toFrenchDate(Date date) {
    return toFrenchDateECT(date, "EEEE dd MMMM yyyy à HH:mm:ss");
  }

  public static String toSignedDate(Date date) {
    return toFrenchDateECT(date, "dd/MM/yyyy HH:mm:ss");
  }

  public static long getExpiredTime(Date expirationDate) {
    return getCalendar(expirationDate).getTimeInMillis() - new Date().getTime();
  }

  public static Calendar getCalendar(Date date) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    return calendar;
  }

  public static int getYear(Date date) {
    return getCalendar(date).get(Calendar.YEAR);
  }

  public static int getHourOfDay(Date date) {
    return getCalendar(date).get(Calendar.HOUR_OF_DAY);
  }

  public static int getMinuteOfDay(Date date) {
    return getCalendar(date).get(Calendar.MINUTE);
  }

  public static int getSecondOfDay(Date date) {
    return getCalendar(date).get(Calendar.SECOND);
  }

  public static List<Integer> getDayOfMonth(Date date) {
    var startDate = getStartDate();
    log.info("Start date: {}", startDate);
    var endDate = getEndDate(date);
    log.info("End date: {}", endDate);
    return startDate
        .plusDays(1)
        .datesUntil(endDate.plusDays(1))
        .map(LocalDate::getDayOfMonth)
        .distinct()
        .toList();
  }

  public static List<Integer> getMonthValue(Date date, List<Integer> days) {
    return LocalDate.now(DEFAULT_DATE_ZONE)
        .datesUntil(date.toInstant().atZone(DEFAULT_DATE_ZONE).toLocalDate().plusDays(1))
        .filter(d -> days.contains(d.getDayOfMonth()))
        .map(LocalDate::getMonthValue)
        .distinct()
        .toList();
  }

  public static List<Integer> getMonthValue(Date date) {
    return LocalDate.now(DEFAULT_DATE_ZONE)
        .datesUntil(date.toInstant().atZone(DEFAULT_DATE_ZONE).toLocalDate().plusDays(1))
        .map(LocalDate::getMonthValue)
        .distinct()
        .toList();
  }

  public static LocalDate getStartDate() {
    return LocalDate.from(LocalDate.now(DEFAULT_DATE_ZONE).atTime(LocalTime.MIN));
  }

  public static LocalDate getEndDate(Date date) {
    return LocalDate.from(
        date.toInstant().atZone(DEFAULT_DATE_ZONE).toLocalDate().atTime(LocalTime.MAX));
  }

  public static boolean isOneDayExpire(Date date) {
    return DateUtil.getStartDate().equals(DateUtil.getEndDate(date).plusDays(-1));
  }
}
