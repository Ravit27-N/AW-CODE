package com.innovationandtrust.corporate.model.entity;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@DynamicUpdate
@Table(name = "employees")
@org.springframework.data.relational.core.mapping.Table
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Employee extends AbstractEntity implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", updatable = false, nullable = false, unique = true)
  private Long id;

  @Column(name = "first_name")
  private String firstName;

  @Column(name = "last_name")
  private String lastName;

  @Column private String functional;

  @Column(name = "user_id")
  private Long userId;

  @Column(name = "deleted", columnDefinition = "bool default false")
  private boolean deleted;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "business_unit_id",
      referencedColumnName = "id",
      foreignKey =
          @ForeignKey(name = "fk_employee_unit_employee", value = ConstraintMode.CONSTRAINT))
  @Fetch(FetchMode.JOIN)
  private BusinessUnit businessUnit;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "user_access_id",
      referencedColumnName = "id",
      foreignKey =
          @ForeignKey(name = "fk_user_accesses_employees", value = ConstraintMode.CONSTRAINT))
  private UserAccess userAccess;

  @PrePersist
  void onCreate() {
    setCreatedAt(new Date());
  }

  @PreUpdate
  void onUpdate() {
    setModifiedAt(new Date());
  }
}
