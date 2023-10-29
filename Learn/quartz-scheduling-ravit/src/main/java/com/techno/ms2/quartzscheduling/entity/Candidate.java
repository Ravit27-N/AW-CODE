package com.techno.ms2.quartzscheduling.entity;


import jakarta.persistence.*;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "candidate")
@SequenceGenerator(
    name = "CANDIDATE_SEQUENCE_GENERATOR",
    sequenceName = "CANDIDATE_SEQUENCE",
    allocationSize = 1)
public class Candidate extends BaseEntity{

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CANDIDATE_SEQUENCE_GENERATOR")
  private Long id;

  private String name;

  private int age;

  @ColumnDefault("true")
  private boolean activeStatus;

  @PrePersist
  private void create() {
    setCreatedAt(new Date());
    setCreatedBy("user");
  }

  @PreUpdate
  private void update() {
    setLastModified(new Date());
    setLastModifiedBy("user");
  }
}
