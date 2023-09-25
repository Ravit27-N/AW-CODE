package com.tessi.cxm.pfl.ms3.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tessi.cxm.pfl.shared.utils.FlowTraceabilityConstant;
import com.tessi.cxm.pfl.shared.utils.FlowTraceabilityStatus;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minidev.json.annotate.JsonIgnore;
import org.springframework.util.StringUtils;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FlowDepositDto implements Serializable {

  private long id;
  private String fileId;
  private String composedFileId;
  private int step;
  private String flowName;
  private Date dateStatus;
  private String fullName;
  private boolean validated;
  private long ownerId;
  @JsonIgnore
  private String status;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private String createdBy;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Date createdAt;
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Date lastModified;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Map<String, String> flowStatus;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Map<String, String> flowChannel;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Map<String, String> flowSubChannel;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Map<String, String> flowDepositMode;

  public Map<String, String> getFlowStatus() {
    if (StringUtils.hasText(this.status)) {
      var data = FlowTraceabilityStatus.valueOfLabel(this.status);
      return Map.of(FlowTraceabilityConstant.STATUS_LABEL, data.getKey(),
          FlowTraceabilityConstant.STATUS, data.getValue());
    }
    return Map.of();
  }
}
