package com.tessi.cxm.pfl.ms5.entity;

import com.tessi.cxm.pfl.shared.auth.AuthenticationUtils;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.util.Date;

@Entity
@IdClass(UserProfileKey.class)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserProfiles extends BaseEntity implements Comparable<UserProfiles> {

  @Id
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "USER_ID", referencedColumnName = "ID")
  @OnDelete(action = OnDeleteAction.CASCADE)
  private UserEntity user;

  @Id
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "PROFILE_ID", referencedColumnName = "ID")
  private Profile profile;

  @PrePersist
  private void create() {
    setCreatedAt(new Date());
    setCreatedBy(AuthenticationUtils.getPrincipal());
  }

  @PreUpdate
  private void update() {
    setLastModified(new Date());
    setLastModifiedBy(AuthenticationUtils.getPrincipal());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof UserProfiles)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    UserProfiles that = (UserProfiles) o;
    return getUser().equals(that.getUser()) && getProfile().equals(that.getProfile());
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), getUser(), getProfile());
  }

  @Override
  public int compareTo(UserProfiles o) {
    return o.getProfile().compareTo(this.getProfile());
  }
}
