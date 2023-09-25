package com.tessi.cxm.pfl.ms3.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class ListFlowTraceabilityDto implements Serializable {

  private Long id;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Date depositDate;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Date createdAt;

  private String depositMode;

  private String flowName;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private String createdBy;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private String service;

  private String channel;

  private String subChannel;

  private String fileId;

  @JsonProperty(access = Access.READ_ONLY)
  private int step;

  @JsonProperty(access = Access.READ_ONLY)
  private String composedId;

  private String status;

  private String server;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Date dateStatus;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private String depositType;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Map<String, String> flowStatus;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Map<String, String> flowChannel;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Map<String, String> flowSubChannel;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Map<String, String> flowDepositMode;

  @Schema(type = "string", example = "John Doe")
  private String fullName;

  private String campaignName;

  private String campaignFilename;

  private long ownerId;

  public String getCampaignName() {
    if (this.campaignName == null) {
      return "";
    }

    return this.campaignName;
  }

  public String campaignFilename() {
    if (this.campaignFilename != null) {
      return this.campaignFilename;
    }

    return "";
  }

  public String getComposedId() {
    if (this.composedId == null) {
      return "";
    }
    return composedId;
  }
}
