package com.tessi.cxm.pfl.ms5.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@SequenceGenerator(
        name = "AUTHENTICATION_ATTEMPTS_SEQUENCE_GENERATOR",
        sequenceName = "AUTHENTICATION_ATTEMPTS_SEQUENCE",
        allocationSize = 1)
public class AuthenticationAttempts extends BaseEntity {
    
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "AUTHENTICATION_ATTEMPTS_SEQUENCE_GENERATOR")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "user_id")
  private UserEntity userEntity;

  @Column(name = "ATTEMPT_DATE")
  private LocalDateTime attemptDate;

  @Column(columnDefinition = "bool DEFAULT 'true'", name = "IS_FAILED_ATTEMPT")
  private boolean isFailedAttempt;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof AuthenticationAttempts)) {
      return false;
    }
    AuthenticationAttempts that = (AuthenticationAttempts) o;
    return Objects.equals(getId(), that.getId())
            && Objects.equals(getUserEntity().getId(), that.getUserEntity().getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), getId(), getUserEntity().getId());
  }


  @PrePersist
  private void create() {
    setCreatedAt(new Date());
  }

  @PreUpdate
  private void update() {
    setLastModified(new Date());
  }

}
