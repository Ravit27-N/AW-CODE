package com.tessi.cxm.pfl.ms3.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@SequenceGenerator(
    name = "FLOW_DETAILS_SEQUENCE_GENERATOR",
    sequenceName = "FLOW_DETAILS_SEQUENCE",
    allocationSize = 1)
public class FlowTraceabilityDetails implements Serializable {

  @Id
  private Long id;

  @Column(columnDefinition = "INT DEFAULT 0")
  private int pageCount;

  @Column(columnDefinition = "INT DEFAULT 0")
  private int pageProcessed;

  @Column(columnDefinition = "INT DEFAULT 0")
  private int pageError;

  private Long campaignId;
  private String campaignName;
  @Version
  @Column(nullable = false, columnDefinition = "BIGINT DEFAULT 0")
  private long version = 0L;

  @MapsId
  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "id")
  @JsonBackReference
  private FlowTraceability flowTraceability;

  @Column(columnDefinition = "INT DEFAULT 0")
  private int step;

  private String composedId;

  @Column(columnDefinition = "VARCHAR DEFAULT ''", length = 20)
  private String portalDepositType;

  @Column(columnDefinition = "VARCHAR DEFAULT ''")
  private String campaignFilename;

  @Column(columnDefinition = "bool DEFAULT false")
  private boolean isValidation;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof FlowTraceabilityDetails)) {
      return false;
    }
    FlowTraceabilityDetails that = (FlowTraceabilityDetails) o;
    return getId().equals(that.getId()) && getFlowTraceability().equals(that.getFlowTraceability());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getFlowTraceability());
  }

  public String getPortalDepositType() {
    if(this.portalDepositType == null) {
      return "";
    }
    return this.portalDepositType;
  }
}
