package com.innovationandtrust.share.model.corporateprofile;

import java.io.Serializable;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserAccessDTO implements Serializable {
  private Long id;
  private String name;
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof UserAccessDTO userAccessDTO)) return false;
    return Objects.equals(getId(), userAccessDTO.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId());
  }
}
