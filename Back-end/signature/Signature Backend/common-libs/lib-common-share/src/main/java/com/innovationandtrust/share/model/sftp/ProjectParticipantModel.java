package com.innovationandtrust.share.model.sftp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.innovationandtrust.share.constant.ParticipantRole;
import com.innovationandtrust.share.constant.RoleConstant;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProjectParticipantModel {
  @NotEmpty private String firstName;

  @NotEmpty private String lastName;

  @NotEmpty private String role;

  @NotEmpty private String email;
  private String phone;

  @Min(1)
  private Integer sortOrder = 1;

  @JsonIgnore private String comment;

  public boolean isActor() {
    return Objects.equals(this.role, RoleConstant.ROLE_APPROVAL)
        || Objects.equals(this.role, RoleConstant.ROLE_SIGNATORY);
  }

  public void setRole(String role) {
    if (!ParticipantRole.isValidRole(role)) {
      throw new IllegalArgumentException("Invalid role");
    }
    this.role = role;
  }
}
