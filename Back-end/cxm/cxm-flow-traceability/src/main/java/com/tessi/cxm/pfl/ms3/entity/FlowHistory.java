package com.tessi.cxm.pfl.ms3.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.tessi.cxm.pfl.shared.model.kafka.BaseUpdateFlowFromProcessCtrl;
import com.tessi.cxm.pfl.shared.utils.FlowHistoryStatus;
import com.tessi.cxm.pfl.shared.utils.FlowTraceabilityConstant;
import com.tessi.cxm.pfl.shared.utils.FlowTraceabilityStatus;
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

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@Entity
@SequenceGenerator(
    name = "FLOW_HISTORY_SEQUENCE_GENERATOR",
    sequenceName = "FLOW_HISTORY_SEQUENCE",
    allocationSize = 1)
public class FlowHistory extends BaseHistoryEvent {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FLOW_HISTORY_SEQUENCE_GENERATOR")
  private Long id;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "flow_traceability_id", referencedColumnName = "id")
  @JsonBackReference
  private FlowTraceability flowTraceability;

  public FlowHistory(String event, String service, Date dateTime) {
    super(event, service, dateTime);
  }

  public FlowHistory(final FlowTraceability traceability, final String server) {
    super(
        FlowTraceabilityStatus.valueOfLabel(traceability.getStatus()).getFlowHistoryStatus(),
        server,
        traceability.getDateStatus());
  }

  public FlowHistory(String server, String event, String createdBy, Date dateTime) {
    super(event, server, dateTime);
    this.setCreatedBy(createdBy);
  }

  public FlowHistory(String server, String event, String createdBy, String lastModifiedBy) {
    super(event, server, new Date());
    this.setCreatedBy(createdBy);
    this.setLastModifiedBy(lastModifiedBy);
  }

  public FlowHistory(BaseUpdateFlowFromProcessCtrl baseUpdate, Date dateTime) {
    this(
        baseUpdate.getServer(),
        FlowTraceabilityStatus.valueOfLabel(baseUpdate.getStatus()).getFlowHistoryStatus(),
        baseUpdate.getCreatedBy(),
        dateTime);
  }

  /**
   * To return value of status and status label.
   *
   * @return
   */
  public Map<String, Object> getHistoryStatus() {
    var historyStatus = FlowHistoryStatus.valueOfLabel(this.event);
    return Map.of(
        FlowTraceabilityConstant.STATUS_VALUE,
        this.event,
        FlowTraceabilityConstant.STATUS_LABEL,
        historyStatus != null ? historyStatus.getKey() : this.event,
        FlowTraceabilityConstant.STATUS_ORDER,
        historyStatus != null ? historyStatus.getOrder() : 0);
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof FlowHistory)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    FlowHistory that = (FlowHistory) o;
    return getId().equals(that.getId()) && getFlowTraceability().equals(that.getFlowTraceability());
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), getId(), getFlowTraceability());
  }
}
