package com.innovationandtrust.project.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.innovationandtrust.share.constant.ParticipantRole;
import com.innovationandtrust.share.model.profile.CompanySettingDto;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** ProjectRequest is the class that used for created a new project. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProjectRequest implements Serializable {

  private Long id;

  @NotEmpty(message = "name cannot be empty.")
  private String name;

  private List<DocumentRequest> documents;

  private CompanySettingDto setting;

  @NotEmpty(message = "signatureLevel cannot be empty.")
  private String signatureLevel;

  private String step;

  private boolean orderSign;
  private boolean orderApprove;

  private Long templateId;

  private boolean autoReminder;
  private Integer channelReminder;
  private Integer reminderOption;

  private Date expireDate;

  @JsonProperty(access = Access.READ_ONLY)
  private String status;

  @JsonProperty(access = Access.READ_ONLY)
  private Long createdBy;

  @JsonProperty(access = Access.READ_ONLY)
  private Long modifiedBy;

  @JsonProperty(access = Access.READ_ONLY)
  private Date createdAt;

  @JsonProperty(access = Access.READ_ONLY)
  private Date modifiedAt;

  @NotNull private List<SignatoryDto> signatories;
  @NotNull private List<ProjectDetailDTO> details;
  private List<DocumentDetailRequest> documentDetails;

  public ProjectRequest(
      Long id, String name, List<SignatoryDto> signatories, List<ProjectDetailDTO> details) {
    this.id = id;
    this.name = name;
    this.signatories = signatories;
    this.details = details;
  }

  public Date getExpiresDate() {
    return this.expireDate;
  }

  @JsonIgnore
  public List<SignatoryDto> getSignatoryByRole(String role) {
    if (this.getSignatories().isEmpty()) {
      return List.of();
    }
    return signatories.stream()
        .filter(s -> ParticipantRole.getByRole(role).equals(ParticipantRole.getByRole(s.getRole())))
        .toList();
  }
}
