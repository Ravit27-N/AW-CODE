package com.tessi.cxm.pfl.ms3.util;

import com.fasterxml.jackson.annotation.JsonValue;
import com.tessi.cxm.pfl.ms3.constant.ElementAssociationConstant;
import com.tessi.cxm.pfl.shared.utils.FlowTraceabilityConstant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Enumeration of element association types.
 *
 * @author Pisey CHORN.
 * @since 05-05-2022.
 */
public enum ElementAssociation {
  ACCUSE_RECEPTION(
      "flow.document.element-association.accuse-reception",
      ElementAssociationConstant.ACCUSE_RECEPTION),
  SLIP_SHEET(
      "flow.document.element-association.slip-sheet", ElementAssociationConstant.SLIP_SHEET);

  private static final Map<String, ElementAssociation> BY_KEY = new HashMap<>();
  private static final Map<String, ElementAssociation> BY_VALUE = new HashMap<>();

  static {
    for (ElementAssociation e : values()) {
      BY_KEY.put(e.key.toLowerCase(Locale.ROOT), e);
      for (String v : e.value.split(",")) {
        BY_VALUE.put(v.toLowerCase(Locale.ROOT), e);
      }
    }
  }

  private final String key;
  private final String value;

  ElementAssociation(String key, String value) {
    this.key = key;
    this.value = value;
  }

  public static ElementAssociation valueOfKey(String key) {
    return BY_KEY.get(key.toLowerCase(Locale.ROOT));
  }

  public static ElementAssociation valueOfLabel(String label) {
    return BY_VALUE.get(label.toLowerCase(Locale.ROOT));
  }

  public static List<Map<String, String>> getKeyValue() {
    List<Map<String, String>> keyValue = new ArrayList<>();
    Arrays.stream(ElementAssociation.values())
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

  public String getKey() {
    return key;
  }

  @JsonValue
  public String getValue() {
    return value;
  }
}
