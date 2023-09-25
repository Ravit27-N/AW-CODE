package com.tessi.cxm.pfl.ms3.constant;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DateConvertor {

  public static Date getDate(String stringDate, Logger log) {
    Date date = null;
    try {
      DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
      date = dateFormat.parse(stringDate);
    } catch (ParseException e) {
      log.error("Failed to parse create date of processing.", e);
      if (log.isDebugEnabled()) {
        log.debug("Failed to parse create date of processing.", e);
      }
    }
    return date;
  }

  /**
   * To convert date time of paris {@code Europe/Paris} time-zone to UTC time.
   *
   * @param dateString refer to string of date
   * @return {@code UTC} time that is converted.
   */
  public static Date parisTimeZoneToUTC(String dateString) {
    ZoneId zone = ZoneId.of("Europe/Paris");
    return timeZoneToUTC(zone, dateString);
  }

  /**
   * @param zone A time-zone ID, such as {@code Europe/Paris}.
   *     <p>A {@code ZoneId} is used to identify * the rules used to convert between * an {@link
   *     Instant} and a {@link LocalDateTime}.
   * @param dateString value of data as {@link String}
   * @return {@code UTC} time that is converted.
   */
  public static Date timeZoneToUTC(ZoneId zone, String dateString) {
    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss").withZone(zone);
    ZonedDateTime utc = ZonedDateTime.parse(dateString, fmt).withZoneSameInstant(ZoneOffset.UTC);
    return Date.from(utc.toInstant());
  }
}
