package com.tessi.cxm.pfl.ms3.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@SequenceGenerator(
    name = "FLOW_DOCUMENT_NOTIFICATION_SEQUENCE_GENERATOR",
    sequenceName = "FLOW_DOCUMENT_NOTIFICATION_SEQUENCE",
    allocationSize = 1)
@Table(
    uniqueConstraints = {
      @UniqueConstraint(
          name = "unique_document_notification_step",
          columnNames = {"document_id", "step"})
    })
public class FlowDocumentNotification implements Serializable {

  @Id
  @GeneratedValue(
      strategy = GenerationType.SEQUENCE,
      generator = "FLOW_DOCUMENT_NOTIFICATION_SEQUENCE_GENERATOR")
  private long id;

  private String step;

  private Date date;

  private String costReal;
  private String weightReal;
  private String stampReal;
  private String numReco;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "document_id",
      referencedColumnName = "id",
      foreignKey = @ForeignKey(name = "fk_flow_document_notification"))
  private FlowDocument document;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof FlowDocumentNotification)) {
      return false;
    }
    FlowDocumentNotification that = (FlowDocumentNotification) o;
    return getId() == that.getId() && getDocument().equals(that.getDocument());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getDocument());
  }
}
