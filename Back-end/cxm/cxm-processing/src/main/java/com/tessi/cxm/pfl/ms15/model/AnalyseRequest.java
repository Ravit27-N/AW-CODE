package com.tessi.cxm.pfl.ms15.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tessi.cxm.pfl.shared.filectrl.model.portal.Attachments;
import java.io.Serializable;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.tessi.cxm.pfl.shared.filectrl.model.portal.BackgroundPage;
import com.tessi.cxm.pfl.shared.filectrl.model.portal.FiligraneDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AnalyseRequest implements Serializable {
  private Integer nbExtract;

  private Integer nbSplit;
  private String forcedModel;
  @NotNull private String simpleFile;
  private String outputDir;
  @NotNull private String configFile;
  @NotNull private String fileName;
  @NotNull private String configName;

  @NotNull private BackgroundPage background;
  @JsonProperty("attachmentDto")
  @NotNull private Attachments attachmentDto;
  private FiligraneDto filigrane;

  private String signature;
}
