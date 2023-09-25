package com.tessi.cxm.pfl.ms32.dto;

import java.io.Serializable;
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
public class FlowDocumentReportData implements Serializable {
  private String subChannel;
  private long volume;
  private long toValidate;
  private long scheduled;
  private long inProgress;
  private long other;
  private double completedPercentage;
  private long terminated;
}
