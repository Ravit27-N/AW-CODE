package com.innovationandtrust.utils.keycloak.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KeycloakUserRequest implements Serializable {

  private String id;
  private String firstName;
  private String lastName;
  @Builder.Default
  private boolean active = false;

  @JsonProperty(access = Access.WRITE_ONLY)
  private String password;

  private String email;

  @Builder.Default
  private List<String> roles = new ArrayList<>();

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof KeycloakUserRequest that)) return false;
    return Objects.equals(getId(), that.getId()) && Objects.equals(getEmail(), that.getEmail());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getEmail());
  }
}
