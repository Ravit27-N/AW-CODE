package com.tessi.cxm.pfl.ms5.entity;

import com.tessi.cxm.pfl.shared.auth.AuthenticationUtils;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@SequenceGenerator(
    name = "DIVISION_SEQUENCE_GENERATOR",
    sequenceName = "DIVISION_SEQUENCE",
    allocationSize = 1)
public class Division extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DIVISION_SEQUENCE_GENERATOR")
  private long id;

  @Column(nullable = false)
  private String name;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "client_id", referencedColumnName = "id")
  private Client client;

  @OneToMany(mappedBy = "division", cascade = CascadeType.ALL, orphanRemoval = true)
  @EqualsAndHashCode.Exclude
  private List<Department> departments = new ArrayList<>();

  @PrePersist
  private void create() {
    setCreatedAt(new Date());
    setCreatedBy(AuthenticationUtils.getPrincipal());
  }

  @PreUpdate
  private void update() {
    setLastModified(new Date());
    setCreatedBy(AuthenticationUtils.getPrincipal());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Division)) {
      return false;
    }
    Division that = (Division) o;
    return Objects.equals(getId(), that.getId())
        && Objects.equals(getClient().getId(), that.getClient().getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getClient().getId());
  }

  public void addDepartments(List<Department> departments) {
    this.departments.clear();
    this.departments.addAll(departments);
    departments.forEach(d -> d.setDivision(this));
  }

  public void addDepartment(Department department) {
    this.departments.add(department);
    department.setDivision(this);
  }

  public void removeDepartment(Department department) {
    this.departments.remove(department);
    department.setDivision(null);
  }

  public void setDepartments(List<Department> departments) {
    this.departments.clear();
    this.departments.addAll(departments);
  }
}
