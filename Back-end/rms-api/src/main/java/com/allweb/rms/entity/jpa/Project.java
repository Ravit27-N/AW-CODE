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
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@SequenceGenerator(
    name = "PROJECT_SEQUENCE_GENERATOR",
    sequenceName = "PROJECT_SEQUENCE",
    initialValue = 1,
    allocationSize = 1)
@Table
public class Project extends AbstractEntity implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PROJECT_SEQUENCE_GENERATOR")
  private int id;

  @NotEmpty
  @Column(unique = true)
  @Schema(maximum = "50")
  private String name;

  private String description;
  private boolean active;
  private boolean isDeleted;

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof Project book)) return false;
    return Objects.equals(id, book.getId());
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }

  @PrePersist
  void onCreate() {
    this.active = true;
    this.isDeleted = false;
    setCreatedAt(new Date());
    setUpdatedAt(new Date());
  }

  @PreUpdate
  void onUpdate() {
    setUpdatedAt(new Date());
  }
}
