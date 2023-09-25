package com.allweb.rms.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Pageable;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CandidateElasticsearchRequest {
  private String candidateName;
  private String gender;
  private float gpa;
  private String university;
  private String position; // Interview title
  private String candidateStatus;
  private String[] filterBy; // Interview, Reminder
  private String filter;
  private boolean isDeleted;
  private Pageable pageable;
  private Date dateFrom;
  private Date dateTo;
}
