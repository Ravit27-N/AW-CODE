package com.innovationandtrust.utils.aping.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CaCguResponse implements Serializable {
  private String actor;
  private String authority;

  @JsonProperty("download-url")
  private String downloadUrl;

  private String session;

  private String token;
}
