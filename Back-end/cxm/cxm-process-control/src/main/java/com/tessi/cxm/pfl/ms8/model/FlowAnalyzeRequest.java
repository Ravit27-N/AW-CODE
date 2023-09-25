package com.tessi.cxm.pfl.ms8.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@AllArgsConstructor
public class FlowAnalyzeRequest {
  private String uuid;
  private String modelName;
  private String flowType;
  private String fileId;
  private String idCreator;
  private String channel;
  private String subChannel;
}
