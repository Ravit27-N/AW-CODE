package com.innovationandtrust.project.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.io.Serializable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DocumentDetail is a class that is representing a documentDetail table in the database, and it has
 * a relationship with a document, and signatory.
 */
@Entity
@Table(name = "document_details", indexes = @Index(columnList = "signatory_id, type"))
@Getter
@Setter
@NoArgsConstructor
@org.springframework.data.relational.core.mapping.Table
public class DocumentDetail implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Double x;
  private Double y;
  private Double width;
  private Double height;

  // text or image
  @Column(name = "content_type")
  private String contentType;

  @Column(name = "file_name")
  private String fileName;

  private String text;

  @Column(name = "text_align")
  private String textAlign;

  @Column(name = "font_size")
  private Integer fontSize;

  @Column(name = "page_num")
  private Integer pageNum;

  // approval or signature
  private String type;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "document_id",
      referencedColumnName = "id",
      foreignKey = @ForeignKey(name = "fk_document_detail", value = ConstraintMode.CONSTRAINT))
  private Document document;

  @Column(name = "signatory_id")
  private Long signatoryId;
}
