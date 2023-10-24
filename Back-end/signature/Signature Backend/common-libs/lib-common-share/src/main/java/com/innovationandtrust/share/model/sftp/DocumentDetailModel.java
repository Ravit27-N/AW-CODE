package com.innovationandtrust.share.model.sftp;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class DocumentDetailModel {
  private Double x;
  private Double y;

  @JsonView(ProjectModelView.Json.class)
  private Double width;

  @JsonView(ProjectModelView.Json.class)
  private Double height;

  @Min(1)
  private Integer pageNum;

  private String type;
  private String signatoryEmail = "";

  @JsonView(ProjectModelView.Json.class)
  private String textAlign;

  @JsonView(ProjectModelView.Json.class)
  private String text;

  @JsonView(ProjectModelView.Json.class)
  private Integer fontSize;

  @JsonView(ProjectModelView.Json.class)
  private String contentType;
}
