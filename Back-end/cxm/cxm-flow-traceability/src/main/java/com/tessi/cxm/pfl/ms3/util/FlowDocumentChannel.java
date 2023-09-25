package com.tessi.cxm.pfl.ms3.util;

import com.fasterxml.jackson.annotation.JsonValue;
import com.tessi.cxm.pfl.ms3.entity.FlowDocument;
import com.tessi.cxm.pfl.shared.utils.FlowDocumentChannelConstant;
import com.tessi.cxm.pfl.shared.utils.FlowDocumentSubChannel;
import com.tessi.cxm.pfl.shared.utils.FlowTraceabilityConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * The enumeration properties of document's channel.
 *
 * @author Sokhour LACH
 * @see FlowDocument#getChannel()
 */
@Schema(enumAsRef = true)
public enum FlowDocumentChannel {
  POSTAL("flow.document.channel.postal", FlowDocumentChannelConstant.POSTAL,
      FlowDocumentSubChannel.postalSubChannels()),
  DIGITAL("flow.document.channel.digital", FlowDocumentChannelConstant.DIGITAL,
      FlowDocumentSubChannel.digitalSubChannels()),
  MULTIPLE("flow.document.channel.multiple", FlowDocumentChannelConstant.MULTIPLE,
      FlowDocumentSubChannel.multipleSubChannels());

  private static final Map<String, FlowDocumentChannel> BY_VALUE = new HashMap<>();

  static {
    for (var v : values()) {
      BY_VALUE.put(v.value.toLowerCase(Locale.ROOT), v);
    }
  }

  private final String key;
  private final String value;
  private final List<String> sendingSubChannel;

  FlowDocumentChannel(String key, String value, List<String> sendingSubChannel) {
    this.key = key;
    this.value = value;
    this.sendingSubChannel = sendingSubChannel;
  }

  /**
   * get value of sending channel by value.
   *
   * @param label refer to value of sending channel.
   * @return {@link FlowDocumentChannel}
   */
  public static FlowDocumentChannel valueOfLabel(String label) {
    return BY_VALUE.get(label.toLowerCase(Locale.ROOT));
  }

  /**
   * To Get key and value of sending-channel.
   *
   * @return return list of {@link Map} with {@link String} keys and {@link String} values.
   */
  public static List<Map<String, Object>> getKeyValue() {
    List<Map<String, Object>> keyValue = new ArrayList<>();
    Arrays.stream(FlowDocumentChannel.values()).forEach(v ->
        keyValue.add(
            Map.of(FlowTraceabilityConstant.OBJECT_KEY, v.getKey(),
                FlowTraceabilityConstant.OBJECT_VALUE, v.getValue(),
                FlowTraceabilityConstant.SENDING_SUB_CHANNEL,
                v.getSendingSubChannel())));
    return keyValue;
  }

  public String getKey() {
    return key;
  }

  @JsonValue
  public String getValue() {
    return value;
  }

  public List<String> getSendingSubChannel() {
    return sendingSubChannel;
  }

  /**
   * Matches the label of sending sub-channel.
   *
   * @param label refer to value of the sending sub-channel.
   * @return {@link Boolean}
   */
  public boolean isMatchSubChannel(String label) {
    return sendingSubChannel.stream().anyMatch(label::equalsIgnoreCase);
  }

  /**
   * get value of sending sub-channel by value.
   *
   * @param label refer to value of the sending sub-channel
   * @return {@link String}
   */
  public String valueOfSubChannel(String label) {
    return sendingSubChannel.stream().filter(label::equalsIgnoreCase).findFirst().orElse(null);
  }
}
