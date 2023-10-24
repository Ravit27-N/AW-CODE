package com.innovationandtrust.project.model.dto;

import jakarta.validation.constraints.NotEmpty;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DocumentDetailRequest used for creating the document detail, client have to input all the field
 * in this class.
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DocumentDetailRequest implements Serializable {
  private Long id;

  @NotEmpty(message = "X cannot be empty.")
  private Double x;

  @NotEmpty(message = "Y cannot be empty.")
  private Double y;

  private Double width;
  private Double height;
  private String contentType;
  private String fileName;
  private String text;
  private String textAlign;
  private Integer fontSize;
  private String fontName;

  @NotEmpty(message = "Page number cannot be empty.")
  private Integer pageNum;

  @NotEmpty(message = "Type cannot be empty.")
  private String type;

  @NotEmpty(message = "Document id cannot be empty.")
  private Long documentId;

  private Long signatoryId;
}
