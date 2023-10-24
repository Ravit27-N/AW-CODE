package com.innovationandtrust.utils.signatureidentityverification.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

/** Enum for document type. */
@Getter
public enum DocumentType {
  IDENTITY_CARD("id_card"),
  PASSPORT("passport"),
  DRIVER_PERMIT("driver_permit"),
  RESIDENCE_PERMIT("residency_permit");

  private final String value;

  DocumentType(String s) {
    this.value = s;
  }

  /**
   * Get enum from string.
   *
   * @param value string value
   * @return DocumentType
   */
  @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
  public static DocumentType fromString(String value) {
    for (DocumentType type : DocumentType.values()) {
      if (type.value.equalsIgnoreCase(value)) {
        return type;
      }
    }
    throw new IllegalArgumentException("No constant with value " + value + " found");
  }

  @Override
  public String toString() {
    return this.value;
  }
}
