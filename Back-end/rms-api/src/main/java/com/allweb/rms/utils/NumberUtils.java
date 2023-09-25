package com.allweb.rms.utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;

public final class NumberUtils {
  private static final Set<Class<? extends Number>> SUPPORTED_NUMBER_CLASSES;

  static {
    List<? extends Class<? extends Number>> classes =
        Arrays.asList(Short.class, Integer.class, Long.class, Float.class, Double.class);
    SUPPORTED_NUMBER_CLASSES = new HashSet<>(classes);
  }

  private NumberUtils() {}

  /**
   * Parse a text to a specific number type and return default value instead of exception.
   *
   * @param text Text to be parse
   * @param targetClass Target class of type which extends {@link Number}.
   * @param <T> Specific number type which extends {@link Number}.
   * @return Default or a successful parsed value of type {@link T}
   * @see NumberUtils#isSupportedNumberClass(Class)
   * @see NumberUtils#getDefaultValue(Class)
   */
  public static <T extends Number> T tryParseNumber(String text, Class<T> targetClass) {
    T parseResult = null;
    if (StringUtils.isNotBlank(text) && isSupportedNumberClass(targetClass)) {
      try {
        parseResult = org.springframework.util.NumberUtils.parseNumber(text, targetClass);
      } catch (NumberFormatException numberFormatException) {
        parseResult = getDefaultValue(targetClass);
      }
    }
    return parseResult;
  }

  /**
   * Check if a {@code targetClass} is supported for parse.
   *
   * @param targetClass Target class of type which extends {@link Number}.
   * @param <T> Specific number type which extends {@link Number}.
   * @return Default or a successful parsed value of type {@link T}.
   */
  public static <T extends Number> boolean isSupportedNumberClass(Class<T> targetClass) {
    return SUPPORTED_NUMBER_CLASSES.contains(targetClass);
  }

  /**
   * @param targetClass Target class of type which extends {@link Number}.
   * @param <T> Specific number type which extends {@link Number}.
   * @return Default value of type {@link T}.
   */
  @SuppressWarnings("unchecked")
  public static <T> T getDefaultValue(Class<T> targetClass) {
    if (targetClass == short.class || targetClass == Short.class) {
      return (T) Short.valueOf((short) 0);
    } else if (targetClass == int.class || targetClass == Integer.class) {
      return (T) Integer.valueOf(0);
    } else if (targetClass == long.class || targetClass == Long.class) {
      return (T) Long.valueOf(0L);
    } else if (targetClass == float.class || targetClass == Float.class) {
      return (T) Float.valueOf(0F);
    } else if (targetClass == double.class || targetClass == Double.class) {
      return (T) Double.valueOf(0D);
    } else {
      return null;
    }
  }
}
