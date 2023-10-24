package com.innovationandtrust.profile.constant;

import java.util.Set;

public enum TemplateType {
  DEFAULT,
  PREDEFINE;

  public static Set<String> types() {
    return Set.of(DEFAULT.name(), PREDEFINE.name());
  }
}
