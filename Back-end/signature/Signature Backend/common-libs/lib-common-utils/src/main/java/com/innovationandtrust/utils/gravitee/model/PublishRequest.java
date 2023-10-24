package com.innovationandtrust.utils.gravitee.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PublishRequest {
  private String version;
  private String description;
  private String visibility;
  private String name;

  @JsonProperty("lifecycle_state")
  private String lifeCycleState;

  private List<Resource> resources;
  private Proxy proxy;
}
