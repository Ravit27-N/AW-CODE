package com.allweb.rms.entity.jpa;

import java.io.Serializable;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleDetailId implements Serializable {

  private static final long serialVersionUID = 1L;

  private String userRoleId;
  private int moduleId;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    UserRoleDetailId accountId = (UserRoleDetailId) o;
    return userRoleId.equals(accountId.userRoleId) && moduleId == accountId.moduleId;
  }

  @Override
  public int hashCode() {
    return Objects.hash(userRoleId, moduleId);
  }
}
