package com.allweb.rms.utils;

import java.util.function.Consumer;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * Represent an adapter to the {@link java.util.function.Consumer} that accepts an argument and
 * returns no result and may throw an exception at runtime.
 *
 * <p>This adapter has functions that accept an internal {@link RethrowableExceptionConsumer} which
 * is functional interface and produces a {@link java.util.function.Consumer} instead.
 *
 * <p>The internal {@link RethrowableExceptionConsumer} functional interface is similar to {@link
 * java.util.function.Consumer} but with the checked exception. Use a lambda expression instead of
 * direct use of {@link RethrowableExceptionConsumer} for a better readable.
 *
 * @author <a href="mailto:sakal.tum@allweb.com.kh">Sakal TUM</a>
 * @see {@link RethrowableExceptionConsumer}
 */
public final class RethrowableExceptionConsumerAdapter {

  private RethrowableExceptionConsumerAdapter() {}

  /**
   * Adapt to the {@link java.util.function.Consumer} by accepting the {@link
   * RethrowableExceptionConsumer} as an input argument.
   *
   * @param <T> {@code T} The type of the input to the function
   * @param rethrowableExceptionConsumer The function that accepts an argument and returns no result
   *     and may throw an exception.
   * @return {@link java.util.function.Consumer}
   */
  public static <T> Consumer<T> acceptMayThrow(
      RethrowableExceptionConsumer<T, Exception> rethrowableExceptionConsumer) {
    return (T t) -> {
      try {
        rethrowableExceptionConsumer.acceptAndRethrow(t);
      } catch (Exception e) {
        ExceptionUtils.rethrow(e);
      }
    };
  }

  /**
   * Adapt to the {@link java.util.function.Consumer} by accepting the {@link
   * RethrowableExceptionConsumer} as an input argument.
   *
   * @param <T> {@code T} The type of the input to the function
   * @param rethrowableExceptionConsumer The function that accepts an argument and returns no result
   *     and may throw an exception.
   * @param onError The operation to perform on the exception thrown.
   * @return {@link java.util.function.Consumer}
   */
  public static <T> Consumer<T> acceptMayThrow(
      RethrowableExceptionConsumer<T, Exception> rethrowableExceptionConsumer,
      Consumer<Throwable> onError) {
    return (T t) -> {
      try {
        rethrowableExceptionConsumer.acceptAndRethrow(t);
      } catch (Exception e) {
        onError.accept(e);
      }
    };
  }

  /**
   * Represents an operation that accepts a single input argument and returns no result and may
   * throw an exception.
   *
   * <p>This is a functional interface whose functional method is {@link #rethrowable(T)}.
   *
   * @param <T> {@code T} The type of the input to the function
   * @param <E> {@code E} The type that extends {@link java.lang.Exception} thrown by the function
   */
  @FunctionalInterface
  public interface RethrowableExceptionConsumer<T, E extends Exception> {

    /**
     * Performs this operation on the given argument and throws an exception when error occurred.
     *
     * @param t The input argument.
     * @throws E Exception which extends {@link java.lang.Exception}.
     */
    void acceptAndRethrow(T t) throws E;
  }
}
