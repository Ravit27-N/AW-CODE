package com.allweb.rms.entity.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
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
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
@SequenceGenerator(
    name = "MODULE_SEQUENCE_GENERATOR",
    sequenceName = "MODULE_SEQUENCE",
    allocationSize = 1,
    initialValue = 13)
@Table
public class Module extends AbstractEntity implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MODULE_SEQUENCE_GENERATOR")
  private int id;

  @NotEmpty private String name;

  @Column(columnDefinition = "text")
  private String description;

  private boolean active;

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!(obj instanceof Module book)) return false;
    return Objects.equals(id, book.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId());
  }

  @PrePersist
  void onCreate() {
    this.active = true;
    setCreatedAt(new Date());
    setUpdatedAt(new Date());
  }

  @PreUpdate
  void onUpdate() {
    setUpdatedAt(new Date());
  }
}
