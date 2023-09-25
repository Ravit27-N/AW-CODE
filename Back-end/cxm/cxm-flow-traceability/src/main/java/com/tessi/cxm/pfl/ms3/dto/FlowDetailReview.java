package com.tessi.cxm.pfl.ms3.dto;

import org.springframework.beans.factory.annotation.Value;

public interface FlowDetailReview {
  Long getId();

  int getPageError();

  long getVersion();

  @Value("#{target.flowTraceability.id}")
  long getFlowTraceabilityId();
}
