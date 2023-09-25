package com.allweb.rms.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AdvanceReportResponse {

  private int candidateId;

  private String interviewPosition;

  private String interviewStatus;
}
