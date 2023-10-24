package com.innovationandtrust.project.model.entity;

import com.innovationandtrust.share.constant.DocumentStatus;
import com.innovationandtrust.share.constant.InvitationStatus;
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
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

/**
 * Signatory is a class that is representing a Signatory table in the database, and it has a
 * relationship with Project.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@DynamicUpdate
@Entity
@Table(
    name = "signatories",
    uniqueConstraints =
    @UniqueConstraint(
        name = "unique_project_signatory",
        columnNames = {"sort_order", "project_id"}))
@org.springframework.data.relational.core.mapping.Table
public class Signatory extends AbstractBaseEntity implements Comparable<Signatory>, Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", updatable = false, nullable = false, unique = true)
  private Long id;

  @Column(name = "first_name")
  private String firstName;

  @Column(name = "last_name")
  private String lastName;

  private String role;

  private String email;

  private String phone;

  private String comment;

  @Column(name = "invitation_status")
  private String invitationStatus;

  @Column(name = "document_status")
  private String documentStatus;

  @Column(name = "sort_order", nullable = false)
  private Integer sortOrder;

  @Column
  private String uuid;

  @Column
  private Date dateStatus;

  @Column
  private Date sentDate;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "project_id",
      referencedColumnName = "id",
      foreignKey = @ForeignKey(name = "fk_project_signatory", value = ConstraintMode.CONSTRAINT))
  private Project project;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Signatory signatory)) {
      return false;
    }
    return Objects.equals(getId(), signatory.getId())
        && Objects.equals(getSortOrder(), signatory.getSortOrder()) && Objects.equals(getProject(),
        signatory.getProject());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getSortOrder(), getProject());
  }

  @Override
  public int compareTo(Signatory signatory) {
    if (Objects.nonNull(this.sortOrder)) {
      return this.sortOrder.compareTo(signatory.getSortOrder());
    }
    return 0;
  }

  @PrePersist
  void onCreate() {
    this.setCreatedAt(new Date());
    this.setInvitationStatus(InvitationStatus.ON_HOLD.name());
    this.setDocumentStatus(DocumentStatus.IN_PROGRESS.name());
  }

  @PreUpdate
  void onUpdate() {
    setModifiedAt(new Date());
  }
}
