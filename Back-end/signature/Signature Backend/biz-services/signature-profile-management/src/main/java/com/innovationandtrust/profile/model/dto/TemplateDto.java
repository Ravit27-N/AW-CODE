package com.innovationandtrust.profile.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.innovationandtrust.profile.constant.TemplateType;
import com.innovationandtrust.share.constant.NotificationConstant;
import com.innovationandtrust.share.constant.ParticipantRole;
import com.innovationandtrust.share.enums.ScenarioStep;
import com.innovationandtrust.utils.exception.exceptions.InvalidRequestException;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TemplateDto implements Serializable {
  @Serial private static final long serialVersionUID = 1L;
  private Long id;
  private String name;
  private Long folderId;
  private Long businessUnitId;
  private Long companyId;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private boolean favorite = false;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private int usedCount = 0;

  @NotNull(message = "Step cannot be null")
  private Integer step;

  @NotNull(message = "Template type cannot be null")
  private String type;

  private String signProcess;
  private Integer level;
  private Integer format;
  private Integer approval;
  private Integer signature;
  private Integer recipient;
  private Integer viewer;

  // Refers to channel_reminder
  private String notificationService;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private String createdByFullName;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private String folderName;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private String businessUnitName;

  private TemplateMessageDto templateMessage;

  private List<UserParticipantDto> participants = new ArrayList<>();

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Date createdAt;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Long createdBy;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Date modifiedAt;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Long modifiedBy;

  @JsonIgnore
  public List<UserParticipantDto> getParticipantsByRole(String role) {
    if (participants.isEmpty()) {
      return List.of();
    }
    return participants.stream()
        .filter(
            participant ->
                ParticipantRole.getByRole(role)
                    .equals(ParticipantRole.getByRole(participant.getRole())))
        .toList();
  }

  @JsonIgnore
  public Optional<UserParticipantDto> getInvalidParticipant() {
    if (participants.isEmpty()) {
      return Optional.empty();
    }
    return participants.stream()
        .filter(
            participant ->
                Objects.isNull(participant.getRole())
                    || !ParticipantRole.isValidRole(participant.getRole()))
        .findFirst();
  }

  public void setType(String type) {
    if (!TemplateType.types().contains(type)) {
      throw new InvalidRequestException("There is no type " + type);
    }
    this.type = type;
  }

  public void setNotificationService(String notificationService) {
    if (!NotificationConstant.getTypes().contains(notificationService)) {
      throw new InvalidRequestException("There is no notification service " + notificationService);
    }
    this.notificationService = notificationService;
  }

  public void setSignProcess(String signProcess) {
    ScenarioStep.getByValue(signProcess);
    this.signProcess = signProcess;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof TemplateDto that)) return false;
    return Objects.equals(getId(), that.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId());
  }
}
