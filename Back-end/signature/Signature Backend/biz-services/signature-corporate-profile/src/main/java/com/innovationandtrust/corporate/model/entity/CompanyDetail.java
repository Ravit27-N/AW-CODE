package com.innovationandtrust.corporate.model.entity;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.Set;
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
@Table(name = "company_details")
@org.springframework.data.relational.core.mapping.Table
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class CompanyDetail implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", updatable = false, nullable = false, unique = true)
  private Long id;

  @Column(name = "logo")
  private String logo;

  @Column(name = "company_id")
  private Long companyId;

  @Column(name = "company_uuid")
  private String companyUuid;

  @Column(name = "user_id")
  private Long userId;

  @Column(name = "creation_date")
  private Date creationDate;

  @Column(name = "name", length = 50)
  private String name;

  @Column
  private String address;

  @Column(name = "first_name")
  private String firstName;

  @Column(name = "last_name")
  private String lastName;

  @Column
  private String gender;

  @OneToMany(mappedBy = "companyDetail", fetch = FetchType.LAZY)
  @Column(name = "business_units")
  @Fetch(FetchMode.JOIN)
  private Set<BusinessUnit> businessUnits;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (!(o instanceof CompanyDetail that)) {
      return false;
    }
    return Objects.equals(getId(), that.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId());
  }
}
