package com.tessi.cxm.pfl.ms32.constant;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

public final class CSVExportingConstant {

  private CSVExportingConstant() {
  }

  public static final String GLOBAL_PREFIX = "Stat_global";
  public static final String SPECIFIC_PREFIX = "Stat";
  public static final String EXTENSION = "csv";
  public static final String CONTENT_TYPE = "application/csv";
  public static final String CSV_NAME_DATE_FORMAT = "yyyyMMddHHmmss";
  public static final String DATE_FORMAT_PATTERN = "dd/MM/yyyy HH:mm";
  private static final DateFormat DATE_FORMAT;

  static {
    DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT_PATTERN);
  }

  public static DateFormat getDateFormat(){
    return DATE_FORMAT;
  }

  public static DateTimeFormatter getDateTimeFormatter(){
    return DateTimeFormatter.ofPattern(DATE_FORMAT_PATTERN);
  }
}
