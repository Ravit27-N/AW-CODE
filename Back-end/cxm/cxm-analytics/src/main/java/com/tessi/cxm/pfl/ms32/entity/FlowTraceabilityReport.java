package com.tessi.cxm.pfl.ms32.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.ToString.Exclude;
import org.hibernate.Hibernate;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Table(
    name = "flow_traceability_report",
    indexes = {
        @Index(columnList = "ownerId,depositMode,createdAt"),
        @Index(columnList = "ownerId, subChannel"),
        @Index(columnList = "ownerId, channel"),
        @Index(columnList = "ownerId")
    })
public class FlowTraceabilityReport extends BaseEntity {

  @Id
  private Long id;

  @NotNull(message = "The ownerId is required")
  private Long ownerId;

  @NotNull(message = "The depositMode field is required")
  private String depositMode;

  @NotNull(message = "The channel field is required")
  private String channel;

  @NotNull(message = "The subChannel field is required")
  private String subChannel;

  @NotNull(message = "The createdBy field is required")
  private String createdBy;

  @NotNull(message = "The depositDate field is required")
  private Date depositDate;

  @OneToMany(
      mappedBy = "flowTraceabilityReport",
      fetch = FetchType.LAZY,
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @Exclude
  private Set<FlowDocumentReport> flowDocumentReports = new HashSet<>();

  @PrePersist
  public void initCreatedAt() {
    this.setCreatedAt(new Date());
  }

  @PreUpdate
  public void initModifiedAt() {
    this.setModifiedAt(new Date());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    FlowTraceabilityReport that = (FlowTraceabilityReport) o;
    return id != null && Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }

  public void addDocumentReport(FlowDocumentReport flowDocumentReport) {
    flowDocumentReport.setFlowTraceabilityReport(this);
    this.getFlowDocumentReports().add(flowDocumentReport);
  }
}
