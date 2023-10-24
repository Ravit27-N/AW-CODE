package com.innovationandtrust.project.model.entity;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * ProjectHistory is a class that is representing a ProjectHistory table in the database, and it has
 * a relationship with Project.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
    name = "project_histories",
    uniqueConstraints =
    @UniqueConstraint(
        name = "unique_project_signatory",
        columnNames = {"sort_order", "project_id"}))
@org.springframework.data.relational.core.mapping.Table
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ProjectHistory extends AbstractBaseEntity
    implements Comparable<ProjectHistory>, Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", updatable = false, nullable = false, unique = true)
  private Long id;

  @Column(name = "date_status")
  @NotNull(message = "`date_status` field is required and cannot be empty.")
  private Date dateStatus;

  @NotEmpty(message = "`action` field is required and cannot be empty!")
  private String action;

  private String actionBy;

  @Column(name = "sort_order")
  private Integer sortOrder;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "project_id",
      referencedColumnName = "id",
      foreignKey = @ForeignKey(name = "fk_project_history", value = ConstraintMode.CONSTRAINT))
  private Project project;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ProjectHistory that)) {
      return false;
    }
    return Objects.equals(id, that.id) && Objects.equals(sortOrder, that.sortOrder);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, sortOrder);
  }

  @Override
  public int compareTo(ProjectHistory projectHistory) {
    if (Objects.isNull(this.sortOrder)) {
      return 0;
    }
    return this.sortOrder.compareTo(projectHistory.getSortOrder());
  }

  @PrePersist
  void onCreate() {
    setCreatedAt(new Date());
  }

  @PreUpdate
  void onUpdate() {
    setModifiedAt(new Date());
  }
}
