package com.tessi.cxm.pfl.ms3.entity;

import com.cxm.tessi.pfl.shared.flowtreatment.constant.PortalDepositType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.tessi.cxm.pfl.ms3.util.Channel;
import com.tessi.cxm.pfl.ms3.util.DepositMode;
import com.tessi.cxm.pfl.ms3.util.SubChannel;
import com.tessi.cxm.pfl.shared.config.kafka.EmailCampaignFlowCreationAdapter;
import com.tessi.cxm.pfl.shared.model.kafka.CampaignFlowCreatedModel;
import com.tessi.cxm.pfl.shared.utils.FlowTraceabilityConstant;
import com.tessi.cxm.pfl.shared.utils.FlowTraceabilityStatus;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.util.StringUtils;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@SequenceGenerator(
    name = "FLOW_TRACEABILITY_SEQUENCE_GENERATOR",
    sequenceName = "FLOW_TRACEABILITY_SEQUENCE",
    allocationSize = 1)
@DynamicUpdate
public class FlowTraceability implements EmailCampaignFlowCreationAdapter<FlowTraceability> {

  @Id
  @GeneratedValue(
      strategy = GenerationType.SEQUENCE,
      generator = "FLOW_TRACEABILITY_SEQUENCE_GENERATOR")
  private Long id;

  private Date depositDate;

  private Date unloadingDate;

  @Size(max = 128)
  @NotEmpty
  private String depositMode;

  @Size(max = 128)
  @NotEmpty
  private String flowName;

  @Size(max = 128)
  @NotNull
  private String channel;

  @Size(max = 128)
  private String subChannel;

  private String status;

  private String fileId;

  private Date dateStatus;

  @Size(max = 128)
  private String fullName;

  @Column(nullable = false)
  private Long ownerId;

  private String createdBy;

  private Date createdAt;

  private String lastModifiedBy;

  private Date lastModified;

  private String modelName;

  @OneToMany(
      mappedBy = "flowTraceability",
      fetch = FetchType.LAZY,
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  @JsonManagedReference
  private Set<FlowHistory> flowHistories = new HashSet<>();

  @OneToOne(mappedBy = "flowTraceability", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference
  private FlowTraceabilityDetails flowTraceabilityDetails = new FlowTraceabilityDetails();

  @OneToOne(
      mappedBy = "flowTraceability",
      fetch = FetchType.LAZY,
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  @JsonManagedReference
  private FlowTraceabilityValidationDetails flowTraceabilityValidationDetails =
      new FlowTraceabilityValidationDetails();

  public FlowTraceability(CampaignFlowCreatedModel model) {
    this.dateStatus = model.getDateOfStatus();
    this.depositDate = model.getDateOfCreation();
    this.flowName = model.getCampaignName();
    this.setCreatedBy(model.getCreatedBy());
    this.setFullName(model.getFullName());
    this.status = FlowTraceabilityStatus.SCHEDULED.getValue();
    this.subChannel = "email";
    this.channel = Channel.DIGITAL.getValue();
    this.depositMode = DepositMode.PORTAL.getValue();
    this.flowTraceabilityDetails.setPortalDepositType(PortalDepositType.CAMPAIGN_EMAIL.name());
    this.flowTraceabilityDetails.setCampaignFilename(model.getCsvName());
    this.ownerId = model.getOwnerId();
  }

  public void addFlowHistory(FlowHistory flowHistory) {
    this.flowHistories.add(flowHistory);
    flowHistory.setFlowTraceability(this);
  }

  // To create new flow details.
  public void addFlowTraceabilityDetails(FlowTraceabilityDetails flowTraceabilityDetails) {
    this.flowTraceabilityDetails = flowTraceabilityDetails;
    flowTraceabilityDetails.setFlowTraceability(this);
  }

  public void addValidationDetails(
      FlowTraceabilityValidationDetails flowTraceabilityValidationDetails) {
    this.flowTraceabilityValidationDetails = flowTraceabilityValidationDetails;
    flowTraceabilityValidationDetails.setFlowTraceability(this);
  }

  public void removeFlowHistory(FlowHistory flowHistory) {
    this.flowHistories.remove(flowHistory);
    flowHistory.setFlowTraceability(null);
  }

  /**
   * To return value of status and status label.
   *
   * @return return {@link Map<String, String>}
   */
  public Map<String, String> getFlowStatus() {
    if (this.status == null) {
      return Map.of();
    }
    var flowStatus = FlowTraceabilityStatus.valueOfLabel(this.status);
    return Map.of(
        FlowTraceabilityConstant.STATUS_VALUE,
        this.status,
        FlowTraceabilityConstant.STATUS_LABEL,
        flowStatus == null ? this.status : flowStatus.getKey());
  }

  /**
   * To return value of channel and channel label.
   *
   * @return return {@link Map<String, String>}
   */
  public Map<String, String> getFlowChannel() {
    return Map.of(
        FlowTraceabilityConstant.STATUS_VALUE,
        this.channel,
        FlowTraceabilityConstant.STATUS_LABEL,
        Channel.valueOfLabel(this.channel).getKey());
  }

  /**
   * To return value of sub-channel and sub-channel label.
   *
   * @return return {@link Map<String, String>}
   */
  public Map<String, String> getFlowSubChannel() {
    var subChannelEnum = SubChannel.valueOfLabel(this.subChannel.trim());
    return Map.of(
        FlowTraceabilityConstant.STATUS_VALUE,
        this.subChannel,
        FlowTraceabilityConstant.STATUS_LABEL,
        subChannelEnum != null ? subChannelEnum.getKey() : SubChannel.NONE.getKey());
  }

  /**
   * To return value of depositMode and depositMode label.
   *
   * @return return {@link Map<String, String>}
   */
  public Map<String, String> getFlowDepositMode() {
    if (StringUtils.hasText(this.depositMode)) {
      return Map.of(
          FlowTraceabilityConstant.STATUS_VALUE,
          this.depositMode,
          FlowTraceabilityConstant.STATUS_LABEL,
          DepositMode.valueOfLabel(this.depositMode).getKey());
    }
    return Map.of();
  }

  @PrePersist
  private void create() {
    if (this.dateStatus == null) {
      setDateStatus(new Date());
    }
    setDepositDate(new Date());
  }

  @PreUpdate
  private void update() {
    setLastModified(new Date());
  }

  @JsonIgnore
  @Override
  public FlowTraceability getEmailCampaignFlow() {
    return this;
  }

  @JsonIgnore
  @Override
  public CampaignFlowCreatedModel getCampaignFlowCreatedModel() {
    var detail = this.flowTraceabilityDetails;
    CampaignFlowCreatedModel campaignFlowCreatedModel =
        new CampaignFlowCreatedModel(
            detail != null ? detail.getCampaignId() : null,
            this.flowName,
            this.dateStatus,
            this.depositDate,
            this.flowName,
            this.getCreatedBy(),
            this.status,
            this.ownerId);
    campaignFlowCreatedModel.setFullName(Objects.requireNonNullElse(this.getFullName(), ""));

    return campaignFlowCreatedModel;
  }

  public String getDepositType() {
    if (this.flowTraceabilityDetails != null
        && StringUtils.hasText(this.flowTraceabilityDetails.getPortalDepositType())) {
      return PortalDepositType.valueOf(
              this.flowTraceabilityDetails.getPortalDepositType().toUpperCase(Locale.ROOT))
          .name();
    }
    return "";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof FlowTraceability)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    FlowTraceability that = (FlowTraceability) o;
    return getId().equals(that.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), getId());
  }
}
