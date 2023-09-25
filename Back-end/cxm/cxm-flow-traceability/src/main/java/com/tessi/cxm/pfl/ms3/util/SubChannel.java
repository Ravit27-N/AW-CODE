package com.tessi.cxm.pfl.ms3.util;

import com.fasterxml.jackson.annotation.JsonValue;
import com.tessi.cxm.pfl.shared.utils.FlowTraceabilityConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Enumeration of cxm-flow-traceability sub-channel.
 *
 * @author Piseth KHON
 * @since 12/10/2021
 */
@Schema(enumAsRef = true)
public enum SubChannel {
  NONE("flow.traceability.sub-channel.none", ""),
  POSTAL("flow.traceability.sub-channel.postal", "Ecopli,Lettre,Reco,Reco AR"),
  DIGITAL("flow.traceability.sub-channel.digital", "CSE,CSE AR,LRE,Email,SMS"),
  MULTIPLE(
      "flow.traceability.sub-channel.multiple",
      POSTAL.getValue().concat(",").concat(DIGITAL.getValue()));

  private static final Map<String, SubChannel> BY_KEY = new HashMap<>();
  private static final Map<String, SubChannel> BY_VALUE = new HashMap<>();

  static {
    for (SubChannel s : values()) {
      BY_KEY.put(s.key.toLowerCase(Locale.ROOT), s);
      for (String v : s.value.split(",")) {
        BY_VALUE.put(v.toLowerCase(Locale.ROOT), s);
      }
    }
  }

  private final String key;
  private final String value;

  private SubChannel(String key, String value) {
    this.key = key;
    this.value = value;
  }

  /**
   * get {@link SubChannel} by key.
   *
   * @param key refer to key of sub-channel.
   */
  public static SubChannel valueOfKey(String key) {
    return BY_KEY.get(key.toLowerCase(Locale.ROOT));
  }

  /**
   * get {@link SubChannel} by value.
   *
   * @param label refer to value of sub-channel.
   */
  public static SubChannel valueOfLabel(String label) {
    return BY_VALUE.get(label.toLowerCase(Locale.ROOT));
  }

  /**
   * To get key and value of sub-channel.
   *
   * @return return list of {@link Map} with {@link String} keys and {@link String} values.
   */
  public static List<Map<String, String>> getKeyValue() {
    List<Map<String, String>> keyValue = new ArrayList<>();
    Arrays.stream(SubChannel.values()).forEach(v ->
        keyValue.add(
          Map.of(FlowTraceabilityConstant.OBJECT_KEY, v.getKey(),
          FlowTraceabilityConstant.OBJECT_VALUE, v.getValue())));
    return keyValue;
  }

  public String getKey() {
    return key;
  }

  @JsonValue
  public String getValue() {
    return value;
  }
}
