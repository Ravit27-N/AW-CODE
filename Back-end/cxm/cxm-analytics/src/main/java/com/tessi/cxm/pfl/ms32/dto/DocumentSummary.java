package com.tessi.cxm.pfl.ms32.dto;

import java.util.LinkedHashMap;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentSummary extends LinkedHashMap<String, Long> {

  private long totalDocument = 0;

  public void countTotal(long documentCount) {
    totalDocument += documentCount;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    DocumentSummary that = (DocumentSummary) o;
    return totalDocument == that.totalDocument;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), totalDocument);
  }
}
