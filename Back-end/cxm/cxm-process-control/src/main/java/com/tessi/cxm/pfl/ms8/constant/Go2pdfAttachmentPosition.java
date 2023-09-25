package com.tessi.cxm.pfl.ms8.constant;

import com.tessi.cxm.pfl.shared.utils.AttachmentPositionConstant;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Go2pdfAttachmentPosition {
  FIRST_POSITION(
      AttachmentPositionConstant.FIRST_POSITION,
      "First",
      "For first position"),
  SECOND_POSITION(
      AttachmentPositionConstant.SECOND_POSITION,
      "Second",
      "For second position"),
  THIRD_POSITION(
      AttachmentPositionConstant.THIRD_POSITION,
      "Third",
      "For third position"),
  FOURTH_POSITION(
      AttachmentPositionConstant.FOURTH_POSITION,
      "Fourth",
      "For fourth position"),
  FIFTH_POSITION(
      AttachmentPositionConstant.FIFTH_POSITION,
      "Fifth",
      "For fifth position");

  private static final Map<String, Go2pdfAttachmentPosition> BY_VALUE = new HashMap<>();
  private static final Map<String, Go2pdfAttachmentPosition> BY_KEY = new HashMap<>();

  private final String key;
  private final String value;
  private final String description;

  static {
    for (Go2pdfAttachmentPosition attachmentPosition : values()) {
      BY_VALUE.put(attachmentPosition.value.toLowerCase(Locale.ROOT), attachmentPosition);
      BY_KEY.put(attachmentPosition.key.toLowerCase(Locale.ROOT), attachmentPosition);
    }
  }

  public static Go2pdfAttachmentPosition getValueByKey(String key) {
    return BY_KEY.get(key.toLowerCase(Locale.ROOT));
  }

  public static Go2pdfAttachmentPosition getKeyByValue(String value) {
    return BY_VALUE.get(value.toLowerCase(Locale.ROOT));
  }
}
