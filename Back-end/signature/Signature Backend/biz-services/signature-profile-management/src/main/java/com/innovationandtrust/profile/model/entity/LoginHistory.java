package com.innovationandtrust.profile.model.entity;

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
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "login_histories")
@org.springframework.data.relational.core.mapping.Table
public class LoginHistory extends AbstractEntity implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", updatable = false, nullable = false, unique = true)
  private Long id;

  @Column(name = "login_email", updatable = false)
  private String loginEmail;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "user_id",
      referencedColumnName = "id",
      foreignKey =
          @ForeignKey(name = "fk_login_history_user_id", value = ConstraintMode.CONSTRAINT))
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
  public int hashCode() {
    return Objects.hash(getId());
  }
}
