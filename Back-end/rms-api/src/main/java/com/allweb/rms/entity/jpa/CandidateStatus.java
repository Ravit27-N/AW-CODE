package com.allweb.rms.entity.jpa;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

@Entity
@Data
@ToString
@Builder
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SequenceGenerator(
    name = "STATUS_CANDIDATE_SEQUENCE_GENERATOR",
    sequenceName = "STATUS_CANDIDATE_SEQUENCE",
    initialValue = 1,
    allocationSize = 1)
@Table
public class CandidateStatus extends AbstractEntity implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(
      strategy = GenerationType.SEQUENCE,
      generator = "STATUS_CANDIDATE_SEQUENCE_GENERATOR")
  private int id;

  @NotEmpty
  @Column(unique = true)
  @Schema(maximum = "50")
  private String title;

  private boolean isDeleted;
  @NotEmpty private String description;
  private boolean active;
  private boolean isDeletable;

  @PrePersist
  void onCreate() {
    this.isDeleted = false;
    this.isDeletable = true;
    setCreatedAt(new Date());
    setUpdatedAt(new Date());
  }

  @PreUpdate
  void onUpdate() {
    setUpdatedAt(new Date());
  }
}
