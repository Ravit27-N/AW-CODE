package com.tessi.cxm.pfl.ms11.entity;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@SequenceGenerator(
    name = "CRITERIA_DISTRIBUTION_SEQUENCE_GENERATOR",
    sequenceName = "CRITERIA_DISTRIBUTION_SEQUENCE",
    allocationSize = 1)
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "customer"})})
@EntityListeners({AuditingEntityListener.class})
public class CriteriaDistribution extends BaseEntityTimeAudit {
  @Id
  @GeneratedValue(
      strategy = GenerationType.SEQUENCE,
      generator = "CRITERIA_DISTRIBUTION_SEQUENCE_GENERATOR")
  private Long id;

  private String name;
  private String customer;
  private boolean isActive;

  @PrePersist
  private void create() {
    setCreatedAt(new Date());
  }

  @PreUpdate
  private void update() {
    setLastModified(new Date());
  }
}
