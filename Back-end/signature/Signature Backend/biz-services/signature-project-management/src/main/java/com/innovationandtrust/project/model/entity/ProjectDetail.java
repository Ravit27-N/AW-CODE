package com.innovationandtrust.project.model.entity;

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
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ProjectDetail is a class that is represents a ProjectDetail table in the database, and it has
 * relationship with project.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
    name = "project_details",
    uniqueConstraints =
        @UniqueConstraint(
            name = "unique_project_detail_type",
            columnNames = {"project_id", "type"}))
@org.springframework.data.relational.core.mapping.Table
public class ProjectDetail implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", updatable = false, nullable = false, unique = true)
  private Long id;

  @Column(name = "type")
  @NotNull(message = "`type` field is required and cannot be null!")
  private String type;

  @Column(name = "title_invitation")
  private String titleInvitation;

  @Column(name = "message_invitation")
  private String messageInvitation;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "project_id",
      referencedColumnName = "id",
      foreignKey = @ForeignKey(name = "fk_project_details", value = ConstraintMode.CONSTRAINT))
  private Project project;
}
