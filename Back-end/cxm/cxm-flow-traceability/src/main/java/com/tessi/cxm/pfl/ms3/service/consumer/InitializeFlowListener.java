package com.tessi.cxm.pfl.ms3.service.consumer;

import com.cxm.tessi.pfl.shared.flowtreatment.constant.PortalDepositType;
import com.tessi.cxm.pfl.ms3.entity.FlowCampaignDetail;
import com.tessi.cxm.pfl.ms3.entity.FlowDeposit;
import com.tessi.cxm.pfl.ms3.entity.FlowHistory;
import com.tessi.cxm.pfl.ms3.entity.FlowTraceability;
import com.tessi.cxm.pfl.ms3.entity.FlowTraceabilityDetails;
import com.tessi.cxm.pfl.ms3.entity.FlowTraceabilityValidationDetails;
import com.tessi.cxm.pfl.ms3.repository.FlowCampaignDetailRepository;
import com.tessi.cxm.pfl.ms3.repository.FlowDepositRepository;
import com.tessi.cxm.pfl.ms3.repository.FlowTraceabilityRepository;
import com.tessi.cxm.pfl.shared.model.kafka.FlowFileControlCreateFlowTraceabilityModel;
import com.tessi.cxm.pfl.shared.utils.FlowTraceabilityStatusConstant;
import java.sql.SQLException;
import java.util.Date;
import java.util.function.Consumer;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * Reference message topic to {@link com.tessi.cxm.pfl.shared.utils.KafkaUtils#INITIAL_FLOW_TRACEABILITY_TOPIC}.
 */
@Slf4j
@Component("initializeFlowListener")
@RequiredArgsConstructor
public class InitializeFlowListener
    implements Consumer<FlowFileControlCreateFlowTraceabilityModel> {

  private final FlowTraceabilityRepository flowTraceabilityRepository;
  private final FlowCampaignDetailRepository flowCampaignDetailRepository;
  private final FlowDepositRepository flowDepositRepository;

  /**
   * Performs this operation on the given argument.
   *
   * @param payload the input argument
   */
  @Transactional(rollbackOn = SQLException.class)
  @Override
  public void accept(FlowFileControlCreateFlowTraceabilityModel payload) {
    switch (PortalDepositType.valueOf(payload.getPortalDepositType())) {
      case CAMPAIGN_SMS:
      case CAMPAIGN_EMAIL:
        initializeFlowCampaign(payload);
        break;
      // not supported yet.
      case PDF:
        initializeFlowPortalPDF(payload);
        break;
      case BATCH:
        initializeFlowBatchDeposit(payload);
        break;
    }
  }

  public void initializeFlowCampaign(FlowFileControlCreateFlowTraceabilityModel flowInitialValue) {
    log.info(
        "Initialize or update flow of campaign \"{}\" with fileId: {}.",
        flowInitialValue.getPortalDepositType().replace("CAMPAIGN_", ""),
        flowInitialValue.getFileId());
    final FlowTraceability flowTraceability = getFlowInstant(flowInitialValue, false);
    flowTraceability.setFlowName(flowInitialValue.getCampaignName());
    FlowTraceabilityDetails flowTraceabilityDetails = flowTraceability.getFlowTraceabilityDetails();
    flowTraceabilityDetails.setCampaignName(flowInitialValue.getCampaignName());
    flowTraceabilityDetails.setCampaignFilename(flowInitialValue.getFlowName());

    // Create flow traceability.
    final FlowTraceability refFlowProxy = this.flowTraceabilityRepository.save(flowTraceability);
    initializeSummaryCampaignFlowDetails(flowInitialValue, refFlowProxy);
  }

  public void initializeFlowPortalPDF(FlowFileControlCreateFlowTraceabilityModel flowInitialValue) {
    log.info(
        "Flow fileId during initialize flow campaign detail portal pdf: {}",
        flowInitialValue.getFileId());
    final FlowTraceability flowTraceability = getFlowInstant(flowInitialValue, true);
    flowTraceability.setFlowName(flowInitialValue.getFlowName());
    flowTraceability.setModelName(flowInitialValue.getModelName());
    final FlowTraceability refFlowProxy = this.flowTraceabilityRepository.save(flowTraceability);
    initializeFlowDepositDetails(flowInitialValue, refFlowProxy);
  }

  private void initializeFlowBatchDeposit(
      FlowFileControlCreateFlowTraceabilityModel flowInitialValue) {
    log.info(
        "Flow fileId during initialize flow campaign detail batch deposit: {}",
        flowInitialValue.getFileId());
    final FlowTraceability flowTraceability = getFlowInstant(flowInitialValue, true);
    flowTraceability.setFlowName(flowInitialValue.getFlowName());
    final FlowTraceability refFlowProxy = this.flowTraceabilityRepository.save(flowTraceability);
    initializeFlowBatchDepositDetails(flowInitialValue, refFlowProxy);
  }

  public void initializeSummaryCampaignFlowDetails(
      FlowFileControlCreateFlowTraceabilityModel flowInitialValue, FlowTraceability refFlowProxy) {

    final FlowCampaignDetail finalFlowCampaignDetail =
        this.flowCampaignDetailRepository.findById(refFlowProxy.getId())
            .orElse(new com.tessi.cxm.pfl.ms3.entity.FlowCampaignDetail());

    finalFlowCampaignDetail.setCampaignName(flowInitialValue.getCampaignName());
    log.info("FlowCamDet: {}, {}.", finalFlowCampaignDetail.getCampaignName(),
        flowInitialValue.getCampaignName());
    finalFlowCampaignDetail.setHtmlTemplate(
        StringUtils.defaultString(flowInitialValue.getHtmlContent(), ""));
    finalFlowCampaignDetail.setTotalRecord(flowInitialValue.getTotalCsvRecord());
    finalFlowCampaignDetail.setCampaignType(
        flowInitialValue.getPortalDepositType().replace("CAMPAIGN_", ""));
    finalFlowCampaignDetail.setFlowTraceability(refFlowProxy);

    this.flowCampaignDetailRepository.save(finalFlowCampaignDetail);
  }

  public void initializeFlowDepositDetails(
      FlowFileControlCreateFlowTraceabilityModel flowInitialValue, FlowTraceability refFlowProxy) {
    FlowDeposit flowDeposit = new FlowDeposit();
    flowDeposit.setFlowTraceability(refFlowProxy);
    flowDeposit.setFileId(flowInitialValue.getFileId());
    flowDeposit.setComposedFileId("");
    flowDeposit.setCreatedBy(flowInitialValue.getCreatedBy());
    flowDeposit.setCreatedAt(refFlowProxy.getCreatedAt());
    flowDeposit.setCreatedAt(new Date());
    flowDeposit.setStatus(FlowTraceabilityStatusConstant.TO_FINALIZE);
    this.flowDepositRepository.save(flowDeposit);
  }

  public void initializeFlowBatchDepositDetails(
      FlowFileControlCreateFlowTraceabilityModel flowInitialValue, FlowTraceability refFlowProxy) {
    FlowCampaignDetail flowCampaignDetail = new FlowCampaignDetail();
    flowCampaignDetail.setId(refFlowProxy.getId());
    flowCampaignDetail.setFlowTraceability(refFlowProxy);
    flowCampaignDetail.setCampaignName(flowInitialValue.getFlowName());
    flowCampaignDetail.setCampaignType(flowInitialValue.getPortalDepositType());
    flowCampaignDetail.setCreatedBy(refFlowProxy.getCreatedBy());
    flowCampaignDetail.setHtmlTemplate(flowInitialValue.getHtmlContent());
    this.flowCampaignDetailRepository.save(flowCampaignDetail);
  }

  public FlowTraceability getFlowInstant(
      FlowFileControlCreateFlowTraceabilityModel flowInitialValue, boolean includeHistory) {

    // initialize the flow.
    FlowTraceability flowTraceability = new FlowTraceability();
    flowTraceability.setCreatedBy(flowInitialValue.getCreatedBy());
    flowTraceability.setOwnerId(flowInitialValue.getOwnerId());
    flowTraceability.setChannel(flowInitialValue.getChannel());
    flowTraceability.setSubChannel(flowInitialValue.getSubChannel());
    flowTraceability.setDepositMode(flowInitialValue.getDepositMode());
    flowTraceability.setFileId(flowInitialValue.getFileId());
    flowTraceability.setFullName(flowInitialValue.getFullName());
    flowTraceability.setStatus(flowInitialValue.getStatus());
    flowTraceability.setDateStatus(flowInitialValue.getDepositDate());
    // initialize the flow history.
    if (includeHistory) {
      FlowHistory history = new FlowHistory(flowTraceability, flowInitialValue.getServer());
      history.setCreatedBy(flowInitialValue.getCreatedBy());
      flowTraceability.addFlowHistory(history);
    }

    // initialize the flow details.
    FlowTraceabilityDetails flowTraceabilityDetail = new FlowTraceabilityDetails();
    flowTraceabilityDetail.setPortalDepositType(flowInitialValue.getPortalDepositType());
    flowTraceabilityDetail.setCampaignFilename(flowInitialValue.getCampaignName());
    flowTraceabilityDetail.setCampaignName(flowInitialValue.getCampaignName());
    flowTraceability.addFlowTraceabilityDetails(flowTraceabilityDetail);

    // flow validation details.
    var validationDetails = new FlowTraceabilityValidationDetails();
    flowTraceability.addValidationDetails(validationDetails);
    return this.flowTraceabilityRepository
        .findByFileId(flowInitialValue.getFileId())
        .orElse(flowTraceability);
  }
}
