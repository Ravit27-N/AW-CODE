package com.tessi.cxm.pfl.ms32.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FlowDocumentProductionDetail {

  private String channel;
  private long volumeReceived;
  private long pndMnd;
  private double pndMndPercentage;
  private long treaty;
  private long inProgress;
  private double treatyPercentage;
}
