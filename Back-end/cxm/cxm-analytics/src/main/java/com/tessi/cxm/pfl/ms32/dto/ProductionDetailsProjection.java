package com.tessi.cxm.pfl.ms32.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductionDetailsProjection implements ProductionDetails {
  private String channel;
  private String subChannel;
  private String status;
  private Long total;
}
