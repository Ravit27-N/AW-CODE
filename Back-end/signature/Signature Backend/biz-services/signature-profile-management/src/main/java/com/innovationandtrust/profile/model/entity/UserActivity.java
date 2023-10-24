package com.innovationandtrust.profile.model.entity;

import jakarta.persistence.Column;
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
import jakarta.validation.constraints.Email;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

/** This entity for forgetting password, activate user, change user mail */
@Entity
@Getter
@Setter
@Builder
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_activity")
public class UserActivity extends AbstractEntity implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", updatable = false, nullable = false, unique = true)
  private Long id;

  @Column(name = "token", unique = true)
  private String token;

  @Email
  @Column(name = "pending_mail")
  private String pendingMail;

  @Email
  @Column(name = "current_mail")
  private String currentMail;

  @Column(name = "expire_time")
  private Date expireTime;

  @Column(name = "actioned", columnDefinition = "bool default false")
  private boolean actioned;

  @Column(name = "finished", columnDefinition = "bool default false")
  private boolean finished;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "user_id",
      referencedColumnName = "id",
      foreignKey = @ForeignKey(name = "fk_user_activity"))
  private User user;

  @PrePersist
  void onCreate() {
    this.setCreatedAt(new Date());
    this.setModifiedAt(new Date());
  }

  @PreUpdate
  void onUpdate() {
    this.setModifiedAt(new Date());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof UserActivity userActivity)) return false;
    return Objects.equals(getId(), userActivity.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId());
  }
}
