package com.innovationandtrust.profile.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
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
public class RoleDto implements Serializable {
  private Long id;
  private String name;

  @JsonProperty(access = Access.READ_ONLY)
  private String keycloakRoleId;

  public RoleDto(Long id, String name) {
    this.id = id;
    this.name = name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof RoleDto roleDto)) {
      return false;
    }
    return Objects.equals(getId(), roleDto.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId());
  }
}
