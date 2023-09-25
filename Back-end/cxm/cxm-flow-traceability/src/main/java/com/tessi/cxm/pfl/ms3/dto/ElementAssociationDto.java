package com.tessi.cxm.pfl.ms3.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.tessi.cxm.pfl.ms3.util.ElementAssociation;
import com.tessi.cxm.pfl.shared.utils.FlowTraceabilityConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Map;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@ToString
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class ElementAssociationDto implements Serializable {
  private long id;

  @Size(max = 128)
  @NotEmpty(message = "elementName is required and cannot be empty.")
  @Schema(type = "string", name = "elementName", example = "Bordereau")
  private String elementName;

  @NotEmpty(message = "fileUrl is required and cannot be empty.")
  @Schema(type = "string", name = "fileUrl", example = "http://cxm-tessi.fr")
  private String fileId;

  private String extension;

  @NotNull(message = "FlowDocumentId is required and cannot be null.")
  @Schema(type = "int", format = "int64", example = "1")
  private long flowDocumentId;

  @JsonProperty(access = Access.READ_ONLY)
  public Map<String, String> getElement() {
    var element = ElementAssociation.valueOfLabel(elementName);
    return Map.of(
        FlowTraceabilityConstant.OBJECT_KEY,
        element != null ? element.getKey() : this.elementName,
        FlowTraceabilityConstant.OBJECT_VALUE,
        elementName);
  }
}
