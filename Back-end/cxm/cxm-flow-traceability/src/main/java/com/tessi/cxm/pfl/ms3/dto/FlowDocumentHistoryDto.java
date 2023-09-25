package com.tessi.cxm.pfl.ms3.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.tessi.cxm.pfl.shared.model.User;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.StringUtils;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class FlowDocumentHistoryDto implements Serializable {
  private Long id;

  @NotEmpty(message = "event is required and cannot be empty.")
  @Schema(type = "string", example = "In production")
  @JsonProperty(access = Access.WRITE_ONLY)
  private String event;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  @Schema(type = "string", example = "user")
  private String createdBy;

  @NotEmpty(message = "server is required and cannot be empty.")
  @Schema(type = "string", example = "tessi")
  private String server;

  @NotNull(message = "flowDocumentId is required and cannot be null.")
  @Schema(type = "integer", format = "int64", example = "1")
  private long flowDocumentId;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Map<String, String> historyStatus;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Date dateTime;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private String fullName;

  public String getFullName() {
    if (!StringUtils.hasText(this.fullName)) {
      return  "";
    }
    return fullName;
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
    if (!(o instanceof FlowDocumentHistoryDto)) {
      return false;
    }
    FlowDocumentHistoryDto that = (FlowDocumentHistoryDto) o;
    return getFlowDocumentId() == that.getFlowDocumentId() && getId().equals(that.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getFlowDocumentId());
  }
}
