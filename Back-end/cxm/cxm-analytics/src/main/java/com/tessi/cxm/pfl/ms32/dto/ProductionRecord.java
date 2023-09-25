package com.tessi.cxm.pfl.ms32.dto;

import com.tessi.cxm.pfl.ms32.constant.CSVExportingConstant;
import com.tessi.cxm.pfl.ms32.util.DateHelper;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Objects;

public class ProductionRecord {
  protected String getStringDateFormat(Date date, String timeZone) {
    if (!Objects.isNull(date)) {
      ZonedDateTime zonedDateTime = DateHelper.getByTimeZone(date, ZoneId.of(timeZone));
      return zonedDateTime.format(CSVExportingConstant.getDateTimeFormatter());
    }
    return "";
  }
}
