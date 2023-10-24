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
@AllArgsConstructor
@NoArgsConstructor
public class KeycloakRoleRequest implements Serializable {
  private String id;
  private String name;

  public RoleRepresentation getRole() {
    var roleRepresentation = new RoleRepresentation();
    roleRepresentation.setName(this.name);
    return roleRepresentation;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof KeycloakRoleRequest that)) return false;
    return Objects.equals(getId(), that.getId()) && Objects.equals(getName(), that.getName());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getName());
  }
}
