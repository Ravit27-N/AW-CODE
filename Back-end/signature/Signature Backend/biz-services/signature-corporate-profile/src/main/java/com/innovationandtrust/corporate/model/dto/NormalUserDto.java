package com.innovationandtrust.corporate.model.dto;

import com.innovationandtrust.corporate.model.entity.AbstractUser;
import jakarta.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NormalUserDto extends AbstractUser implements Serializable {
  private Set<String> roles = new HashSet<>();
  @NotEmpty private Long companyId;
  @NotEmpty private Long businessId;
  @NotEmpty private Long userId;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof NormalUserDto that)) {
      return false;
    }
    return this.getId() == that.getId();
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId());
  }
}
