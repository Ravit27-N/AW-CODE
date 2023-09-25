package com.allweb.rms.entity.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@SequenceGenerator(
    name = "COMPANY_PROFILE_SEQUENCE_GENERATOR",
    sequenceName = "COMPANY_PROFILE_SEQUENCE",
    allocationSize = 1)
@Table
public class CompanyProfile extends AbstractEntity implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(
      strategy = GenerationType.SEQUENCE,
      generator = "COMPANY_PROFILE_SEQUENCE_GENERATOR")
  private int id;

  private String title;

  @Column(columnDefinition = "text")
  private String description;

  @Column(columnDefinition = "text")
  private String address;

  @NotEmpty private String telephone;
  @NotEmpty private String email;
  @NotEmpty private String website;

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!(obj instanceof CompanyProfile book)) return false;
    return Objects.equals(id, book.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId());
  }

  @PrePersist
  void onCreate() {
    setCreatedAt(new Date());
  }

  @PreUpdate
  void onUpdate() {
    setUpdatedAt(new Date());
  }
}
