package com.tessi.cxm.pfl.ms32.entity;

import com.tessi.cxm.pfl.shared.utils.FlowDocumentStatus;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

@Entity
@Getter
@Setter
@Table(
    name = "flow_document_report",
    indexes = {
        @Index(columnList = "status, createdAt"),
      @Index(columnList = "dateReception,status,subChannel"),
      @Index(columnList = "dateReception,status,subChannel,filler1,filler2,filler3,filler4,filler5")
    })
public class FlowDocumentReport extends BaseEntity {

  @Id private Long id;

  @NotNull(message = "The status field is required")
  private String status;

  @NotNull(message = "The dateStatus field is required")
  private Date dateStatus;

  private String subChannel;

  private String filler1;

  private String filler2;

  private String filler3;

  private String filler4;

  private String filler5;

  @Column(columnDefinition = "BIGINT DEFAULT 0", nullable = false)
  private Long totalPage;

  private String numReco;

  private Date dateReception;

  private Date dateSending;

  @Size(max = 20, message = "The maximum length of the 'idDoc' field is 20 characters")
  @Column(length = 20)
  private String idDoc;

  @Size(max = 128, message = "The maximum length of the 'recipient' field is 128 characters")
  @Column(length = 128)
  private String recipient;

  @ManyToOne
  @JoinColumn(name = "flow_id", referencedColumnName = "id")
  private FlowTraceabilityReport flowTraceabilityReport;

  @Builder.Default
  @OneToMany(
      mappedBy = "flowDocumentReport",
      fetch = FetchType.LAZY,
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  private Set<FlowDocumentReportHistory> flowDocumentHistories = new HashSet<>();

  public void addDocumentReportHistory(FlowDocumentReportHistory flowDocumentReportHistory) {
    flowDocumentReportHistory.setFlowDocumentReport(this);
    this.getFlowDocumentHistories().add(flowDocumentReportHistory);
  }

  public void setDateReception(Date dateReception) {
    if (dateReception == null || this.status == null) {
      return;
    }

    if (FlowDocumentStatus.IN_PROGRESS.getValue().equals(this.status)) {
      this.dateReception = dateReception;
    }
  }

  public void setDateStatus(Date dateStatus) {
    this.dateStatus = dateStatus;
    setDateReception(dateStatus);
  }

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
    FlowDocumentReport that = (FlowDocumentReport) o;
    return id != null && Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
