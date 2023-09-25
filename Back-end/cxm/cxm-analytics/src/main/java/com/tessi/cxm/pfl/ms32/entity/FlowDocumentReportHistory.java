package com.tessi.cxm.pfl.ms32.entity;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(indexes = {@Index(columnList = "status")})
public class FlowDocumentReportHistory extends BaseEntity {
  @Id private Long id;

  @NotEmpty(message = "The status field is required")
  @Column(nullable = false)
  private String status;

  @NotNull(message = "The dateStatus field is required")
  private Date dateStatus;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "flow_document_report_id", referencedColumnName = "id")
  private FlowDocumentReport flowDocumentReport;
}
