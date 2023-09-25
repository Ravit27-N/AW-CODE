package com.tessi.cxm.pfl.ms3.service;

import com.tessi.cxm.pfl.shared.model.kafka.CreateFlowDocumentHistoryStatusReportModel;
import com.tessi.cxm.pfl.shared.model.kafka.CreateFlowDocumentReportModel;
import com.tessi.cxm.pfl.shared.model.kafka.CreateFlowTraceabilityReportModel;
import com.tessi.cxm.pfl.shared.model.kafka.UpdateFlowDocumentStatusReportModel;
import com.tessi.cxm.pfl.shared.model.kafka.UpdateFlowTraceabilityReportModel;
import com.tessi.cxm.pfl.shared.service.ServiceUtils;
import com.tessi.cxm.pfl.shared.utils.KafkaUtils;
import javax.validation.Validator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportingService implements ServiceUtils {

  private final StreamBridge streamBridge;

  @Getter
  private final Validator validator;

  /**
   * Create flow traceability report.
   *
   * @param flowTraceabilityReportModel - object of {@link CreateFlowTraceabilityReportModel}.
   */
  public void createFlowTraceabilityReport(
      CreateFlowTraceabilityReportModel flowTraceabilityReportModel) {
    if (flowTraceabilityReportModel.getChannel() != null
        && flowTraceabilityReportModel.getSubChannel() != null) {
      log.info("<<CREATE_FLOW_TRACEABILITY_REPORT: {}>>", flowTraceabilityReportModel);
      try {
        this.streamBridge.send(KafkaUtils.CREATE_FLOW_TRACEABILITY_REPORT_TOPIC,
            flowTraceabilityReportModel);
      } catch (Exception e) {
        log.error(e.getMessage(), e);
      }
    }
  }

  /**
   * Update flow traceability report.
   *
   * @param flowTraceabilityReport - object of {@link UpdateFlowTraceabilityReportModel}.
   */
  public void updateFlowTraceabilityReport(
      UpdateFlowTraceabilityReportModel flowTraceabilityReport) {
    if (StringUtils.hasText(flowTraceabilityReport.getSubChannel())) {
      log.info("<< UPDATE_FLOW_TRACEABILITY_REPORT >>");
      log.debug("Payload: {}.", flowTraceabilityReport);
      try {
        this.streamBridge.send(
            KafkaUtils.UPDATE_FLOW_TRACEABILITY_REPORT_TOPIC, flowTraceabilityReport);
      } catch (Exception e) {
        log.error(e.getMessage(), e);
      }
    }
  }

  /**
   * Create flow document reports.
   *
   * @param flowDocumentReportModel - object of {@link CreateFlowDocumentReportModel}
   */
  public void createFlowDocumentReport(
      CreateFlowDocumentReportModel flowDocumentReportModel) {
    if (flowDocumentReportModel.getStatus() != null) {
      log.info("<<CREATE_FLOW_DOCUMENT_REPORT: {}>>", flowDocumentReportModel);
      try {
        this.streamBridge.send(KafkaUtils.CREATE_FLOW_DOCUMENT_REPORT_TOPIC,
            flowDocumentReportModel);
      } catch (Exception e) {
        log.error(e.getMessage(), e);
      }
    }
  }

  /**
   * Update flow document status report.
   *
   * @param flowDocumentStatusReportModel - object of {@link UpdateFlowDocumentStatusReportModel}.
   */
  public boolean updateFlowDocumentStatusReport(
      UpdateFlowDocumentStatusReportModel flowDocumentStatusReportModel) {
    if (flowDocumentStatusReportModel.getStatus() != null) {
      log.info("<<UPDATE_FLOW_DOCUMENT_STATUS_REPORT: {}>>", flowDocumentStatusReportModel);
      try {
        return this.streamBridge.send(
            KafkaUtils.UPDATE_FLOW_DOCUMENT_STATUS_REPORT_TOPIC, flowDocumentStatusReportModel);
      } catch (Exception e) {
        log.error(e.getMessage(), e);
        return false;
      }
    }
    return false;
  }

  public void createFlowDocumentEventHistory(
      CreateFlowDocumentHistoryStatusReportModel flowDocumentHistoryModel) {
    this.validate(flowDocumentHistoryModel);
    try {
      log.info("<< UPDATE_FLOW_DOCUMENT_REPORT_EVENT_HISTORY >>");
      log.debug("Payload: {}.", flowDocumentHistoryModel);
      this.streamBridge.send(
          KafkaUtils.CREATE_FLOW_DOCUMENT_HISTORY_STATUS_TOPIC, flowDocumentHistoryModel);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }
}
