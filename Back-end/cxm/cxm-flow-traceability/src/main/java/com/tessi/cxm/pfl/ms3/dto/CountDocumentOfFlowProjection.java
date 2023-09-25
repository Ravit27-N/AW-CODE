package com.tessi.cxm.pfl.ms3.dto;

import com.tessi.cxm.pfl.ms3.entity.FlowTraceability;
import java.io.Serializable;

public interface CountDocumentOfFlowProjection extends Serializable {

  FlowTraceability getFlowTraceability();

  long getTotalDocs();
}
