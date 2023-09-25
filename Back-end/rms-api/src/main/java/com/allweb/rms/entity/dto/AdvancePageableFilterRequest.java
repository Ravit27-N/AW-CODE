package com.allweb.rms.entity.dto;

import lombok.Data;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Data
public class AdvancePageableFilterRequest {
  protected Map<String, Object> attributes = new HashMap<>();
  Date from;
  Date to;

  public void addAttribute(String attribute, Object value) {
    this.attributes.put(attribute, value);
  }

  public Object get(String attribute) {
    return this.attributes.get(attribute);
  }

  public <T> T get(String key, Class<? extends T> clazz) {
    Object data = this.attributes.get(key);
    return clazz.cast(data);
  }

  public boolean contains(String attribute) {
    return this.attributes.containsKey(attribute);
  }
}
