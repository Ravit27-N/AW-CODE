package com.tessi.cxm.pfl.ms5.constant;

import com.tessi.cxm.pfl.shared.exception.BadRequestException;
import com.tessi.cxm.pfl.shared.exception.ResourceTypeNotFoundException;
import com.tessi.cxm.pfl.shared.utils.ResourceType;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum AddressType {
  SERVICE("service"),
  DIVISION("division"),
  CLIENT("client"),
  USER("user");
  private final String value;

  public static AddressType resourceContain(String name) {
    return Arrays.stream(values())
        .filter(v -> v.getValue().equalsIgnoreCase(name))
        .findFirst()
        .orElseThrow(() -> new BadRequestException("Address type not found"));
  }
}
