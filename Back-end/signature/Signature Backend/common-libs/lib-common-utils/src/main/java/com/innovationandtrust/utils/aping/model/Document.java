package com.innovationandtrust.utils.aping.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class Document {
  private final String upload;

  @JsonProperty("file-name")
  private String fileName;

  @JsonProperty("title")
  private String title = "file upload from poc";

  @JsonProperty("abstract")
  private String abstracts = "in et ex";

  @JsonProperty("manifest-data")
  private JsonEmpty manifestData = new JsonEmpty();

  @JsonProperty("user-data")
  private JsonEmpty userData = new JsonEmpty();

  public Document(String upload, String fileName) {
    this.upload = upload;
    this.fileName = fileName;
  }
}
