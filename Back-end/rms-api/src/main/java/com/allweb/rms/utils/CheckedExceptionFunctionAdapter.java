package com.allweb.rms.utils;

import java.util.function.Consumer;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represent an adapter to the {@link java.util.function.Function} that accept an argument and
 * produces a result with a checked exception.
 *
 * <p>This adapter has functions that accept an internal {@link CheckedExceptionFunction} which is
 * functional interface and produces a {@link java.util.function.Function} instead.
 *
 * <p>The internal {@link CheckedExceptionFunction} functional interface is similar to {@link
 * java.util.function.Function} but with the checked exception. Use a lambda expression instead of
 * direct use of {@link CheckedExceptionFunction} for a better readable.
 *
 * @author <a href="mailto:sakal.tum@allweb.com.kh">Sakal TUM</a>
 * @see {@link CheckedExceptionFunction}
 */
public final class CheckedExceptionFunctionAdapter {
  private static final Logger LOGGER =
      LoggerFactory.getLogger(CheckedExceptionFunctionAdapter.class);

  private CheckedExceptionFunctionAdapter() {}

  /**
   * Adapt to the {@link java.util.function.Function} by accepting the {@link
   * CheckedExceptionFunction} as an input argument.
   *
   * @param <T> {@code T} The type of the input to the function.
   * @param <R> {@code R} the type of the result of the function.
   * @param function The function with the checked exception to apply.
   * @return {@link java.util.function.Function}<{@link T}, {@link R}>
   */
  public static <T, R> Function<T, R> applyChecked(
      CheckedExceptionFunction<T, R, Exception> function) {

    return (T t) -> {
      try {
        return function.applyAndChecked(t);
      } catch (Exception e) {
        LOGGER.debug(e.getMessage(), e);
      }
      return null;
    };
  }

  /**
   * Adapt to the {@link java.util.function.Function} by accepting the {@link
   * CheckedExceptionFunction} as an input argument.
   *
   * @param <T> {@code T} The type of the input to the function.
   * @param <R> {@code R} the type of the result of the function.
   * @param function The function with the checked exception to apply.
   * @param onError The operation to perform on the exception thrown.
   * @return {@link java.util.function.Function}<{@link T}, {@link R}>
   */
  public static <T, R> Function<T, R> applyChecked(
      CheckedExceptionFunction<T, R, Exception> function, Consumer<Throwable> onError) {

    return (T t) -> {
      try {
        return function.applyAndChecked(t);
      } catch (Exception e) {
        onError.accept(e);
      }
      return null;
    };
  }

  /**
   * Represents a function with checked exception that accepts one argument and produces a result .
   *
   * <p>This is a functional interface whose functional method is {@link #applyAndChecked(T)}
   *
   * @param <T> The type of the input to the function
   * @param <R> The type of the result of the function
   * @param <E> The type of the exception thrown by the function {@link #applyAndChecked(T)}
   */
  @FunctionalInterface
  public interface CheckedExceptionFunction<T, R, E extends Exception> {

    /**
     * Applies this function to the given argument and may throw an exception.
     *
     * @param t The function argument of type {@link T}
     * @return The function result of type {@link R}
     * @throws E Checkek exception that may be thrown by this function
     */
    R applyAndChecked(T t) throws E;
  }
}
