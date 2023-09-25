package com.allweb.rms.service.elastic;

import java.util.HashMap;
import java.util.Map;

public class ChainContext {
  private final Map<String, Object> data = new HashMap<>();

  public Object get(String key) {
    return data.get(key);
  }

  public void put(String key, Object object) {
    this.data.put(key, object);
  }

  public boolean contains(String key) {
    return this.data.containsKey(key) && this.data.get(key) != null;
  }
}
