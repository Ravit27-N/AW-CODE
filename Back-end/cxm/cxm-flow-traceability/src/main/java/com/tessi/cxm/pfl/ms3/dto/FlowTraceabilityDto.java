package com.tessi.cxm.pfl.ms3.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.tessi.cxm.pfl.ms3.util.Channel;
import com.tessi.cxm.pfl.ms3.util.DepositMode;
import com.tessi.cxm.pfl.shared.utils.FlowTraceabilityConstant;
import com.tessi.cxm.pfl.shared.utils.FlowTraceabilityStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class FlowTraceabilityDto implements Serializable {

  @Schema(type = "int", format = "int64", example = "0")
  private Long id;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  @Schema(type = "string", example = "2021-10-26")
  private Date depositDate;

  @JsonInclude(Include.NON_NULL)
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Date unloadingDate;

  @Size(max = 128)
  @NotEmpty(message = "depositMode is required and cannot be empty")
  @Schema(
      type = "array",
      enumAsRef = true,
      required = true,
      implementation = DepositMode.class,
      example = "Portal")
  private String depositMode;

  @Size(max = 128)
  @NotEmpty(message = "flowName is required and cannot be empty")
  @Schema(type = "string", example = "letree.pdf")
  private String flowName;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  @Schema(type = "string", example = "User1")
  private String createdBy;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Date createdAt;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  @Schema(type = "string", example = "Pretracc.")
  private String service;

  @Size(max = 128)
  @NotEmpty(message = "channel is required and cannot be empty")
  @Schema(
      type = "array",
      enumAsRef = true,
      required = true,
      implementation = Channel.class,
      example = "Digital")
  private String channel;

  @Size(max = 128)
  @NotEmpty(message = "subChannel is required and cannot be empty")
  @Schema(type = "string", example = "Reco")
  private String subChannel;

  private String fileId;

  private long ownerId;

  @JsonProperty(access = Access.READ_ONLY)
  private int step;

  @JsonProperty(access = Access.READ_ONLY)
  private String composedId;

  @JsonProperty(access = Access.READ_ONLY)
  @Schema(type = "string", example = "delivery")
  private String division;

  @Schema(
      type = "array",
      enumAsRef = true,
      required = true,
      implementation = FlowTraceabilityStatus.class,
      example = "In Creation")
  private String status;

  @NotEmpty(message = "server is required and cannot be empty")
  @Schema(type = "string", example = "tessi")
  @JsonProperty(access = Access.WRITE_ONLY)
  private String server;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Date dateStatus;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private String depositType;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  @Schema(
      type = "object",
      example =
          "{\"statusLabel\":\"flow.traceability.status.in_creation\",\"status\":\"In Creation\"}")
  private Map<String, String> flowStatus;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  @Schema(
      type = "object",
      example = "{\"statusLabel\":\"flow.traceability.channel.digital\",\"status\":\"Digital\"}")
  private Map<String, String> flowChannel;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  @Schema(
      type = "object",
      example = "{\"statusLabel\":\"flow.traceability.sub-channel.multiple\",\"status\":\"Reco\"}")
  private Map<String, String> flowSubChannel;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  @Schema(
      type = "object",
      example = "{\"statusLabel\":\"flow.traceability.deposit.mode.batch\",\"status\":\"batch\"}")
  private Map<String, String> flowDepositMode;

  @Schema(type = "string", example = "John Doe")
  private String fullName;

  @JsonProperty(value = "histories", access = Access.READ_ONLY)
  private Set<FlowHistoryDto> flowHistories;

  private String modelName;

  public Set<FlowHistoryDto> getFlowHistories() {
    if (this.flowHistories == null) {
      return Set.of();
    }

    return this.flowHistories.stream()
        .sorted(Comparator.comparing(x -> Integer.parseInt(x.getHistoryStatus().get(
            FlowTraceabilityConstant.STATUS_ORDER))))
        .collect(Collectors.toCollection(LinkedHashSet::new));
  }
}
