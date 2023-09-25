package com.tessi.cxm.pfl.ms5.util;

import com.tessi.cxm.pfl.ms5.exception.InvalidDurationPattern;
import java.time.Duration;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateUtils;

@Slf4j
public class UserResetPasswordValidator {

  public static final String DURATION_TOKEN_EXPIRED = "DURATION_TOKEN_EXPIRED";
  private static final Map<Character, Function<String, Duration>> DURATION_PARSERS =
      new HashMap<>();

  static {
    DURATION_PARSERS.put('S', txt -> Duration.ofSeconds(Integer.parseInt(txt)));
    DURATION_PARSERS.put('M', txt -> Duration.ofMinutes(Integer.parseInt(txt)));
    DURATION_PARSERS.put('H', txt -> Duration.ofHours(Integer.parseInt(txt)));
    DURATION_PARSERS.put('D', txt -> Duration.ofDays(Integer.parseInt(txt)));
    // input more durations type...
  }

  private UserResetPasswordValidator() {
  }

  /**
   * Check expired date with current date.
   *
   * @return true if expired
   */
  public static boolean isTokenExpired(Date expiredDate) {
    return com.tessi.cxm.pfl.shared.utils.DateUtils.isLessThanCurrentDate(expiredDate);
  }

  /**
   * Convert duration from string to Duration object.
   *
   * @param duration of input string
   * @return duration of input string
   * @throws InvalidDurationPattern if invalid format of duration
   */
  public static Duration durationParser(String duration) {
    char durationIdentify = duration.charAt(duration.length() - 1);
    log.info("character of duration {}", durationIdentify);
    Function<String, Duration> parser =
        DURATION_PARSERS.get(Character.toUpperCase(durationIdentify));
    if (parser != null) {
      final Duration durationOfCharacter =
          parser.apply(duration.substring(0, duration.length() - 1));
      log.info("the number of minutes in this duration {}", durationOfCharacter.toMinutes());
      return durationOfCharacter;
    }
    throw new InvalidDurationPattern(
        "Invalid Duration pattern, The number of (days, hours ,minutes and seconds) must parse to a long that may have from zero to 9 digits and have suffixes in ASCII of \"D\", \"H\", \"M\" and \"S\" for days, hours, minutes and seconds, accepted in upper or lower case.");
  }

  /**
   * Get expired date.
   *
   * @param minute is minutes to plush with current date
   * @return date expired
   */
  public static Date getExpiredDate(int minute) {
    Date targetTime = Calendar.getInstance(TimeZone.getTimeZone(ZoneId.systemDefault())).getTime();
    return DateUtils.addMinutes(targetTime, minute);
  }
}
