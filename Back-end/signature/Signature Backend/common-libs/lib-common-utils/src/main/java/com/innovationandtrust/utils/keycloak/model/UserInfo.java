package com.innovationandtrust.utils.keycloak.model;

import java.io.Serializable;
import java.util.Objects;
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
public class UserInfo implements Serializable {

  private Long userId;
  private Company company;
  private boolean firstLogin;
  private String corporateId;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof UserInfo userInfo)) return false;
    return Objects.equals(getUserId(), userInfo.getUserId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getUserId());
  }
}
