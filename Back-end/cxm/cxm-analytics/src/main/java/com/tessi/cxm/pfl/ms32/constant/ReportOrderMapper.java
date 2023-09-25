package com.tessi.cxm.pfl.ms32.constant;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ReportOrderMapper {

  private ReportOrderMapper() {}

  public static Stream<String[]> documentReportLabelMap() {
    var postal = new String[] {"postal", "Postal", "1"};
    var email = new String[] {"email", "Email", "2"};
    var sms = new String[] {"sms", "SMS", "3"};
    return Stream.of(postal, email, sms);
  }

  public static int orderLabel(String name) {
    return documentReportLabelMap()
        .collect(Collectors.toMap(data -> data[0], data -> Integer.parseInt(data[2])))
        .getOrDefault(name.toLowerCase(), 0);
  }

  public static String mapCapitalizeLabel(String name) {
    return documentReportLabelMap()
        .collect(Collectors.toMap(data -> data[0], data -> data[1]))
        .getOrDefault(name.toLowerCase(), name.toUpperCase());
  }

  public static List<Date> listBetweenTwoDate(Date start, Date end) {

    return toLocalDate(start)
        .datesUntil(toLocalDate(end).plusDays(1))
        .collect(Collectors.toList())
        .stream()
        .map(java.sql.Date::valueOf)
        .collect(Collectors.toList());
  }

  public static LocalDate toLocalDate(Date date) {
    return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
  }
}
