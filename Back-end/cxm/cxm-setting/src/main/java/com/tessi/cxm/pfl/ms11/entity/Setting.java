package com.tessi.cxm.pfl.ms11.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.Date;
import java.util.Objects;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@ToString
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@SequenceGenerator(
    name = "SETTING_SEQUENCE_GENERATOR",
    sequenceName = "SETTING_SEQUENCE",
    allocationSize = 1)
@Table(
    uniqueConstraints = {
      @UniqueConstraint(columnNames = {"customer", "depositType", "extension", "flowType"})
    })
public class Setting extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SETTING_SEQUENCE_GENERATOR")
  private Long id;

  private String customer;
  private String depositType;
  private String connector;
  private String extension;
  private String flowType;
  private Long idCreator;

  @Column(columnDefinition = "boolean DEFAULT true")
  private boolean scanActivation;

  @OneToOne(
      mappedBy = "setting",
      cascade = CascadeType.ALL,
      fetch = FetchType.LAZY,
      orphanRemoval = true)
  @ToString.Exclude
  private SettingInstruction settingInstruction;

  @OneToOne(
      mappedBy = "setting",
      fetch = FetchType.LAZY,
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  @Fetch(FetchMode.JOIN)
  @ToString.Exclude
  private PortalSetting portalSetting;

  @PrePersist
  private void create() {
    setCreatedAt(new Date());
  }

  @PreUpdate
  private void update() {
    setLastModified(new Date());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    Setting setting = (Setting) o;
    return id != null && Objects.equals(id, setting.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
