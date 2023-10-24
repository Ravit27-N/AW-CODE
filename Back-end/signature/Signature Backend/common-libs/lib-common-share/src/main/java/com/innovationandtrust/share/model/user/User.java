package com.innovationandtrust.share.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {

  private Long id;
  private UUID userEntityId;
  private String firstName;
  private String lastName;
  private String email;
  private Long createdBy;

  @JsonIgnore
  public String getFullName() {
    return String.format("%s %s", this.firstName, this.lastName);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (!(o instanceof User user))
      return false;
    return Objects.equals(getId(), user.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId());
  }
}
