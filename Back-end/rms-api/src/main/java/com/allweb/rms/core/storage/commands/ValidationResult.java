package com.allweb.rms.core.storage.commands;

import com.allweb.rms.core.storage.StorageConstants;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

public class ValidationResult {

  @Getter private final Map<String, String> messages = new HashMap<>();
  @Getter @Setter private boolean isValid = true;

  public void addError(String key, String message) {
    if (StringUtils.isNotEmpty(message)) {
      messages.put(key, message);
    }
  }

  public void addError(StorageConstants.Errors errorConstants) {
    messages.put(errorConstants.getKey(), errorConstants.getMessage());
  }

  public void removeError(String key, String message) {
    if (StringUtils.isNotEmpty(message)) {
      messages.remove(key);
    }
  }

  public void clear() {
    this.messages.clear();
  }
}
