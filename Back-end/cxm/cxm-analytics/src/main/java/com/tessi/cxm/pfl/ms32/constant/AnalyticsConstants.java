package com.tessi.cxm.pfl.ms32.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AnalyticsConstants {
  public static final String DATE_FORMAT = "yyyy-MM-dd";
  public static final String DATE_FORMAT_ISO = "yyyy-MM-dd HH:mm:ss";

  public static final String BLANK = "Blank";

  public static final String NUMBER_TYPE = "number";
  public static final String PERCENT_TYPE = "percent";
  public static final String TEXT_TYPE = "text";

  public static final String EXPORTING_REQUEST_FILTER_KEY = "REQUEST_FILTER_KEY";
  public static final String CLIENT_FILLERS_KEY = "CLIENT_FILLERS_KEY";
  public static final String CSV_DELIMITER_KEY = "CSV_DELIMITER_KEY";
  public static final String PAGE_SIZE_KEY = "PAGE_SIZE_KEY";
  public static final String TARGET_EXPORTING_TIMEZONE = "TARGET_EXPORTING_TIMEZONE";
}
