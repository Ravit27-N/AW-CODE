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

/** Enumeration of cxm-flow-traceability channel. */
@Schema(enumAsRef = true)
public enum Channel {
  NONE("flow.traceability.channel.none", "", SubChannel.NONE.getValue()),
  POSTAL("flow.traceability.channel.postal", "Postal", SubChannel.POSTAL.getValue()),
  DIGITAL("flow.traceability.channel.digital", "Digital", SubChannel.DIGITAL.getValue()),
  MULTIPLE("flow.traceability.channel.multiple", "Multiple", SubChannel.MULTIPLE.getValue());
  private static final Map<String, Channel> BY_VALUE = new HashMap<>();
  private static final Map<String, Channel> BY_KEY = new HashMap<>();
  private static final Map<String, Channel> BY_SUB_CHANNEL = new HashMap<>();

  static {
    for (Channel c : values()) {
      BY_KEY.put(c.key.toLowerCase(Locale.ROOT), c);
      BY_VALUE.put(c.value.toLowerCase(Locale.ROOT), c);

      for (String s : c.subChannel.split(",")) {
        BY_SUB_CHANNEL.put(s.toLowerCase(Locale.ROOT), c);
      }
    }
  }

  private final String key;
  private final String value;
  private final String subChannel;

  Channel(String key, String value, String subChannel) {
    this.key = key;
    this.value = value;
    this.subChannel = subChannel;
  }

  /**
   * get {@link Channel} by key.
   *
   * @param key refer to key of channel.
   */
  public static Channel valueOfKey(String key) {
    return BY_KEY.get(key.toLowerCase(Locale.ROOT));
  }

  /**
   * get {@link Channel} by value.
   *
   * @param label refer to value of channel.
   */
  public static Channel valueOfLabel(String label) {
    return BY_VALUE.get(label.toLowerCase(Locale.ROOT));
  }

  /**
   * get {@link Channel} by sub-channel value.
   *
   * @param subChannelValue refer to value of sub-channel.
   */
  public static Channel valueOfSubChannel(String subChannelValue) {
    return BY_SUB_CHANNEL.get(subChannelValue.toLowerCase(Locale.ROOT));
  }

  /**
   * get key and value of channel.
   *
   * @return {@link List<Map<String, String>>}
   */
  public static List<Map<String, String>> getKeyValue() {
    List<Map<String, String>> keyValue = new ArrayList<>();
    Arrays.stream(Channel.values())
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

  @JsonValue
  public String getValue() {
    return value;
  }

  public String getSubChannel() {
    return subChannel;
  }

  public String getKey() {
    return key;
  }
}
