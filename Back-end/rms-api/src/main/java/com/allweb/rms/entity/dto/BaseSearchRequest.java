package com.allweb.rms.entity.dto;

import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.Pageable;

import java.util.Date;

@Data
@SuperBuilder
public abstract class BaseSearchRequest {
  private String filter;
  private Date startDate;
  private Date endDate;
  private Pageable pageable;
}
