package com.tessi.cxm.pfl.ms5.constant;

import lombok.AllArgsConstructor;

/**
 * To define privileges level.
 *
 * @author Sokhour LACH
 */
@AllArgsConstructor
public enum PrivilegeLevelValidator {
  VISIBILITY("visibility"),
  MODIFICATION("modification"),
  OWNER("owner");

  private final String value;

  public String getValue() {
    return value;
  }
}
