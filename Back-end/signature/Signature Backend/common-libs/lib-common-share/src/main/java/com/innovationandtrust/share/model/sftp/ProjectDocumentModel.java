package com.innovationandtrust.share.model.sftp;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.innovationandtrust.share.model.project.Document;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProjectDocumentModel {
  @NotEmpty private String fileName;

  @JsonView(ProjectModelView.Json.class)
  private String fullPath;

  @JsonView(ProjectModelView.Json.class)
  private String contentType;

  @JsonView(ProjectModelView.Json.class)
  private String extension;

  @JsonView(ProjectModelView.Json.class)
  private Document info;

  @JacksonXmlElementWrapper(localName = "documentDetails")
  @JacksonXmlProperty(localName = "documentDetail")
  @JsonProperty("documentDetails")
  private @Valid List<DocumentDetailModel> documentDetails;
}
