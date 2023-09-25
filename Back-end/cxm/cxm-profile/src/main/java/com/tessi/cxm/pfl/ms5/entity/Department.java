package com.tessi.cxm.pfl.ms5.entity;

import com.tessi.cxm.pfl.shared.auth.AuthenticationUtils;
import java.util.Date;
import java.util.List;
import java.util.Objects;
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
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * This entity refer to Service in terms of business requirement.
 */
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "service")
@SequenceGenerator(name = "SERVICE_SEQUENCE_GENERATOR", sequenceName = "SERVICE_SEQUENCE", allocationSize = 1)
public class Department extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SERVICE_SEQUENCE_GENERATOR")
  private long id;

  @Column(nullable = false)
  private String name;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "division_id")
  private Division division;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "department")
  private List<UserEntity> users;

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
    if (!(o instanceof Department)) {
      return false;
    }
    Department that = (Department) o;
    return Objects.equals(getId(), that.getId())
        && Objects.equals(getDivision().getId(), that.getDivision().getId())
        && Objects.equals(getDivision().getClient(), that.getDivision().getClient());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getDivision().getId(), getDivision().getClient());
  }
}
