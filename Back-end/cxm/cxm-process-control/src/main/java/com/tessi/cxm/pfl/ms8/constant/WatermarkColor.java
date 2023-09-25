package com.tessi.cxm.pfl.ms8.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum WatermarkColor {
  BLACK("Black"),
  BLUE("Blue"),
  CYAN("Cyan"),
  DARK_GRAY("Dark_Gray"),
  GRAY("Gray"),
  GREEN("Green"),
  LIGHT_GRAY("Light_Gray"),
  MAGENTA("Magenta"),
  ORANGE("Orange"),
  PINK("Pink"),
  RED("Red"),
  WHITE("White"),
  YELLOW("Yellow");
  private final String value;

  /**
   * If a key is present, returns {@code true}, otherwise {@code false}.
   *
   * @return {@code true} if a value is present, otherwise {@code false}
   */
  public static boolean isPresent(String value) {
    return Arrays.stream(values())
        .anyMatch(watermarkColor -> watermarkColor.value.equalsIgnoreCase(value));
  }
}
