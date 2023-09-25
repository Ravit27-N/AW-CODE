package com.allweb.rms.entity.jpa;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Entity
@Getter
@Setter
@Builder
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SequenceGenerator(
    name = "STATUS_SEQUENCE_GENERATOR",
    sequenceName = "STATUS_SEQUENCE",
    initialValue = 1,
    allocationSize = 1)
@Table(
    name = "interview_status",
    indexes = {@Index(name = "IDX_INTERVIEW_STATUS", columnList = "name")})
public class InterviewStatus extends AbstractEntity implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "STATUS_SEQUENCE_GENERATOR")
  @Schema(name = "id", type = "int", description = "Not required when create new")
  private int id;

  @Schema(type = "string", required = true, maximum = "50", name = "name")
  @Column(length = 50, unique = true)
  private String name;

  @Schema(
      name = "isActive",
      type = "boolean",
      allowableValues = {"true", "false"})
  private boolean isActive;

  @PrePersist
  void onCreate() {
    setCreatedAt(new Date());
  }

  @PreUpdate
  void onUpdate() {
    setUpdatedAt(new Date());
  }
}
