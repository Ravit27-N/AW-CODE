package com.techno.ms2.quartzscheduling.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@SequenceGenerator(
    name = "BOOK_CONFIGURATION_SEQUENCE_GENERATOR",
    sequenceName = "BOOK_CONFIGURATION_SEQUENCE_GENERATOR",
    allocationSize = 1)
@Table(name = "check_quartz")
public class CheckQuartz {
  @Id
  @GeneratedValue(
      strategy = GenerationType.SEQUENCE,
      generator = "BOOK_CONFIGURATION_SEQUENCE_GENERATOR")
  private Long id;

  @Column(name = "check-name")
  private String checkName;

  @Column(name = "time")
  private LocalDateTime time;

  @Column(name = "url")
  private String url;

  @Column(name = "frequency")
  private String frequency;

  @Column(name = "unit")
  private String unit;

  @JsonIgnore
  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @JsonIgnore
  @Column(name = "modified_at", nullable = false)
  private LocalDateTime modifiedAt;

  @PrePersist
  void onCreate() {
    setModifiedAt(LocalDateTime.now());
    setCreatedAt(LocalDateTime.now());
  }

  @PreUpdate
  void onUpdate() {
    setModifiedAt(LocalDateTime.now());
  }
}
