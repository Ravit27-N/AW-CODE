package com.innovationandtrust.share.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.innovationandtrust.share.constant.ScenarioStepConstant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum ScenarioStep {
  APPROVAL(ScenarioStepConstant.APPROVAL, false),
  ORDER(ScenarioStepConstant.ORDER, true),
  LEGAL(ScenarioStepConstant.LEGAL, false),
  COSIGN(ScenarioStepConstant.COSIGN, false),
  INDIVIDUAL_SIGN(ScenarioStepConstant.INDIVIDUAL_SIGN, false),
  ORDERED_COSIGN(ScenarioStepConstant.ORDERED_COSIGN, true),
  COUNTER_SIGN(ScenarioStepConstant.COUNTER_SIGN, true);

  private static final Map<String, ScenarioStep> BY_VALUE = new HashMap<>();

  static {
    Arrays.stream(values()).forEach(v -> BY_VALUE.put(v.strValue, v));
  }

  private final String strValue;

  private final boolean isRequiredOrder;

  ScenarioStep(String value, boolean isRequiredOrder) {
    this.strValue = value;
    this.isRequiredOrder = isRequiredOrder;
  }

  @JsonValue
  public String getVal() {
    return strValue;
  }

  public static ScenarioStep getByValue(String strValue) {
    var key = BY_VALUE.get(strValue);
    if (key == null) {
      throw new IllegalArgumentException("Invalid scenario tag " + strValue);
    }
    return key;
  }

  public boolean isRequiredOrder() {
   return this.isRequiredOrder;
  }
}
