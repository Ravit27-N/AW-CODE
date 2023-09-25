package com.tessi.cxm.pfl.ms5.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryClientActiveServiceResponsesDTO {

  private List<QueryClientActiveServiceResponseDTO> services;

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class QueryClientActiveServiceResponseDTO {

    @Schema(type = "integer", example = "1")
    private long id;

    @Schema(type = "string", example = "Service 1")
    private String name;
  }
}
