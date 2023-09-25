package com.tessi.cxm.pfl.ms3.entity;

import java.util.Date;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@SequenceGenerator(
    name = "ELEMENT_ASSOCIATION_SEQUENCE_GENERATOR",
    sequenceName = "ELEMENT_ASSOCIATION_SEQUENCE",
    allocationSize = 1)
public class ElementAssociation extends BaseEntity {
  @Id
  @GeneratedValue(
      strategy = GenerationType.SEQUENCE,
      generator = "ELEMENT_ASSOCIATION_SEQUENCE_GENERATOR")
  private Long id;

  @NotEmpty private String elementName;
  @NotEmpty private String fileId;
  private String extension;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "flow_document_id", referencedColumnName = "id")
  private FlowDocument flowDocument;

  @PrePersist
  private void create() {
    setCreatedAt(new Date());
  }

  @PreUpdate
  private void update() {
    setLastModified(new Date());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ElementAssociation)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    ElementAssociation that = (ElementAssociation) o;
    return getId().equals(that.getId()) && getFlowDocument().equals(that.getFlowDocument());
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), getId(), getFlowDocument());
  }
}
