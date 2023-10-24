package com.innovationandtrust.profile.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** For public resource. */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityRes {
  private boolean active;
  private String resetToken;

  @Override
  public String toString() {
    return "ActivityRes{" +
            "active=" + active +
            ", resetToken='" + resetToken + '\'' +
            '}';
  }
}
