package com.allweb.rms.utils;

public class StringUtils {
  private StringUtils() {}

  public static String toSnakeCase(String text) {
    return text.replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase();
  }
}
