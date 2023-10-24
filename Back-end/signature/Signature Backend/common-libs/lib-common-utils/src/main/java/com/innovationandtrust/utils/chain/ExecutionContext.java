package com.innovationandtrust.utils.chain;

import java.util.HashMap;
import java.util.Optional;

public class ExecutionContext extends HashMap<String, Object> {

  /**
   * Get value by specific key.
   *
   * @param key Key.
   * @return Value of a specific key.
   */
  public <T> T get(String key, Class<T> clazz) {
    var value = this.get(key);
    if (value != null) {
      return clazz.cast(value);
    }
    return null;
  }

  /**
   * Get value by specific key. If value is not found, return a default value.
   *
   * @param key Key.
   * @param clazz Class of value to be return.
   * @param defaultValue Default value to be return if value not found.
   * @return Value of a specific key.
   */
  public <T> T getOrElse(String key, Class<T> clazz, T defaultValue) {
    var value = this.get(key, clazz);
    return value != null ? value : defaultValue;
  }

  /**
   * Find and get value by specific key.
   *
   * @param key Key.
   * @return Optional value of a specific key.
   */
  public <T> Optional<T> find(String key, Class<T> clazz) {
    var value = this.get(key);
    if (value != null) {
      return Optional.of(clazz.cast(value));
    }
    return Optional.empty();
  }
}
