package com.innovationandtrust.profile.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.ElementCollection;
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
import jakarta.persistence.UniqueConstraint;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
    name = "company_setting",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "unique_company_signature_level",
          columnNames = {"signature_level", "company_uuid"})
    })
public class CompanySetting extends AbstractEntity implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", updatable = false, nullable = false, unique = true)
  private Long id;

  @Column(name = "signature_level")
  private String signatureLevel;

  @Column(name = "channel_reminder")
  private String channelReminder;

  @ElementCollection(targetClass = String.class)
  private Set<String> fileType;

  @Column(name = "personal_terms")
  private String personalTerms;

  @Column(name = "identity_terms")
  private String identityTerms;

  @Column(name = "document_terms")
  private String documentTerms;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "company_uuid",
      referencedColumnName = "uuid",
      foreignKey = @ForeignKey(name = "fk_company_setting", value = ConstraintMode.CONSTRAINT))
  private Company company;

  @PrePersist
  void onCreate() {
    setCreatedAt(new Date());
    setModifiedAt(new Date());
  }

  @PreUpdate
  void onUpdate() {
    setModifiedAt(new Date());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Company c)) return false;
    return Objects.equals(getId(), c.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId());
  }
}
