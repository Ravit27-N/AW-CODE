package com.allweb.rms.utils;

import java.util.UUID;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;

@Log4j2
public class UUIDUtils {
  private UUIDUtils() {}

  public static String removeUUIDFromStart(String separator, String text) {
    if (StringUtils.isNotBlank(text) && text.contains(separator)) {
      int index = text.indexOf(separator);
      try {
        UUID uuid = UUID.fromString(text.substring(0, index));
        return text.substring(uuid.toString().length() + 1);
      } catch (IllegalStateException ex) {
        log.debug(ex);
      }
    }
    return text;
  }
}
