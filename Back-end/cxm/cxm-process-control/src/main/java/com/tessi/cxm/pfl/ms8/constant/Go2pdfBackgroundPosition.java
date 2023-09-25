package com.tessi.cxm.pfl.ms8.constant;

import com.tessi.cxm.pfl.shared.utils.BackgroundPositionConstant;
import com.tessi.cxm.pfl.shared.utils.Go2pdfBackgroundPositionConstant;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@RequiredArgsConstructor
@Getter
public enum Go2pdfBackgroundPosition {
  ALL_PAGES(
      BackgroundPositionConstant.ALL_PAGES,
      Go2pdfBackgroundPositionConstant.ALL,
      "For all pages"),
  FIRST_PAGE(
      BackgroundPositionConstant.FIRST_PAGE,
      Go2pdfBackgroundPositionConstant.FIRST,
      "For just the first page"),
  NEXT_PAGES(
      BackgroundPositionConstant.NEXT_PAGES,
      Go2pdfBackgroundPositionConstant.NEXT,
      "For all pages except first and last"),
  LAST_PAGE(
      BackgroundPositionConstant.LAST_PAGE, Go2pdfBackgroundPositionConstant.LAST, "For the last");
  private static final Map<String, Go2pdfBackgroundPosition> BY_VALUE = new HashMap<>();
  private static final Map<String, Go2pdfBackgroundPosition> BY_KEY = new HashMap<>();

  private final String key;
  private final String value;
  private final String description;

  static {
    for (Go2pdfBackgroundPosition backgroundPosition : values()) {
      BY_VALUE.put(backgroundPosition.value.toLowerCase(Locale.ROOT), backgroundPosition);
      BY_KEY.put(backgroundPosition.key.toLowerCase(Locale.ROOT), backgroundPosition);
    }
  }

  public static Go2pdfBackgroundPosition getValueByKey(String key) {
    return BY_KEY.get(key.toLowerCase(Locale.ROOT));
  }

  public static Go2pdfBackgroundPosition getKeyByValue(String value) {
    return BY_VALUE.get(value.toLowerCase(Locale.ROOT));
  }
}
