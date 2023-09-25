package com.allweb.rms.entity.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class InterviewElasticsearchRequest extends BaseSearchRequest {
  private String title;
  private String[] status;
}
