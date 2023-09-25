package com.tessi.cxm.pfl.ms32.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;
import java.util.Objects;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductionDetailsDTO extends LinkedHashMap<String, Long> {

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
    ProductionDetailsDTO that = (ProductionDetailsDTO) o;
    return totalDocument == that.totalDocument;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), totalDocument);
  }
    }