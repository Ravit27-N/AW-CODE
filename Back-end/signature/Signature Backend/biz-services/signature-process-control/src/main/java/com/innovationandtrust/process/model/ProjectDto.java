package com.innovationandtrust.process.model;

import com.innovationandtrust.share.model.user.User;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDto {
  private Long id;
  protected User createdByUser;
  private Long assignedTo;
  private Date createdAt;
}
