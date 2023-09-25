package com.tessi.cxm.pfl.ms32.constant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DepositModeResponseDto {
  private String key;
  private double value;
}
