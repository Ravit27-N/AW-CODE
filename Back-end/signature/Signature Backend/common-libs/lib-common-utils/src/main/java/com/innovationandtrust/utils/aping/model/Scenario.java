package com.innovationandtrust.utils.aping.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class Scenario {
  @JsonProperty("documents")
  private List<String> docUrls;

  private List<@Valid ScenarioStep> steps;
  private int format;
  private int level;
}
