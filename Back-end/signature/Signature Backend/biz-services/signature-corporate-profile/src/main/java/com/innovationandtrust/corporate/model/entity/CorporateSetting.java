package com.innovationandtrust.corporate.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicUpdate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@DynamicUpdate
@Table(
    name = "corporate_settings",
    uniqueConstraints =
        @UniqueConstraint(
            name = "unique_corporate_setting_company",
            columnNames = {"id", "company_id", "is_default"}))
@org.springframework.data.relational.core.mapping.Table
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class CorporateSetting implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", updatable = false, nullable = false, unique = true)
  private Long id;

  @Column(name = "company_id")
  private Long companyId;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  @Column
  private String logo;

  @Column(name = "main_color", length = 15)
  private String mainColor;

  @Column(name = "secondary_color", length = 15)
  private String secondaryColor;

  @Column(name = "link_color", length = 15)
  private String linkColor;

  @Column(name = "is_default", columnDefinition = "boolean default true")
  private boolean isDefault = false;

  public CorporateSetting(Long companyId, String logo, boolean isDefault) {
    this.companyId = companyId;
    this.logo = logo;
    this.isDefault = isDefault;
  }
}
