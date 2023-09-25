package com.tessi.cxm.pfl.ms3.entity;

import com.tessi.cxm.pfl.shared.utils.FlowDocumentStatus;
import com.tessi.cxm.pfl.shared.utils.FlowTraceabilityConstant;
import java.util.Date;
import java.util.Map;
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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;
import org.springframework.util.StringUtils;

/**
 * @author Sokhour LACH
 */
@Setter
@Getter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@SequenceGenerator(
    name = "FLOW_DOCUMENT_HISTORY_SEQUENCE_GENERATOR",
    sequenceName = "FLOW_DOCUMENT_HISTORY_SEQUENCE",
    allocationSize = 1)
public class FlowDocumentHistory extends BaseHistoryEvent {

  @Id
  @GeneratedValue(
      strategy = GenerationType.SEQUENCE,
      generator = "FLOW_DOCUMENT_HISTORY_SEQUENCE_GENERATOR")
  private Long id;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "flow_document_id", referencedColumnName = "id")
  private FlowDocument flowDocument;

  public FlowDocumentHistory(Long id, String event, String server, Date dateTime) {
    super(event, server, dateTime);
    this.id = id;
  }

  public FlowDocumentHistory(final FlowDocument flowDocument, final String server) {
    super(flowDocument.getStatus(), server, flowDocument.getDateStatus());
    this.setCreatedBy(flowDocument.getCreatedBy());
  }

  public FlowDocumentHistory(final FlowDocument flowDocument, final String server, final String createdBy) {
    super(flowDocument.getStatus(), server, flowDocument.getDateStatus());
    this.setCreatedBy(createdBy);
  }

  public FlowDocumentHistory(String server, String status, String createdBy) {
    super(status, server, new Date());
    this.setCreatedBy(createdBy);
    this.setLastModifiedBy(createdBy);
  }

  public FlowDocumentHistory(String server, String status, String createdBy, String lastModifiedBy,
      Date dateTime) {
    super(status, server, dateTime);
    this.setCreatedBy(createdBy);
    this.setLastModifiedBy(lastModifiedBy);
  }

  public FlowDocumentHistory(String event, String server, Date dateTime) {
    super(event, server, dateTime);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    FlowDocumentHistory that = (FlowDocumentHistory) o;

    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), flowDocument);
  }

  @PrePersist
  private void create() {
    setCreatedAt(new Date());
    if (this.dateTime == null) {
      setDateTime(new Date());
    }
  }

  @PreUpdate
  private void update() {
    setLastModified(new Date());
  }

  /**
   * To return value of sub-status key and sub-status label.
   *
   * @return map of {@link String} keys and {@link String} values.
   */
  public Map<String, Object> getHistoryStatus() {
    if (!StringUtils.hasText(this.event)) {
      return Map.of();
    }
    var status = FlowDocumentStatus.valueOfLabel(this.event);
    return Map.of(
        FlowTraceabilityConstant.STATUS_VALUE,
        this.event,
        FlowTraceabilityConstant.STATUS_LABEL,
        status != null ? status.getKey() : this.event, FlowTraceabilityConstant.STATUS_ORDER,
        status != null ? status.getOrder() : 0);
  }
}
