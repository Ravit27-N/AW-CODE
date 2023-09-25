package com.allweb.rms.utils;

import java.util.function.Predicate;
import org.apache.commons.lang3.exception.ExceptionUtils;

public final class ThrowablePredicateAdapter {

  public static <I, E extends Exception> Predicate<I> createThrowablePredicate(
      ThrowablePredicate<I, E> throwablePredicate) {
    return (I input) -> {
      try {
        return throwablePredicate.test(input);
      } catch (Exception e) {
        ExceptionUtils.rethrow(e);
      }
      return false;
    };
  }

  public interface ThrowablePredicate<I, E extends Exception> {
    Boolean test(I input) throws E;
  }
}
