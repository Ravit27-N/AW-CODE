package com.allweb.rms.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Pageable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DemandElasticsearchRequest {
  private String filter;
  private boolean isDeleted;
  private Pageable pageable;
  private String sortDirection;
  private String sortByField;
  private boolean active;
}
