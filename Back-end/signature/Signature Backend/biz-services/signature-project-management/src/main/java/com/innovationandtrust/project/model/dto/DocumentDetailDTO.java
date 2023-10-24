package com.innovationandtrust.project.model.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** DocumentDetailDTO used for response to client when they request for it. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DocumentDetailDTO implements Serializable {
  private Long id;
  private Double x;
  private Double y;
  private Double width;
  private Double height;
  private Integer pageNum;
  private String type;

  private String contentType;
  private String fileName;
  private String text;
  private String textAlign;
  private Integer fontSize;
  private String fontName;
  private Long documentId;
  private Long signatoryId;
}
