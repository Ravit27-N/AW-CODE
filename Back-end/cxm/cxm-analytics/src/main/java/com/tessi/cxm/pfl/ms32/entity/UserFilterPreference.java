package com.tessi.cxm.pfl.ms32.entity;

import java.util.Date;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Table(name = "user_filter_preference")
@SequenceGenerator(
    name = "USER_FILTER_PREFERENCE_SEQUENCE_GENERATOR",
    sequenceName = "USER_FILTER_PREFERENCE_SEQUENCE",
    allocationSize = 1)
public class UserFilterPreference extends BaseEntity {

  @Id
  @GeneratedValue(
      strategy = GenerationType.SEQUENCE,
      generator = "USER_FILTER_PREFERENCE_SEQUENCE_GENERATOR")
  private Long id;

  @NotNull(message = "The userId field is required")
  private Long ownerId;

  private String selectDateType;

  @Temporal(TemporalType.DATE)
  private Date customStartDate;

  @Temporal(TemporalType.DATE)
  private Date customEndDate;

  @NotNull(message = "The createdBy field is required")
  private String createdBy;

  @PrePersist
  public void initCreatedAt() {
    this.setCreatedAt(new Date());
  }

  @PreUpdate
  public void initModifiedAt() {
    this.setModifiedAt(new Date());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    UserFilterPreference that = (UserFilterPreference) o;
    return id != null && Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
