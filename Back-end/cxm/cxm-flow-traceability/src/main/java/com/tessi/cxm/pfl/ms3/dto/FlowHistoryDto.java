package com.tessi.cxm.pfl.ms3.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.tessi.cxm.pfl.shared.model.User;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class FlowHistoryDto implements Serializable {
  @Schema(type = "integer", format = "int64", example = "1")
  private Long id;

  @Schema(type = "string", example = "Deposited")
  @JsonProperty(access = Access.WRITE_ONLY)
  private String event;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  @Schema(type = "string", example = "User1")
  private String createdBy;

  @Schema(type = "string", example = "Pretracc.")
  private String server;

  @NotNull(message = "flowTraceabilityId is required and cannot be null.")
  @Schema(type = "int", format = "int64", example = "1")
  private long flowTraceabilityId;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  @Schema(
      type = "object",
      example = "{\"statusLabel\":\"flow.history.status.in_creation\",\"status\":\"Deposited\"}")
  private Map<String, String> historyStatus;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  @Schema(type = "integer", format = "int64", example = "1634786662132")
  private Date dateTime;

  @JsonProperty(access = Access.READ_ONLY)
  private String fullName;

  /**
   * To get value of server.
   *
   * @return value of server
   */
  public String getServer() {
    if (server == null) {
      return "";
    }
    return this.server;
  }

  public String getFullName() {
    if(StringUtils.hasText(this.fullName)) {
      return this.fullName;
    }

    return "";
  }

  public void setFullName(User user) {
    if (user != null) {
      this.fullName = user.getFirstName().concat(" ").concat(user.getLastName());
    } else {
      this.fullName = "";
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof FlowHistoryDto)) {
      return false;
    }
    FlowHistoryDto that = (FlowHistoryDto) o;
    return getFlowTraceabilityId() == that.getFlowTraceabilityId()
        && Objects.equals(getId(), that.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getFlowTraceabilityId());
  }
}
