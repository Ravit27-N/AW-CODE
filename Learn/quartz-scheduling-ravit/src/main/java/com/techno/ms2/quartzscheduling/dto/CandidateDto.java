package com.techno.ms2.quartzscheduling.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CandidateDto {

  private Long id;

  private String name;

  private int age;

  private boolean activeStatus;
}
