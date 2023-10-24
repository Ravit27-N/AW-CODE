package com.innovationandtrust.share.model.project;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DocumentDetail implements Serializable {

  private double x;

  private double y;

  private double width;
  private double height;

  // text or image
  private String contentType;
  // if contentType is an image
  private String fileName;
  private String text;
  private String textAlign;
  private Integer fontSize;
  private String type;
  private String fontName;
  private Integer pageNum;
  private Long signatoryId;
  private Long documentId;

}
