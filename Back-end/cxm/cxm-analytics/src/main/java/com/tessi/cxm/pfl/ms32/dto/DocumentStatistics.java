package com.tessi.cxm.pfl.ms32.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DocumentStatistics {
  private String filler;
  private int fillerGroupingLevel;
  private long volumeReceived;
  private long processed;
  private long inProgress;
  private long pndMnd;
  private double pndMndPercentage;
  private double processedPercentage;
  private List<DocumentStatistics> data = new ArrayList<>();
}
