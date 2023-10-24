package com.innovationandtrust.project.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.innovationandtrust.share.model.user.User;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** ProjectDTO used for response back the project data to client. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProjectDTO implements Serializable {
  @JsonProperty(access = Access.READ_ONLY)
  protected Long createdBy;

  private Long id;

  @NotEmpty(message = "name cannot be empty.")
  private String name;

  private List<DocumentDTO> documents;
  private String step;

  private String signatureLevel;

  private boolean orderSign = true;
  private boolean orderApprove = true;

  private Long templateId;

  @JsonProperty(access = Access.READ_ONLY)
  private String templateName;

  private boolean autoReminder = false;
  private Integer channelReminder;
  private Integer reminderOption;
  private Long assignedTo;

  @JsonProperty(access = Access.READ_ONLY)
  private String flowId;

  @JsonProperty(access = Access.READ_ONLY)
  private String status;

  @JsonProperty(access = Access.READ_ONLY)
  private Long modifiedBy;

  @JsonProperty(access = Access.READ_ONLY)
  private Date createdAt;

  @JsonProperty(access = Access.READ_ONLY)
  private Date modifiedAt;

  private Date expireDate;

  @NotNull private List<SignatoryDto> signatories;
  @NotNull private List<ProjectDetailDTO> details;
  private List<ProjectHistoryDTO> histories;

  @JsonProperty(access = Access.READ_ONLY)
  private User createdByUser;

  public ProjectDTO(Long id, String name) {
    this.id = id;
    this.name = name;
  }

  @JsonProperty(access = Access.READ_ONLY)
  private String manifestName;
}
