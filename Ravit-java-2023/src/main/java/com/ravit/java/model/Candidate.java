package com.ravit.java.model;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
