package com.tessi.cxm.pfl.ms5.util;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.util.Pair;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HolidayCalculator {

  public static final String[] HOLIDAY_LABELS = {
    "new_year_day",
    // "easter_day",
    "easter_monday",
    "labour_day",
    "victory_day",
    "ascension_thursday",
    // "pentecost",
    "pentecost_monday",
    "national_day",
    "assumption_day",
    "all_saints_day",
    "armistice_day",
    "christmas_day"
  };

  private static final int NEW_YEAR_DAY = 1;
  private static final int EASTER_MONDAY = 2;
  private static final int LABOUR_DAY = 3;
  private static final int VICTORY_DAY = 4;
  private static final int ASCENSION_THURSDAY = 5;
  private static final int PENTECOST_MONDAY = 6;
  private static final int NATIONAL_DAY = 7;
  private static final int ASSUMPTION_DAY = 8;
  private static final int ALL_SAINTS_DAY = 9;
  private static final int ARMISTICE_DAY = 10;
  private static final int CHRISTMAS_DAY = 11;

  private static HolidayCalculator calculator;
  private int calYear;
  private LocalDate[] holidays;

  // Singleton access
  public static HolidayCalculator instance() {
    if (calculator == null) {
      calculator = new HolidayCalculator();
    }

    return calculator;
  }

  private void calculate(int year) {
    holidays = new LocalDate[11];

    // creat new year calendar
    // new_year_day: =DATE(B1,1,1)
    Calendar calendar =
        new Calendar.Builder()
            .set(Calendar.DAY_OF_MONTH, 1)
            .set(Calendar.MONTH, Calendar.JANUARY)
            .set(Calendar.YEAR, year)
            .build();
    holidays[excel(NEW_YEAR_DAY)] = toLocalDate(calendar.getTime());

    var eastPair = this.findEasterDay(year);
    calendar.set(Calendar.MONTH, eastPair.getFirst() - 1);
    calendar.set(Calendar.DAY_OF_MONTH, eastPair.getSecond());

    // =B3+1
    calendar.roll(Calendar.DAY_OF_MONTH, 1);
    holidays[excel(EASTER_MONDAY)] = toLocalDate(calendar.getTime());

    // =DATE(B1,5,1)
    calendar.set(Calendar.MONTH, Calendar.MAY);
    calendar.set(Calendar.DAY_OF_MONTH, 1);
    holidays[excel(LABOUR_DAY)] = toLocalDate(calendar.getTime());

    // =DATE(B1,5,8)
    calendar.set(Calendar.MONTH, Calendar.MAY);
    calendar.set(Calendar.DAY_OF_MONTH, 8);
    holidays[excel(VICTORY_DAY)] = toLocalDate(calendar.getTime());

    // =B3+39
    calendar.set(Calendar.MONTH, eastPair.getFirst() - 1);
    calendar.set(Calendar.DAY_OF_MONTH, eastPair.getSecond());
    calendar.roll(Calendar.DAY_OF_YEAR, 39);
    holidays[excel(ASCENSION_THURSDAY)] = toLocalDate(calendar.getTime());

    // =B3+49
    calendar.set(Calendar.MONTH, eastPair.getFirst() - 1);
    calendar.set(Calendar.DAY_OF_MONTH, eastPair.getSecond());
    calendar.roll(Calendar.DAY_OF_YEAR, 49);

    // =B3+50
    calendar.set(Calendar.MONTH, eastPair.getFirst() - 1);
    calendar.set(Calendar.DAY_OF_MONTH, eastPair.getSecond());
    calendar.roll(Calendar.DAY_OF_YEAR, 50);
    holidays[excel(PENTECOST_MONDAY)] = toLocalDate(calendar.getTime());

    // =DATE(B1,7,14)
    calendar.set(Calendar.MONTH, Calendar.JULY);
    calendar.set(Calendar.DAY_OF_MONTH, 14);
    holidays[excel(NATIONAL_DAY)] = toLocalDate(calendar.getTime());

    // =DATE(B1,8,15)
    calendar.set(Calendar.MONTH, Calendar.AUGUST);
    calendar.set(Calendar.DAY_OF_MONTH, 15);
    holidays[excel(ASSUMPTION_DAY)] = toLocalDate(calendar.getTime());

    // =DATE(B1,11,1)
    calendar.set(Calendar.MONTH, Calendar.NOVEMBER);
    calendar.set(Calendar.DAY_OF_MONTH, 1);
    holidays[excel(ALL_SAINTS_DAY)] = toLocalDate(calendar.getTime());

    // =DATE(B1,11,11)
    calendar.set(Calendar.MONTH, Calendar.NOVEMBER);
    calendar.set(Calendar.DAY_OF_MONTH, 11);
    holidays[excel(ARMISTICE_DAY)] = toLocalDate(calendar.getTime());

    // =DATE(B1,12,25)
    calendar.set(Calendar.MONTH, Calendar.DECEMBER);
    calendar.set(Calendar.DAY_OF_MONTH, 25);
    holidays[excel(CHRISTMAS_DAY)] = toLocalDate(calendar.getTime());
  }

  private int excel(int excelRow) {
    return (excelRow - 1);
  }

  public LocalDate[] getHoliday(int year) {
    if (calYear != year) {
      calculate(year);
    }

    return holidays;
  }

  private Pair<Integer, Integer> findEasterDay(int year) {
    float A, B, C, P, Q, M, N, D, E;

    // All calculations done on the basis of Gauss Easter Algorithm
    A = year % 19;
    B = year % 4;
    C = year % 7;
    P = (float) Math.floor(year / 100);
    Q = (float) Math.floor((13 + 8 * P) / 25);
    M = (int) (15 - Q + P - Math.floor(P / 4)) % 30;
    N = (int) (4 + P - Math.floor(P / 4)) % 7;
    D = (19 * A + M) % 30;
    E = (2 * B + 4 * C + 6 * D + N) % 7;
    int days = (int) (22 + D + E);

    // A corner case, when D is 29
    if ((D == 29) && (E == 6)) {
      return Pair.of(4, 19);
    } else if ((D == 28) && (E == 6)) {
      return Pair.of(4, 18);
    } else {
      if (days > 31) {
        return Pair.of(4, days - 31);
      } else {
        return Pair.of(3, days);
      }
    }
  }

  public static LocalDate toLocalDate(Date date) {
    return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
  }
}
