package com.tessi.cxm.pfl.ms3.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@Entity
@SequenceGenerator(
    name = "FLOW_DOCUMENT_BACKGROUND_SEQUENCE_GENERATOR",
    sequenceName = "FLOW_DOCUMENT_BACKGROUND_SEQUENCE",
    allocationSize = 1)
public class FlowDocumentBackground implements Serializable {
  @Id
  @GeneratedValue(
      strategy = GenerationType.SEQUENCE,
      generator = "FLOW_DOCUMENT_BACKGROUND_SEQUENCE_GENERATOR")
  private Long id;

  private String background;
  private String position;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "flow_document_details_id", referencedColumnName = "id")
  @JsonBackReference
  private FlowDocumentDetails flowDocumentDetails;
}
