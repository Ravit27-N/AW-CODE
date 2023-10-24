package com.innovationandtrust.utils.signatureidentityverification.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

/** Enum for document country. */
@Getter
public enum DocumentCountry {
  FRENCH("fr");

  private final String value;

  DocumentCountry(String country) {
    this.value = country;
  }

  /**
   * Get enum from string.
   *
   * @param value string value
   * @return DocumentType
   */
  @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
  public static DocumentCountry fromString(String value) {
    for (DocumentCountry type : DocumentCountry.values()) {
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
