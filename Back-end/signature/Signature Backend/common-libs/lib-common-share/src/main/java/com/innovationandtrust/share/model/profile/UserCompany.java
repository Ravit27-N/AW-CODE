package com.innovationandtrust.share.model.profile;

import com.innovationandtrust.share.model.user.User;
import java.io.Serializable;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserCompany extends User implements Serializable {
  private Company company;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Company that)) return false;
    return Objects.equals(getId(), that.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId());
  }
}
