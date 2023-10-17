package com.allweb.shared.core.chains;

import java.util.HashMap;

public class ExecutionContext extends HashMap<String, Object> {

  public <T> T get(String key, Class<T> clazz) {
    var value = this.get(key);
    if (value != null) {
      return clazz.cast(value);
    }
    return null;
  }

  public <T> T getOrElse(String key, Class<T> clazz, T defaultValue) {
    var value = this.get(key);
    if (value != null) {
      return clazz.cast(value);
    }
    return defaultValue;
  }
}