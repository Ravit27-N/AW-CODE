package com.tessi.cxm.pfl.ms5.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tessi.cxm.pfl.shared.utils.JavaTypeConstants;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString.Exclude;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Type;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Slf4j
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"functionalityKey", "profile_id"})})
@SequenceGenerator(
    name = "PROFILE_DETAIL_GENERATOR",
    sequenceName = "PROFILE_DETAIL_SEQUENCE",
    allocationSize = 1
)
public class ProfileDetails extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PROFILE_DETAIL_GENERATOR")
  private long id;

  @Size(max = 128)
  @NotEmpty()
  private String functionalityKey;

  @JsonProperty
  @Type(type = JavaTypeConstants.JSON)
  @Column(columnDefinition = "jsonb")
  @Basic(fetch = FetchType.LAZY)
  private List<Privilege> privileges = new ArrayList<>();

  private String visibilityLevel;

  private String modificationLevel;

  @ManyToOne(
      fetch = FetchType.LAZY,
      cascade = {CascadeType.PERSIST})
  @JoinColumn(name = "profile_id", referencedColumnName = "id")
  @Exclude
  @JsonIgnore
  private Profile profile;

  @ManyToOne(fetch = FetchType.LAZY)
  @JsonIgnore
  @Exclude
  private Client client;

  @PrePersist
  private void initCreatedAtDate() {
    this.setCreatedAt(new Date());
  }

  @PreUpdate
  private void initLastModified() {
    this.setLastModified(new Date());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ProfileDetails)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    ProfileDetails that = (ProfileDetails) o;
    return getFunctionalityKey().equals(that.getFunctionalityKey()) && getProfile().equals(
        that.getProfile()) && Objects.equals(id, that.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), getFunctionalityKey(), getProfile(), getId());
  }
}
