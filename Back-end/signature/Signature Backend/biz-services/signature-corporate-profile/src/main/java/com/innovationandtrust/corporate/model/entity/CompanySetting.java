package com.innovationandtrust.corporate.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

  @Column(name = "company_channel")
  private String companyChannel;

  @ElementCollection(targetClass = String.class)
  private Set<String> companyFileType;

  @Column(name = "personal_terms")
  private String personalTerms;

  @Column(name = "identity_terms")
  private String identityTerms;

  @Column(name = "document_terms")
  private String documentTerms;

  @Column(name = "company_uuid")
  private String companyUuid;

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
    if (!(o instanceof CompanySetting companySetting)) return false;
    return Objects.equals(getId(), companySetting.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId());
  }
}
