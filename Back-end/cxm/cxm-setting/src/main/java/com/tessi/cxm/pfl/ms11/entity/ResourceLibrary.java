package com.tessi.cxm.pfl.ms11.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@SequenceGenerator(
    name = "RESOURCE_LIBRARY_SEQUENCE_GENERATOR",
    sequenceName = "RESOURCE_LIBRARY_SEQUENCE",
    allocationSize = 1)
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"clientId", "label", "type"}))
public class ResourceLibrary extends BaseEntity {

  @Id
  @GeneratedValue(
      strategy = GenerationType.SEQUENCE,
      generator = "RESOURCE_LIBRARY_SEQUENCE_GENERATOR")
  private Long id;

  private String fileName;

  @Column(unique = true)
  private String fileId;

  private String label;

  private String type;
  private Long fileSize;
  private Long pageNumber;

  @Column(nullable = false)
  private Long ownerId;

  private Long clientId;

  @OneToMany
  @NotFound(action = NotFoundAction.IGNORE)
  @JoinColumn(
      name = "key",
      referencedColumnName = "type",
      insertable = false,
      updatable = false,
      foreignKey = @javax.persistence.ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
  private List<ResourceTypeTranslate> resourceTypeTranslate = new ArrayList<>();

  @PrePersist
  private void create() {
    this.setCreatedAt(new Date());
  }

  @PreUpdate
  private void update() {
    setLastModified(new Date());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ResourceLibrary that = (ResourceLibrary) o;
    return id.equals(that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
