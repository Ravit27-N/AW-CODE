package com.innovationandtrust.utils.signatureidentityverification.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Objects;
import lombok.Getter;

/** Enum for document rotation type. */
@Getter
public enum DocumentRotationType {
  NORTH(0),
  SOUTH(90),
  EAST(180),
  WEST(270);

  private final int value;

  DocumentRotationType(int value) {
    this.value = value;
  }

  /**
   * Get enum from integer.
   *
   * @param value integer value.
   * @return DocumentRotationType.
   */
  @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
  public static DocumentRotationType fromInt(int value) {
    for (DocumentRotationType type : DocumentRotationType.values()) {
      if (Objects.equals(type.value, value)) {
        return type;
      }
    }
    throw new IllegalArgumentException("No constant with value " + value + " found");
  }

  @Override
  public String toString() {
    return String.valueOf(this.value);
  }
}
