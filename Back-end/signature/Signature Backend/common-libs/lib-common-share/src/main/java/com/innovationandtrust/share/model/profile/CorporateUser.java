package com.innovationandtrust.share.model.profile;

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
public class CorporateUser implements Serializable {
  private long id;
  private String firstName;
  private String lastName;
  private String email;

  private String userEntityId;

  private Long createdBy;
  private long companyId;

  private Long userId;
  private NormalUser normalUser;

  public CorporateUser(String userEntityId) {
    this.userEntityId = userEntityId;
    this.normalUser = new NormalUser();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof CorporateUser that)) return false;
    return Objects.equals(getUserEntityId(), that.getUserEntityId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getUserEntityId());
  }
}
