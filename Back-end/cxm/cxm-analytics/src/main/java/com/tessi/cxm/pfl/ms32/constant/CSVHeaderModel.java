package com.tessi.cxm.pfl.ms32.constant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class CSVHeaderModel {
  private String value;
  private int order;
}
