package com.innovationandtrust.share.model.tdc;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TdcJsonFile {
  @JsonProperty(value = "@class")
  private String className = "net.docubase.toolkit.model.document.Document";

  private String filename;
  private String extension;
  private String baseId;
  private List<TdcCriterionModel> criterions;
}
