package com.innovationandtrust.project.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

/**
 * Document is a class that is representing a document table in the database, and it has a
 * relationship with documentDetail and Project.
 */
@Entity
@Table(name = "documents")
@Getter
@Setter
@NoArgsConstructor
@DynamicUpdate
@org.springframework.data.relational.core.mapping.Table
public class Document implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "file_name")
  private String fileName;

  @Column(name = "original_file_name")
  private String originalFileName;

  @Column(name = "signed_doc_url")
  private String signedDocUrl;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  @Column(name = "edited_file_name")
  private String editedFileName;

  @Column(name = "content_type")
  private String contentType;

  @Column(name = "full_path")
  private String fullPath;

  private Long size;
  private String extension;

  @Column(name = "total_pages")
  private Integer totalPages;

  @OneToMany(mappedBy = "document", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @Fetch(FetchMode.JOIN)
  private Set<DocumentDetail> documentDetails;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "project_id",
      referencedColumnName = "id",
      foreignKey = @ForeignKey(name = "fk_file", value = ConstraintMode.CONSTRAINT))
  private Project project;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Document document)) {
      return false;
    }
    return Objects.equals(getId(), document.getId())
        && Objects.equals(getProject(), document.getProject());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getProject());
  }
}
