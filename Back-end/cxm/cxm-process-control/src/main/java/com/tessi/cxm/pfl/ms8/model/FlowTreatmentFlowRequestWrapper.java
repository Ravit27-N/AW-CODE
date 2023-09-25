package com.tessi.cxm.pfl.ms8.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FlowTreatmentFlowRequestWrapper implements Serializable {

  @NotEmpty
  private String composedFileId;
  @NotEmpty
  private String idCreator;
  @NotEmpty
  private String uuid;
  @NotNull
  private Production production;
  private boolean validation;

  @Getter
  @Setter
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Production implements Serializable {

    @JsonProperty("Urgency")
    private String urgency;

    @JsonProperty("Color")
    private String color;

    @JsonProperty("Recto")
    private String recto;

    @JsonProperty("Archiving")
    private String archiving;
  }
}
