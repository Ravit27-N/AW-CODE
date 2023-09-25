package com.tessi.cxm.pfl.ms3.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.tessi.cxm.pfl.ms3.util.FlowDocumentChannel;
import com.tessi.cxm.pfl.shared.utils.FlowDocumentStatus;
import com.tessi.cxm.pfl.shared.utils.FlowTraceabilityConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BaseFlowDocumentDto {
  protected long id;

  @Size(max = 128)
  @NotEmpty(message = "document is required and cannot be empty.")
  protected String document;

  protected long batchNumber;
  protected long pageNumber;
  protected long sheetNumber;
  protected long fileSize;

  @Size(max = 128)
  @NotEmpty(message = "channel is required and cannot be empty.")
  @Schema(
      type = "array",
      enumAsRef = true,
      required = true,
      example = "Digital",
      implementation = FlowDocumentChannel.class)
  protected String channel;

  @NotNull(message = "flowTraceabilityId is required and cannot be null.")
  @Schema(type = "int", format = "int64", example = "1")
  protected long flowTraceabilityId;

  @JsonProperty(access = Access.READ_ONLY)
  protected Date dateStatus;

  @JsonProperty(access = Access.READ_ONLY)
  protected Date createdAt;

  @Size(max = 128)
  @NotEmpty(message = "recipient is required and cannot be empty.")
  @Schema(
      type = "array",
      enumAsRef = true,
      required = true,
      example = "In progress",
      implementation = FlowDocumentStatus.class)
  protected String status;

  /**
   * To return value of channel and channel label.
   *
   * @return return {@link Map} with {@link String} key and {@link String} value.
   */
  @JsonProperty(access = Access.READ_ONLY)
  public Map<String, String> getDocumentChannel() {
    if (!StringUtils.isEmpty(this.channel)) {
      var statusLabel = FlowDocumentChannel.valueOfLabel(this.channel);
      return Map.of(
          FlowTraceabilityConstant.STATUS_VALUE,
          this.channel,
          FlowTraceabilityConstant.STATUS_LABEL,
          statusLabel != null ? statusLabel.getKey() : this.channel);
    }
    return new HashMap<>();
  }
}
