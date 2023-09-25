package com.tessi.cxm.pfl.ms3.entity;

import java.util.Date;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "flow_campaign_detail")
public class FlowCampaignDetail extends BaseEntity {
  @Id private Long id;

  @Column(columnDefinition = "TEXT default ''")
  private String htmlTemplate;

  // Total recode of csv file
  @Column(columnDefinition = "INT default 0")
  private long totalRecord;
  // Erreurs
  @Column(columnDefinition = "INT default 0")
  private long totalError;

  // Délivrés
  @Column(columnDefinition = "INT default 0")
  private long totalDelivered;

  // Ouverts
  @Column(columnDefinition = "INT default 0")
  private long totalOpened;
  // Cliqués
  @Column(columnDefinition = "INT default 0")
  private long totalClicked;
  // Bounces
  @Column(columnDefinition = "INT default 0")
  private long totalBounce;

  @Column(columnDefinition = "INT default 0")
  private long totalBlock;

  @Column(columnDefinition = "INT default 0")
  private long totalCancel;

  @Column(columnDefinition = "INT default 0")
  private long temporaryError;

  @Column(columnDefinition = "INT default 0")
  private long permanentError;

  // Renvoi
  @Column(columnDefinition = "INT default 0")
  private long totalResent;

  @Column(columnDefinition = "INT default 0")
  private long totalCanceled;

  private long campaignId;

  @NotEmpty(message = "campaignName field is required and cannot be null")
  @Column(nullable = false, columnDefinition = "varchar(255) default ''")
  private String campaignName;

  private String campaignType;

  public FlowCampaignDetail(com.tessi.cxm.pfl.shared.model.kafka.FlowCampaignDetail detail) {
    this.campaignId = detail.getCampaignId();
    this.campaignName = detail.getCampaignName();
    this.htmlTemplate = detail.getCampaignTemplate();
    this.totalRecord = detail.getTotalCsvRecord();
    this.setCreatedBy(detail.getCreatedBy());
    this.campaignType = detail.getType();
  }

  @MapsId
  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "id", foreignKey = @ForeignKey(name = "flow_campaign_detail"))
  private FlowTraceability flowTraceability;

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
    if (!(o instanceof FlowCampaignDetail)) {
      return false;
    }
    FlowCampaignDetail that = (FlowCampaignDetail) o;
    return getId().equals(that.getId()) && getFlowTraceability().equals(that.getFlowTraceability());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getFlowTraceability());
  }
}
