package com.innovationandtrust.process.utils;

import com.innovationandtrust.share.enums.NotificationReminderOption;
import com.innovationandtrust.process.constant.RegexPatternConstant;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.CronExpression;
import org.springframework.util.StringUtils;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CronExpressionUtils {

  private static final int SECOND = 0;

  private static final int MINUTE = 0;

  public static String buildCronExpression(Date expireDate, NotificationReminderOption reminder) {
    switch (reminder) {
      case ONCE_A_DAY -> {
        return buildOncePerDayCron(expireDate);
      }
      case EVERY_2_DAYS -> {
        return buildEvery2DaysCron(expireDate);
      }
      case ONCE_A_WEEK -> {
        return buildOncePerWeekCron(expireDate);
      }
      case EVERY_2_WEEKS -> {
        return buildEvery2WeeksCron(expireDate);
      }
      case EVERY_FIVE_MINUTE -> {
        return buildEvery5MinuteCron(expireDate);
      }
      default -> {
        return "";
      }
    }
  }

  // For testing
  private static String buildEvery5MinuteCron(Date expireDate) {
    var expression =
        String.format(
            RegexPatternConstant.CRON_EXPRESSION,
            "*",
            "*/15",
            "*",
            "*",
            convertListToString(DateUtil.getMonthValue(expireDate)),
            "?",
            DateUtil.getYear(expireDate));
    return getExpression(expression, NotificationReminderOption.EVERY_FIVE_MINUTE);
  }

  private static String buildOncePerDayCron(Date endDate) {
    var days = DateUtil.getDayOfMonth(endDate);
    if (days.isEmpty()) {
      return "";
    }
    return getExpression(buildExpression(endDate, days), NotificationReminderOption.ONCE_A_DAY);
  }

  private static String buildEvery2DaysCron(Date endDate) {
    return getExpression(
        buildExpression(endDate, getCronDays(endDate, 2)), NotificationReminderOption.EVERY_2_DAYS);
  }

  public static String buildOncePerWeekCron(Date endDate) {
    return getExpression(
        buildExpression(endDate, getCronDays(endDate, 7)), NotificationReminderOption.ONCE_A_WEEK);
  }

  public static String buildEvery2WeeksCron(Date endDate) {
    return getExpression(
        buildExpression(endDate, getCronDays(endDate, 14)),
        NotificationReminderOption.EVERY_2_WEEKS);
  }

  private static List<Integer> getCronDays(Date endDate, int dayToSkip) {
    var startDate = LocalDate.now(DateUtil.DEFAULT_DATE_ZONE);
    long numOfDays =
        ChronoUnit.DAYS.between(
            startDate,
            endDate.toInstant().atZone(DateUtil.DEFAULT_DATE_ZONE).toLocalDate().plusDays(1));
    log.info("Total num of day before expire: {}", numOfDays);
    if (numOfDays <= 0) {
      return List.of();
    }
    if (numOfDays <= dayToSkip) {
      return List.of();
    }
    var days =
        new java.util.ArrayList<>(
            Stream.iterate(startDate, date -> date.plusDays(dayToSkip))
                .limit((long) Math.ceil((double) numOfDays / dayToSkip))
                .map(LocalDate::getDayOfMonth)
                .toList());
    days.removeIf(v -> Objects.equals(v, startDate.getDayOfMonth()));
    return days;
  }

  private static String buildExpression(Date endDate, List<Integer> days) {
    if (days.isEmpty()) {
      return "";
    }
    log.info("Total num of days : {}", days);
    return String.format(
        RegexPatternConstant.CRON_EXPRESSION,
        DateUtil.getSecondOfDay(endDate),
        DateUtil.getMinuteOfDay(endDate),
        DateUtil.getHourOfDay(endDate),
        convertListToString(days),
        convertListToString(DateUtil.getMonthValue(endDate, days)),
        "?",
        DateUtil.getYear(endDate));
  }

  private static String getExpression(String expression, NotificationReminderOption reminder) {
    if (!StringUtils.hasText(expression)) {
      return "";
    }
    try {
      var cronExp = new CronExpression(expression).getCronExpression();
      log.info("Expression after build: " + cronExp);
      return cronExp;
    } catch (ParseException e) {
      log.error("Failed to create cron expression ", e);
      throw new IllegalArgumentException("Invalid cron expression for " + reminder);
    }
  }

  private static String convertListToString(List<Integer> list) {
    return String.join(",", list.stream().distinct().map(String::valueOf).toList());
  }
}
