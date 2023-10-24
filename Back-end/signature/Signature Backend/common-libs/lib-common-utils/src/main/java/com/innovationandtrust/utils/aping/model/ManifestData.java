package com.innovationandtrust.utils.aping.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ManifestData {
  @JsonProperty("manifest-data")
  private Object data = new JsonEmpty();
}
