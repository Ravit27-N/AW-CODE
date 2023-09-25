package com.tessi.cxm.pfl.ms3.dto;

import com.tessi.cxm.pfl.ms3.entity.FlowTraceability;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CountDocumentOfFlowImpl implements CountDocumentOfFlowProjection {

  private FlowTraceability flowTraceability;

  private long totalDocs;


  @Override
  public FlowTraceability getFlowTraceability() {
    return this.flowTraceability;
  }

  @Override
  public long getTotalDocs() {
    return this.totalDocs;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof CountDocumentOfFlowImpl)) {
      return false;
    }
    CountDocumentOfFlowImpl that = (CountDocumentOfFlowImpl) o;
    return getTotalDocs() == that.getTotalDocs() && getFlowTraceability().getId().equals(
        that.getFlowTraceability().getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getFlowTraceability().getId(), getTotalDocs());
  }
}
