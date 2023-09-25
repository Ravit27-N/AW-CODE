package com.tessi.cxm.pfl.ms32.util;

public class AnalyticsCalculatorUtils {
  private AnalyticsCalculatorUtils() {}

  public static double getTotalPercentage(long obtained, double total) {
    double percentage = (obtained / total) * 100.0;
    return Math.round(percentage * Math.pow(10, 2)) / Math.pow(10, 2);
  }
}
