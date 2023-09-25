package com.tessi.cxm.pfl.ms3.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.tessi.cxm.pfl.ms3.util.FlowDocumentChannel;
import com.tessi.cxm.pfl.shared.model.ProcessingResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Flow document detail.
 *
 * @author Sokhour LACH
 * @since 04/11/2021
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FlowDocumentDetailsDto implements Serializable {
  private long id;

  // flow traceability
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private String flowName;

  // Recipients
  private String address;
  private String email;
  private String campaignName;
  private String telephone;
  private String subChannel;

  // Extraction of data
  private String reference;
  private List<FlowDocumentFiller> fillers;

  private String docName;

  private ProcessingResponse enrichment;

  // Production criteria is separated by sending channel of document
  // channels have postal and digital.
  @JsonProperty(access = Access.WRITE_ONLY)
  @Schema(type = "string", example = "Non")
  private String archiving = "Non"; // both

  @JsonProperty(access = Access.WRITE_ONLY)
  @Schema(type = "string", example = "Aucunes")
  private String addition = "Aucunes"; // both

  @JsonProperty(access = Access.WRITE_ONLY)
  @Schema(type = "string", example = "Mail")
  private String postage = "Mail"; // both

  @JsonProperty(access = Access.WRITE_ONLY)
  @Schema(type = "string", example = "Aucun")
  private String watermark = "Aucun"; // both

  @JsonProperty(access = Access.WRITE_ONLY)
  @Schema(type = "string", example = "Monochrome")
  private String color; // postal

  @JsonProperty(access = Access.WRITE_ONLY)
  @Schema(type = "string", example = "C5")
  private String envelope; // postal

  @JsonProperty(access = Access.WRITE_ONLY)
  @Schema(type = "string", example = "Recto")
  private String impression; // postal

  @JsonProperty(access = Access.WRITE_ONLY)
  @Schema(type = "string", example = "Non")
  private String postalPickup = "Non"; // digital

  @JsonProperty(access = Access.READ_ONLY)
  private Map<String, String> productCriteria = new HashMap<>();

  @JsonProperty(access = Access.READ_ONLY)
  private String createdBy;

  @JsonProperty(access = Access.READ_ONLY)
  private Long ownerId;

  private String modelName;

  public String getArchiving() {
    return Objects.requireNonNullElse(this.archiving, "");
  }

  public void setArchiving(String archiving) {
    this.archiving = Objects.requireNonNullElse(archiving, "");
  }

  public String getAddition() {
    return Objects.requireNonNullElse(this.addition, "");
  }

  public void setAddition(String addition) {
    this.addition = Objects.requireNonNullElse(addition, "");
  }

  public String getPostage() {
    return Objects.requireNonNullElse(this.postage, "");
  }

  public void setPostage(String postage) {
    this.postage = Objects.requireNonNullElse(postage, "");
  }

  public String getWatermark() {
    return Objects.requireNonNullElse(this.watermark, "");
  }

  public void setWatermark(String watermark) {
    this.watermark = Objects.requireNonNullElse(watermark, "");
  }

  // Note:  Impact from Lombok @Builder.
  // When use with Lombok @Builder, all the setter are not used anymore.
  // Use getter method to instead of field if that property is required non-null value.

  public String getColor() {
    return Objects.requireNonNullElse(this.color, "");
  }

  public void setColor(String color) {
    this.color = Objects.requireNonNullElse(color, "");
  }

  public String getEnvelope() {
    return Objects.requireNonNullElse(this.envelope, "");
  }

  public void setEnvelope(String envelope) {
    this.envelope = Objects.requireNonNullElse(envelope, "");
  }

  public String getImpression() {
    return Objects.requireNonNullElse(this.impression, "");
  }

  public void setImpression(String impression) {
    this.impression = Objects.requireNonNullElse(impression, "");
  }

  public String getPostalPickup() {
    return Objects.requireNonNullElse(this.postalPickup, "");
  }

  public void setPostalPickup(String postalPickup) {
    this.postalPickup = Objects.requireNonNullElse(postalPickup, "");
  }

  public void setProductCriteria(String channel) {
    if (channel.equalsIgnoreCase(FlowDocumentChannel.POSTAL.getValue())) {
      this.productCriteria =
          Map.of(
              "archiving",
              this.getArchiving(),
              "addition",
              this.getAddition(),
              "postage",
              this.getPostage(),
              "pageBackground",
              this.getWatermark(),
              "color",
              this.getColor(),
              "envelope",
              this.getEnvelope(),
              "impression",
              this.getImpression());
    } else if (channel.equalsIgnoreCase(FlowDocumentChannel.DIGITAL.getValue())) {
      this.productCriteria =
          Map.of(
              "archiving",
              this.getArchiving(),
              "addition",
              this.getAddition(),
              "postage",
              this.getPostage(),
              "pageBackground",
              this.getWatermark(),
              "postalPickup",
              this.getPostage());
    }
  }
}
