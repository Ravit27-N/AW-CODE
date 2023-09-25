package com.tessi.cxm.pfl.ms3.entity;

import com.tessi.cxm.pfl.ms3.util.FlowDocumentChannel;
import com.tessi.cxm.pfl.shared.model.kafka.EmailCampaignFlowDocument;
import com.tessi.cxm.pfl.shared.utils.FlowDocumentStatus;
import com.tessi.cxm.pfl.shared.utils.FlowDocumentSubChannel;
import com.tessi.cxm.pfl.shared.utils.FlowTraceabilityConstant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;
import org.hibernate.annotations.DynamicUpdate;

@Builder
@Getter
@Setter
@AllArgsConstructor
@Entity
@SequenceGenerator(
    name = "FLOW_DOCUMENT_SEQUENCE_GENERATOR",
    sequenceName = "FLOW_DOCUMENT_SEQUENCE",
    allocationSize = 1)
@Table(
    uniqueConstraints = {
      @UniqueConstraint(columnNames = {"csvLineNumber", "flow_traceability_id"})
    })
@DynamicUpdate
public class FlowDocument {
  @Id
  @GeneratedValue(
      strategy = GenerationType.SEQUENCE,
      generator = "FLOW_DOCUMENT_SEQUENCE_GENERATOR")
  private long id;

  @Size(max = 128)
  @NotEmpty
  private String document;

  private long batchNumber;
  private long pageNumber;
  private long sheetNumber;
  private String relatedItem;
  private long fileSize;

  @Size(max = 128)
  //  @NotEmpty
  private String recipient;

  @Size(max = 128)
  private String status;

  @Size(max = 128)
  @NotEmpty
  private String channel;

  @Size(max = 128)
  //  @NotEmpty
  private String subChannel;

  private Date dateStatus;

  private String createdBy;

  private Date createdAt;

  private String lastModifiedBy;

  private Date lastModified;

  private String fileId;

  @Column(columnDefinition = "INT DEFAULT 0", updatable = false)
  private int csvLineNumber;

  @Version
  @Column(nullable = false, columnDefinition = "BIGINT DEFAULT 0")
  private long version = 0L;

  @Size(max = 20, message = "The maximum length of the 'idDoc' field is 20 characters")
  @Column(length = 20)
  private String idDoc;

  private String hubIdDoc;

  @Column(name = "EXPORT_STATUS")
  @Enumerated(EnumType.STRING)
  private FlowDocumentExportStatus exportStatus = FlowDocumentExportStatus.TO_EXPORT;

  private Date unloadingDate;
  @Builder.Default
  @OneToMany(
      mappedBy = "flowDocument",
      fetch = FetchType.LAZY,
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  private Set<FlowDocumentHistory> flowDocumentHistories = new HashSet<>();

  @ManyToOne(
      fetch = FetchType.LAZY,
      cascade = {CascadeType.PERSIST})
  @JoinColumn(name = "flow_traceability_id", referencedColumnName = "id")
  private FlowTraceability flowTraceability;

  @OneToOne(
      mappedBy = "flowDocument",
      fetch = FetchType.LAZY,
      cascade = CascadeType.ALL,
      optional = false,
      orphanRemoval = true)
  private FlowDocumentDetails detail;

  @Builder.Default
  @OneToMany(
      mappedBy = "document",
      fetch = FetchType.LAZY,
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  private List<FlowDocumentNotification> notifications = new ArrayList<>();

  public FlowDocument() {
    this.detail = new FlowDocumentDetails();
    this.detail.setFlowDocument(this);
    this.flowDocumentHistories = new HashSet<>();
  }

  public FlowDocument(EmailCampaignFlowDocument document) {
    this.document = document.getDocumentName();
    this.channel = FlowDocumentChannel.DIGITAL.getValue();
    this.subChannel = FlowDocumentSubChannel.EMAIL.getValue();
    this.pageNumber = 1;
    this.sheetNumber = 1;
    this.recipient = String.join(",", document.getDestination());
    this.status = document.getStatus();
    this.dateStatus = document.getDateOfStatus();
    this.csvLineNumber = document.getLineNumber();
    this.fileId = document.getFileId();
    this.fileSize = document.getFileSize();
  }

  public void addFlowDocumentHistory(FlowDocumentHistory flowDocumentHistory) {
    flowDocumentHistories.add(flowDocumentHistory);
    flowDocumentHistory.setFlowDocument(this);
  }

  public void removeFlowDocumentHistory(FlowDocumentHistory flowDocumentHistory) {
    flowDocumentHistories.remove(flowDocumentHistory);
    flowDocumentHistory.setFlowDocument(null);
  }

  /**
   * To create notification of document.
   *
   * @param flowDocumentNotification refers to object of {@link FlowDocumentNotification}
   */
  public void addFlowDocumentNotification(FlowDocumentNotification flowDocumentNotification) {
    this.notifications.add(flowDocumentNotification);
    flowDocumentNotification.setDocument(this);
  }

  /**
   * To remove notification from document.
   *
   * @param flowDocumentNotification refers to the object of {@link FlowDocumentNotification}
   */
  public void removeFlowDocumentNotification(FlowDocumentNotification flowDocumentNotification) {
    this.notifications.remove(flowDocumentNotification);
    flowDocumentNotification.setDocument(null);
  }

  /**
   * To return value of status and status label.
   *
   * @return return {@link Map} with {@link String} key and {@link String} value.
   */
  public Map<String, String> getDocumentStatus() {
    var docStatus = FlowDocumentStatus.valueOfLabel(this.status);
    return Map.of(
        FlowTraceabilityConstant.STATUS_VALUE,
        this.status,
        FlowTraceabilityConstant.STATUS_LABEL,
        docStatus != null ? docStatus.getKey() : this.status);
  }


  /**
   * To return value of channel and channel label.
   *
   * @return return {@link Map} with {@link String} key and {@link String} value.
   */
  public Map<String, String> getDocumentChannel() {
    var statusLabel = FlowDocumentChannel.valueOfLabel(this.channel);
    return Map.of(
        FlowTraceabilityConstant.STATUS_VALUE,
        this.channel,
        FlowTraceabilityConstant.STATUS_LABEL,
        statusLabel != null ? statusLabel.getKey() : this.channel);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
      return false;
    }
    FlowDocument that = (FlowDocument) o;

    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return 483299079;
  }

  @PrePersist
  private void create() {
    if (getDateStatus() == null) {
      setDateStatus(new Date());
    }
    setCreatedAt(new Date());
  }

  @PreUpdate
  private void update() {
    setLastModified(new Date());
  }
}
