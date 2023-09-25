package com.tessi.cxm.pfl.ms3.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.tessi.cxm.pfl.shared.utils.FlowDocumentSubChannel;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;
import java.util.Map;
import javax.validation.constraints.NotEmpty;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class FlowDocumentDto extends BaseFlowDocumentDto {

  @NotEmpty(message = "relatedItems is required and cannot be empty.")
  private String relatedItem;
  @Size(max = 128)
  @NotEmpty(message = "recipient is required and cannot be empty.")
  private String recipient;
  @Size(max = 128)
  @NotEmpty(message = "subChannel is required and cannot be empty.")
  @Schema(
      type = "array",
      enumAsRef = true,
      required = true,
      example = "CSE",
      implementation = FlowDocumentSubChannel.class)
  private String subChannel;
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Date createdAt;
  @Schema(type = "string", example = "http://cxm.tessi.fr/images")
  private String fileUrl;
  private String fileId;
  @NotEmpty(message = "server is required and cannot be empty")
  @Schema(type = "string", example = "tessi")
  private String server;
  @JsonProperty(access = Access.READ_ONLY)
  private String createdBy;
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  @Schema(
      type = "object",
      example = "{\"statusLabel\":\"flow.traceability.sub-channel.multiple\",\"status\":\"Reco\"}")
  private Map<String, String> documentStatus;
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Map<String, String> documentChannel;
  @JsonProperty(access = Access.WRITE_ONLY)
  private FlowDocumentDetailsDto details;
  @JsonProperty(access = Access.READ_ONLY)
  private String docName;
  private long ownerId;

  public FlowDocumentDto(
      long id,
      @Size(max = 128) @NotEmpty(message = "document is required and cannot be empty.")
          String document,
      long batchNumber,
      long pageNumber,
      long sheetNumber,
      long fileSize,
      @Size(max = 128) @NotEmpty(message = "channel is required and cannot be empty.")
          String channel,
      long flowTraceabilityId,
      String relatedItem,
      String recipient,
      String status,
      String subChannel,
      Date dateStatus,
      Date createdAt,
      String server,
      FlowDocumentDetailsDto details) {
    super(
        id, document, batchNumber, pageNumber, sheetNumber, fileSize, channel, flowTraceabilityId, dateStatus, createdAt, status);
    this.relatedItem = relatedItem;
    this.recipient = recipient;
    this.status = status;
    this.subChannel = subChannel;
    this.createdAt = createdAt;
    this.details = details;
    this.server = server;
  }

}
