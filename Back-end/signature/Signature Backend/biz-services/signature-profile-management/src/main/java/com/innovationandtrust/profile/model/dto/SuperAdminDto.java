package com.innovationandtrust.profile.model.dto;

import java.io.Serializable;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class SuperAdminDto extends AbstractUser implements Serializable {
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof SuperAdminDto that)) {
      return false;
    }
    return Objects.equals(getEmail(), that.getEmail());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getEmail());
  }
}
