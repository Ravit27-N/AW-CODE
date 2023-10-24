package com.innovationandtrust.project.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.innovationandtrust.share.model.user.User;

import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProjectSignatory implements Serializable {
  private Long id;
  private String name;
  private String flowId;
  private String status;
  private Date createdAt;
  private Date expireDate;
  private Long createdBy;
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private User createdByUser;
}
