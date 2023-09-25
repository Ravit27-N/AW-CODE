package com.tessi.cxm.pfl.ms3.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tessi.cxm.pfl.shared.utils.FlowTraceabilityConstant;
import com.tessi.cxm.pfl.shared.utils.FlowTraceabilityStatus;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FlowCampaignDto implements Serializable {

  private long id;
  private Date createdAt;
  private String createdBy;
  private String fullName;
  private Date dateStatus;
  private String service;
  private String division;
  @JsonIgnore private String status;
  private FlowCampaignDetailDto detail;
  private long ownerId;

  /**
   * To return value of status and status label.
   *
   * @return return {@link Map} of {@link String} as key and {@link String} as value
   */
  public Map<String, String> getFlowStatus() {
    var flowStatus = FlowTraceabilityStatus.valueOfLabel(this.status);
    return Map.of(
        FlowTraceabilityConstant.STATUS_VALUE,
        this.status,
        FlowTraceabilityConstant.STATUS_LABEL,
        flowStatus != null ? flowStatus.getKey() : this.status);
  }

  public FlowCampaignDetailDto getDetail() {
    if (this.detail == null) {
      return new FlowCampaignDetailDto();
    }
    return this.detail;
  }
}
