package com.innovationandtrust.utils.aping.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
@AllArgsConstructor
public class ScenarioStep {
  private String process;
  private String cardinality;
  private int signatureType;
  @JsonProperty("steps")
  @NotEmpty private List<String> actorUrls;

  public ScenarioStep(String process, String cardinality, List<String> actorUrls) {
    this.process = process;
    this.cardinality = cardinality;
    this.actorUrls = actorUrls;
    this.signatureType = 0;
  }
}
