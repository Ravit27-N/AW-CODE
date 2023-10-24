package com.innovationandtrust.profile.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
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
    name = "companies",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "unique_company_name_and_siret",
          columnNames = {"name", "siret"}),
      @UniqueConstraint(name = "unique_uuid", columnNames = "uuid")
    })
@org.springframework.data.relational.core.mapping.Table
public class Company extends AbstractEntity implements Serializable {
  @Serial private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", updatable = false, nullable = false, unique = true)
  private Long id;

  @Column(name = "uuid")
  private String uuid;

  @NotNull @NotEmpty @Column private String name;

  @NotNull @NotEmpty @Column private String siret;

  @Column private String logo;

  @Column private String mobile;

  @Column private String email;

  @Column(name = "contact_first_name")
  private String contactFirstName;

  @Column(name = "contact_last_name")
  private String contactLastName;

  @Column(name = "fix_number")
  private String fixNumber;

  @Column(name = "address_line_1")
  private String addressLine1;

  @Column(name = "address_line_2")
  private String addressLine2;

  @Column(name = "postal_code")
  private String postalCode;

  @Column private String state;
  @Column private String country;
  @Column private String city;
  @Column private String territory;

  @Column(name = "is_archiving", columnDefinition = "boolean default true")
  private boolean isArchiving = true;

  @OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
  private Set<Template> templates = new HashSet<>();

  @PrePersist
  void onCreate() {
    setCreatedAt(new Date());
    setModifiedAt(new Date());
    setUuid(UUID.randomUUID().toString());
  }

  @PreUpdate
  void onUpdate() {
    setModifiedAt(new Date());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Company company)) return false;
    return Objects.equals(getId(), company.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId());
  }
}
