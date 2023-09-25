package com.tessi.cxm.pfl.ms5.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Builder
@Getter
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BatchUserResponseDto {
  private long total;
  private long successCount;
  private long errorCount;
}
