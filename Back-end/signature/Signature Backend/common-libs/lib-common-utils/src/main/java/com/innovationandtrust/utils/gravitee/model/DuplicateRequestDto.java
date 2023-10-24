package com.innovationandtrust.utils.gravitee.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class DuplicateRequestDto {
  @JsonProperty("context_path")
  private String contextPath;

  private String version;

  @JsonProperty("filtered_fields")
  private List<String> filteredFields = new ArrayList<>();

  public DuplicateRequestDto(String contextPath, String version) {
    this.contextPath = contextPath;
    this.version = version;
  }
}
