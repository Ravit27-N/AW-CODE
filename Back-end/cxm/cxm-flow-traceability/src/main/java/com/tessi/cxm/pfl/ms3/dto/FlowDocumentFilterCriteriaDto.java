package com.tessi.cxm.pfl.ms3.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Define the criteria of document filtering.
 * 
 * @author Sokhour LACH
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class FlowDocumentFilterCriteriaDto {
  @Schema(
      type = "object",
      example = "[{\"key\":\"flow.document.channel.digital\",\"value\":\"Digital\"}]")
  private List<Map<String, Object>> sendingChannel = new ArrayList<>();
  @Schema(
      type = "object",
      example = "[{\n"
          + " \"value\": \"In progress\",\n" 
          + " \"key\": \"flow.document.status.in_progress\"\n" 
          + "  }]")
  private List<Map<String, String>> flowDocumentStatus = new ArrayList<>();
  private List<String> sendingSubChannel = new ArrayList<>();
}
