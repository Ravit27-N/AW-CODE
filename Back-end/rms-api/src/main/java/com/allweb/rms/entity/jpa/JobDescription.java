package com.allweb.rms.entity.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@ToString
@Builder
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SequenceGenerator(
    name = "JOB_DESCRIPTION_SEQUENCE_GENERATOR",
    sequenceName = "JOB_DESCRIPTION_SEQUENCE",
    allocationSize = 1)
@Table(indexes = {@Index(name = "IDX_JOB_DESCRIPTION", columnList = "title")})
public class JobDescription extends AbstractEntity implements Serializable {
  private static final long serialVersionUID = 1L;
  /*
   *
   * Entity class of jobDes object
   *
   * */
  @Id
  @GeneratedValue(
      strategy = GenerationType.SEQUENCE,
      generator = "JOB_DESCRIPTION_SEQUENCE_GENERATOR")
  @Column(length = 11)
  private int id;

  @Column(nullable = false, length = 200)
  @NotEmpty
  @NotNull
  private String title;

  @Column(columnDefinition = "text")
  private String description;

  private String filename;

  @Column(nullable = false)
  @NotNull
  private boolean active;

  public JobDescription(
      int id,
      String title,
      String description,
      String filename,
      boolean isActive,
      Date createdAt,
      Date updatedAt) {
    this.id = id;
    this.title = title;
    this.description = description;
    this.filename = filename;
    this.active = isActive;
    setUpdatedAt(updatedAt);
    setCreatedAt(createdAt);
  }

  @PrePersist
  void onCreate() {
    setCreatedAt(new Date());
  }

  @PreUpdate
  void onUpdate() {
    setCreatedAt(new Date());
  }
}
