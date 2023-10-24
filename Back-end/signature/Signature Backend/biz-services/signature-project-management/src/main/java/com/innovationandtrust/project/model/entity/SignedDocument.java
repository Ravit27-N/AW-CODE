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
import java.io.Serializable;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

/**
 * SignedDocument is a class that is representing a SignedDocument table in the database, and it has
 * a relationship with signatory.
 */
@Entity
@Table(name = "signed_documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate
@org.springframework.data.relational.core.mapping.Table
public class SignedDocument implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "file_name")
  private String fileName;

  @Column(name = "full_path")
  private String fullPath;

  @Column(name = "content_length")
  private int contentLength;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "signatory_id",
      referencedColumnName = "id",
      foreignKey =
      @ForeignKey(name = "fk_signed_file_signatory_id", value = ConstraintMode.CONSTRAINT),
      nullable = false)
  private Signatory signatory;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof SignedDocument signatorySignedDocument)) {
      return false;
    }
    return Objects.equals(getId(), signatorySignedDocument.getId())
        && Objects.equals(getSignatory().getId(), signatorySignedDocument.getSignatory().getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getSignatory().getId());
  }
}
