package com.innovationandtrust.utils.keycloak.model;

import java.io.Serializable;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.keycloak.representations.idm.RoleRepresentation;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KeycloakRoleResponse implements Serializable {
  private String id;
  private String name;

  public KeycloakRoleResponse(RoleRepresentation response) {
    this(response.getId(), response.getName());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof KeycloakRoleResponse that)) return false;
    return Objects.equals(getId(), that.getId()) && Objects.equals(getName(), that.getName());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getName());
  }
}
