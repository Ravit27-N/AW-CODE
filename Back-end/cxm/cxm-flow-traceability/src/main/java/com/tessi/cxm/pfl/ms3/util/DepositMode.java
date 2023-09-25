package com.tessi.cxm.pfl.ms3.util;

import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants;
import com.tessi.cxm.pfl.shared.utils.FlowTraceabilityConstant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonValue;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.stream.Collectors;

/** Enumeration of cxm-flow-traceability deposit mode. */
@Schema(enumAsRef = true)
public enum DepositMode {
  PORTAL("flow.traceability.deposit.mode.portal", FlowTreatmentConstants.PORTAL_DEPOSIT),
  VIRTUAL_PRINTER(
      "flow.traceability.deposit.mode.virtualPrinter", FlowTreatmentConstants.IV_DEPOSIT),
  API("flow.traceability.deposit.mode.api", FlowTreatmentConstants.API_DEPOSIT),
  BATCH("flow.traceability.deposit.mode.batch", FlowTreatmentConstants.BATCH_DEPOSIT);
  private static final Map<String, DepositMode> BY_VALUE = new HashMap<>();
  private static final Map<String, DepositMode> BY_KEY = new HashMap<>();

  static {
    for (DepositMode d : values()) {
      BY_VALUE.put(d.value.toLowerCase(Locale.ROOT), d);
      BY_KEY.put(d.key.toLowerCase(Locale.ROOT), d);
    }
  }

  private final String key;
  private final String value;

  DepositMode(String key, String value) {
    this.key = key;
    this.value = value;
  }

  /**
   * get {@link DepositMode} by value.
   *
   * @param label refer to value of depositMode.
   */
  public static DepositMode valueOfLabel(String label) {
    return BY_VALUE.get(label.toLowerCase(Locale.ROOT));
  }

  /**
   * get {@link DepositMode} by key.
   *
   * @param key refer to key of depositMode.
   */
  public static DepositMode valueOfKey(String key) {
    return BY_KEY.get(key.toLowerCase(Locale.ROOT));
  }

  /**
   * get key and value of deposit Mode.
   *
   * @return {@link List<Map<String, String>>}
   */
  public static List<Map<String, String>> getKeyValue() {
    List<Map<String, String>> keyValue = new ArrayList<>();
    Arrays.stream(DepositMode.values())
        .forEach(
            v ->
                keyValue.add(
                    Map.of(
                        FlowTraceabilityConstant.OBJECT_KEY,
                        v.getKey(),
                        FlowTraceabilityConstant.OBJECT_VALUE,
                        v.getValue())));
    return keyValue;
  }

  public String getValue() {
    return value;
  }

  public String getKey() {
    return key;
  }

  @JsonValue
  public List<String> getValues() {
    return Arrays.stream(values()).map(v -> v.value).collect(Collectors.toUnmodifiableList());
  }
}
