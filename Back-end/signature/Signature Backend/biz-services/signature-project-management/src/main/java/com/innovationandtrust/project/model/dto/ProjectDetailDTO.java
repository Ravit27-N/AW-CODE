package com.innovationandtrust.project.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ProjectDetailDTO used for create, and response back the project detail to client when they
 * request to see the project data.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDetailDTO implements Serializable {
  private Long id;
  private String type;
  private String titleInvitation;
  private String messageInvitation;

  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private Long projectId;
}
