package com.tessi.cxm.pfl.ms3.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** @author Sokhour LACH */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class FlowFilterCriteriaDto {
  @Schema(
      type = "object",
      example = "[{\"statusLabel\":\"flow.traceability.channel.digital\",\"status\":\"Digital\"}]")
  private List<Map<String, String>> channel = new ArrayList<>();

  @Schema(
      type = "object",
      example =
          "[{\"statusLabel\":\"flow.traceability.sub-channel.multiple\",\"status\":\"Reco\"}]")
  private List<Map<String, String>> subChannel = new ArrayList<>();

  @Schema(
      type = "object",
      example = "[{\"statusLabel\":\"flow.traceability.deposit.mode.batch\",\"status\":\"batch\"}]")
  private List<Map<String, String>> depositMode = new ArrayList<>();

  @Schema(
      type = "object",
      example =
          "[{\"statusLabel\":\"flow.traceability.status.in_creation\",\"status\":\"In Creation\"}]")
  private List<Map<String, String>> flowStatus = new ArrayList<>();
}
