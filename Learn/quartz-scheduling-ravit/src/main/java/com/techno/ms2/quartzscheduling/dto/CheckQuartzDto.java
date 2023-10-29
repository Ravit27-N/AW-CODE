package com.techno.ms2.quartzscheduling.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class CheckQuartzDto {
  private String checkName;
  private LocalDateTime time;
  private String url;
  private String frequency;
  private String unit;
}
