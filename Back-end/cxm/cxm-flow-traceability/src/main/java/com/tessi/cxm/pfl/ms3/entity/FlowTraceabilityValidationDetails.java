package com.tessi.cxm.pfl.ms3.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@SequenceGenerator(
    name = "FLOW_TRACEABILITY_VALIDATION_DETAILS_SEQUENCE_GENERATOR",
    sequenceName = "FLOW_TRACEABILITY_VALIDATION_DETAILS_SEQUENCE",
    allocationSize = 1)
public class FlowTraceabilityValidationDetails implements Serializable {

  @Id
  private Long id;
  private long totalDocumentValidation;
  private long totalDocumentError;
  private long totalDocumentRefused;
  private long totalDocument;
  @Column(columnDefinition = "int default 0")
  private long totalRemaining;

  @MapsId
  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "id")
  @JsonBackReference
  private FlowTraceability flowTraceability;

  @Override
  public String toString() {
    return "FlowTraceabilityValidationDetails{"
        + "id=" + id
        + ", totalDocumentValidation=" + totalDocumentValidation
        + ", totalDocumentError=" + totalDocumentError
        + ", totalDocumentRefused=" + totalDocumentRefused
        + ", totalDocument=" + totalDocument
        + ", totalRemaining=" + totalRemaining
        + '}';
  }
}
