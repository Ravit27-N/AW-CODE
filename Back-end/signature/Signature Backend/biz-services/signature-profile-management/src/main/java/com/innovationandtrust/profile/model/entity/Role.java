package com.innovationandtrust.profile.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.io.Serializable;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
    name = "roles",
    uniqueConstraints = {
      @UniqueConstraint(name = "unique_role_name", columnNames = "name"),
      @UniqueConstraint(name = "unique_keycloak_role_ref", columnNames = "keycloak_role_id")
    })
@DynamicUpdate
@org.springframework.data.relational.core.mapping.Table
public class Role extends AbstractEntity implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", updatable = false, nullable = false, unique = true)
  private Long id;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "keycloak_role_id", nullable = false)
  private String keycloakRoleId;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Role role)) return false;
    return Objects.equals(getId(), role.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId());
  }
}
