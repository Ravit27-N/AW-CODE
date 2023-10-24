package com.innovationandtrust.project.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ProjectHistoryDTO used for create a project history, and response back to client when they
 * request it.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectHistoryDTO implements Serializable {
  private Long id;
  private Date dateStatus;
  private String action;
  private String actionBy;
  private Integer sortOrder;

  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private Long projectId;
}
