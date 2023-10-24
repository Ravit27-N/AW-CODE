package com.innovationandtrust.corporate.model.entity;

import jakarta.persistence.Cacheable;
import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
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
@Table(
    name = "business_units",
    uniqueConstraints =
    @UniqueConstraint(
        name = "unique_business_unit",
        columnNames = {"sort_order", "parent_id"}))
@org.springframework.data.relational.core.mapping.Table
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class BusinessUnit extends AbstractEntity implements Comparable<BusinessUnit>, Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", updatable = false, nullable = false, unique = true)
  private Long id;

  @Column(name = "unit_name")
  private String unitName;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "company_detail_id",
      referencedColumnName = "id",
      foreignKey =
      @ForeignKey(name = "fk_business_unit_company_detail", value = ConstraintMode.CONSTRAINT))
  private CompanyDetail companyDetail;

  @Column(name = "sort_order")
  private Integer sortOrder;

  @OneToMany(mappedBy = "businessUnit", fetch = FetchType.LAZY)
  private Set<Folder> folders = new HashSet<>();

  @OneToMany(mappedBy = "businessUnit", fetch = FetchType.LAZY)
  private Set<Employee> employees = new HashSet<>();

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "parent_id",
      referencedColumnName = "id",
      foreignKey = @ForeignKey(name = "fk_business_unit_parent", value = ConstraintMode.CONSTRAINT))
  private BusinessUnit parent;

  @OneToMany(
      mappedBy = "parent",
      fetch = FetchType.LAZY,
      cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  private Set<BusinessUnit> children = new TreeSet<>();


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof BusinessUnit businessUnit)) {
      return false;
    }
    return Objects.equals(getId(), businessUnit.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId());
  }

  @PrePersist
  void onCreate() {
    setCreatedAt(new Date());
  }

  @PreUpdate
  void onUpdate() {
    setModifiedAt(new Date());
  }

  @Override
  public int compareTo(BusinessUnit businessUnit) {
    return this.sortOrder.compareTo(businessUnit.getSortOrder());
  }
}
