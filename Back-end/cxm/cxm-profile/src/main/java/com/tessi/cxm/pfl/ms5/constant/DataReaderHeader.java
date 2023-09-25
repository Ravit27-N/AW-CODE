package com.tessi.cxm.pfl.ms5.constant;

import java.util.Arrays;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum DataReaderHeader {
  CLIENT(DataReaderHeaderConstant.CLIENT, 0),
  PRENOM(DataReaderHeaderConstant.FIRST_NAME, 1),
  NOM(DataReaderHeaderConstant.LAST_NAME, 2),
  EMAIL(DataReaderHeaderConstant.EMAIL, 3),
  DIVISION(DataReaderHeaderConstant.DIVISION, 4),
  SERVICE(DataReaderHeaderConstant.SERVICE, 5),
  PROFIL(DataReaderHeaderConstant.PROFILE, 6);

  private final String key;
  private final int order;

  public static final String HEADER_DEFINED = Arrays.stream(values())
      .map(DataReaderHeader::getKey)
      .collect(Collectors.joining(";"));

  public String getKey() {
    return key;
  }

  public int getOrder() {
    return order;
  }
}
