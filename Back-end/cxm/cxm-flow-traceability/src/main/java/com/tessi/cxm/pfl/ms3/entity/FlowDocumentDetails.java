package com.tessi.cxm.pfl.ms3.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.tessi.cxm.pfl.shared.utils.JavaTypeConstants;
import com.vladmihalcea.hibernate.type.array.StringArrayType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@TypeDef(
    name = JavaTypeConstants.STRING_ARRAY, typeClass = StringArrayType.class
)
@DynamicUpdate
public class FlowDocumentDetails implements Serializable {
  @Id private long id;

  // Recipients
  private String address;
  private String email;
  private String telephone;

  // Extraction of data
  private String reference;

  @Type(type = JavaTypeConstants.STRING_ARRAY)
  @Column(columnDefinition = "text[]")
  private String[] fillers;

  // Production criteria
  private String archiving;
  private String addition;
  private String color;
  private String envelope;
  private String impression;
  private String postage;
  private String watermark;
  private String signature;
  private String postalPickup;
  private String docName;

  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @MapsId
  @JoinColumn(name = "id")
  private FlowDocument flowDocument;

  @OneToMany(
      mappedBy = "flowDocumentDetails",
      fetch = FetchType.LAZY,
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  @JsonManagedReference
  private List<FlowDocumentBackground> flowDocumentBackgrounds = new ArrayList<>();

  @OneToMany(
      mappedBy = "flowDocumentDetails",
      fetch = FetchType.LAZY,
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  @JsonManagedReference
  private List<FlowDocumentAttachment> flowDocumentAttachments = new ArrayList<>();

  public void setFlowDocumentAttachments(List<FlowDocumentAttachment> flowDocumentAttachments) {
    this.flowDocumentAttachments.clear();
    this.flowDocumentAttachments.addAll(flowDocumentAttachments);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof FlowDocumentDetails)) {
      return false;
    }
    FlowDocumentDetails that = (FlowDocumentDetails) o;
    return getId() == that.getId() && getFlowDocument().equals(that.getFlowDocument());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getFlowDocument());
  }
}
