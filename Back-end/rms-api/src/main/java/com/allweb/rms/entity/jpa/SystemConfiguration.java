package com.allweb.rms.entity.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.cache.annotation.Cacheable;

@Entity
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SequenceGenerator(
    name = "SYSTEM_MAIL_CONFIGURATION_SEQUENCE_GENERATOR",
    sequenceName = "SYSTEM_MAIL_CONFIGURATION_SEQUENCE",
    initialValue = 13,
    allocationSize = 1)
@Cache(region = "systemConfigCache", usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Cacheable
@Table
public class SystemConfiguration extends AbstractEntity implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(
      strategy = GenerationType.SEQUENCE,
      generator = "SYSTEM_MAIL_CONFIGURATION_SEQUENCE_GENERATOR")
  private int id;

  @Column(length = 50, nullable = false, unique = true)
  private String configKey;

  @Column(length = 200, nullable = false)
  private String configValue;

  @Column(columnDefinition = "text")
  private String description;

  private boolean active;

  @PrePersist
  void onCreate() {
    setCreatedAt(new Date());
  }

  @PreUpdate
  void onUpdate() {
    setUpdatedAt(new Date());
  }
}
