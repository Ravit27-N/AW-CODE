package com.innovationandtrust.utils.tinyurl.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class DataResponse {
  private String domain;
  private String alias;

  @JsonProperty("tiny_url")
  private String tinyUrl;

  private String url;
}
