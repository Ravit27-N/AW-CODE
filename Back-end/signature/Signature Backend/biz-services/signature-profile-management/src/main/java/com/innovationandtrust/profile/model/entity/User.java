package com.innovationandtrust.profile.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@DynamicUpdate
@Table(
    name = "users",
    uniqueConstraints = {
      @UniqueConstraint(name = "unique_keycloak_user_id_ref", columnNames = "user_entity_id"),
      @UniqueConstraint(name = "unique_user_email", columnNames = "email")
    })
@org.springframework.data.relational.core.mapping.Table
public class User extends AbstractEntity implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", updatable = false, nullable = false, unique = true)
  private Long id;

  @Column(name = "user_entity_id", nullable = false)
  private String userEntityId;

  @Column(name = "first_name")
  private String firstName;

  @Column(name = "last_name")
  private String lastName;

  @Column(length = 254)
  @Email
  private String email;

  @Column private String phone;

  @Column private String functional;

  @Column(name = "active", columnDefinition = "bool default true")
  private boolean active;

  @Column(name = "deleted", columnDefinition = "bool default false")
  private boolean deleted;

  @Column(name = "company_id")
  private long companyId;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "user_roles",
      joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
      inverseJoinColumns = {@JoinColumn(name = "role_id", referencedColumnName = "id")})
  private Set<Role> roles = new HashSet<>();

  public User(Long id, String firstName, String lastName) {
    this.id = id;
    this.firstName = firstName;
    this.lastName = lastName;
  }

  @PrePersist
  void onCreate() {
    this.setCreatedAt(new Date());
    this.setModifiedAt(new Date());
  }

  @PreUpdate
  void onUpdate() {
    this.setModifiedAt(new Date());
  }

  public Set<String> getRoles() {
    if (this.roles.isEmpty()) {
      return new HashSet<>();
    }
    return this.roles.stream().map(Role::getName).collect(Collectors.toSet());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof User user)) return false;
    return Objects.equals(getId(), user.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId());
  }
}
