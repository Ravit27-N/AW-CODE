package com.tessi.cxm.pfl.ms3.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApprovalRequest {
  private List<String> fileIds;

  @Schema(type = "String", required = true, example = "validate")
  private String status;
}
