package com.tessi.cxm.pfl.ms32.service;

import com.tessi.cxm.pfl.ms32.entity.FlowDocumentReport;
import com.tessi.cxm.pfl.ms32.entity.FlowDocumentReportHistory;
import com.tessi.cxm.pfl.ms32.exception.FlowDocumentReportNotFoundException;
import com.tessi.cxm.pfl.ms32.exception.FlowTraceabilityReportNotFoundException;
import com.tessi.cxm.pfl.ms32.exception.StatusNotInOrderException;
import com.tessi.cxm.pfl.ms32.repository.FlowDocumentReportRepository;
import com.tessi.cxm.pfl.ms32.repository.FlowTraceabilityReportRepository;
import com.tessi.cxm.pfl.shared.model.kafka.CreateFlowDocumentHistoryStatusReportModel;
import com.tessi.cxm.pfl.shared.model.kafka.CreateFlowDocumentReportModel;
import com.tessi.cxm.pfl.shared.model.kafka.UpdateFlowDocumentStatusReportModel;
import com.tessi.cxm.pfl.shared.utils.FlowDocumentChannelConstant;
import com.tessi.cxm.pfl.shared.utils.FlowDocumentStatus;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/** A service for processing Flow document report. */
@RequiredArgsConstructor
@Slf4j
@Service
public class FlowDocumentReportService {

  private final ModelMapper modelMapper;
  private final FlowDocumentReportRepository flowDocumentReportRepository;
  private final FlowTraceabilityReportRepository flowReportRepository;

  /**
   * Create a new {@link FlowDocumentReport}. If it is existed by {@link
   * CreateFlowDocumentReportModel#getDocumentId()}, do nothing.
   *
   * @param payload {@link CreateFlowDocumentReportModel}
   * @throws FlowTraceabilityReportNotFoundException When {@link
   *     com.tessi.cxm.pfl.ms32.entity.FlowTraceabilityReport} is not found by {@link
   *     CreateFlowDocumentReportModel#getFlowId()}.
   */
  @Transactional(rollbackFor = Exception.class)
  @Retryable(
      value = FlowTraceabilityReportNotFoundException.class,
      maxAttempts = 10,
      backoff = @Backoff(delay = 300))
  public void createDocumentReport(CreateFlowDocumentReportModel payload) {
    var flowReport =
        this.flowReportRepository
            .findById(payload.getFlowId())
            .orElseThrow(() -> new FlowTraceabilityReportNotFoundException(payload.getFlowId()));

    Optional<FlowDocumentReport> existingFlowDoc =
        this.flowDocumentReportRepository.findById(payload.getDocumentId());

    if (existingFlowDoc.isEmpty()) {
      var flowDocReport = new FlowDocumentReport();
      this.modelMapper.map(payload, flowDocReport);
      if (FlowDocumentChannelConstant.POSTAL.equalsIgnoreCase(flowReport.getChannel())) {
        setFlowDocumentFiller(flowDocReport, payload.getFillers());
      }
      if (!CollectionUtils.isEmpty(payload.getCreateFlowDocumentHistoryStatusReportModels())) {
        payload
            .getCreateFlowDocumentHistoryStatusReportModels()
            .forEach(
                flowDocumentHistoryModel ->
                    setFlowDocumentReportHistory(flowDocReport, flowDocumentHistoryModel));
      }
      flowReport.addDocumentReport(flowDocReport);

      this.flowReportRepository.save(flowReport);
      return;
    }
    log.debug("Flow document report is already existed: {}.", payload.getDocumentId());
  }

  /**
   * Update flow document status reports.
   *
   * @param payload - object of {@link UpdateFlowDocumentStatusReportModel}.
   */
  @Transactional(rollbackFor = Exception.class)
  public void updateDocumentStatusReport(UpdateFlowDocumentStatusReportModel payload) {
    this.flowDocumentReportRepository
        .findById(payload.getDocumentId())
        .ifPresent(
            flowDocumentReport -> {
              flowDocumentReport.setStatus(payload.getStatus());
              flowDocumentReport.setDateStatus(payload.getDateStatus());
              flowDocumentReport.setModifiedAt(new Date());
              if (!StringUtils.hasText(flowDocumentReport.getIdDoc())) {
                flowDocumentReport.setIdDoc(payload.getIdDoc());
              }
              if (!StringUtils.hasText(flowDocumentReport.getNumReco())) {
                flowDocumentReport.setNumReco(payload.getNumReco());
              }
              flowDocumentReport.setSubChannel(payload.getSubChannel());
              this.flowDocumentReportRepository.save(flowDocumentReport);
            });
  }

  @Transactional(rollbackFor = Exception.class)
  @Retryable(
      value = {FlowDocumentReportNotFoundException.class, StatusNotInOrderException.class},
      maxAttempts = 10,
      backoff = @Backoff(delay = 300))
  public void createFlowDocumentEventHistory(
      CreateFlowDocumentHistoryStatusReportModel historyStatusReportModel) {
    this.flowDocumentReportRepository
        .findById(historyStatusReportModel.getFlowDocumentId())
        .ifPresentOrElse(
            flowDocumentReport -> {
              setFlowDocumentReportHistory(flowDocumentReport, historyStatusReportModel);
              updateDateSending(flowDocumentReport, historyStatusReportModel);
            },
            () -> {
              throw new FlowDocumentReportNotFoundException(
                  historyStatusReportModel.getFlowDocumentId());
            });
  }

  private void setFlowDocumentReportHistory(
          FlowDocumentReport flowDocumentReport, CreateFlowDocumentHistoryStatusReportModel flowDocumentHistoryModel) {
    Optional<FlowDocumentReportHistory> existHistory =
            flowDocumentReport.getFlowDocumentHistories().stream()
                    .filter(hist -> Objects.equals(hist.getId(), flowDocumentHistoryModel.getId()))
                    .findFirst();
    if (existHistory.isPresent()) {
      FlowDocumentReportHistory flowDocumentReportHistory = existHistory.get();
      flowDocumentReportHistory.setStatus(flowDocumentHistoryModel.getStatus());
      flowDocumentReportHistory.setDateStatus(flowDocumentHistoryModel.getDateStatus());
    } else {
      FlowDocumentReportHistory flowDocumentReportHistory = new FlowDocumentReportHistory();
      flowDocumentReportHistory.setId(flowDocumentHistoryModel.getId());
      flowDocumentReportHistory.setStatus(flowDocumentHistoryModel.getStatus());
      flowDocumentReportHistory.setDateStatus(flowDocumentHistoryModel.getDateStatus());
      flowDocumentReportHistory.setCreatedAt(flowDocumentHistoryModel.getDateStatus());
      flowDocumentReport.addDocumentReportHistory(flowDocumentReportHistory);
    }
  }

  /**
   * Update date sending of {@link FlowDocumentReport}
   *
   * <p>For Postal, date sending is the date status of history {@link FlowDocumentStatus#STAMPED}.
   * For Digital, date sending is the date status of history {@link FlowDocumentStatus#SENT}.
   *
   * @param flowDocumentReport {@link FlowDocumentReport} to update.
   * @param docHistoryPayload Document hisotory payload.
   */
  private void updateDateSending(
      FlowDocumentReport flowDocumentReport,
      CreateFlowDocumentHistoryStatusReportModel docHistoryPayload) {
    var hisList =
        List.of(FlowDocumentStatus.SENT.getValue(), FlowDocumentStatus.STAMPED.getValue());
    if (docHistoryPayload.getStatus() != null
        && flowDocumentReport.getDateSending() == null
        && hisList.contains(docHistoryPayload.getStatus())) {
      if (!FlowDocumentStatus.COMPLETED
          .getValue()
          .equalsIgnoreCase(flowDocumentReport.getStatus())) {
        throw new StatusNotInOrderException(
            flowDocumentReport.getId(), FlowDocumentStatus.COMPLETED.getValue());
      }
      flowDocumentReport.setDateSending(docHistoryPayload.getDateStatus());
    }
  }

  private void setFlowDocumentFiller(FlowDocumentReport documentReport, String[] fillers) {
    if (fillers == null || fillers.length != 5) {
      log.error("Fail to set fillers to flowDocument Report because: fillers do not equal 5 item");
      return;
    }
    documentReport.setFiller1(StringUtils.hasText(fillers[0]) ? fillers[0] : null);
    documentReport.setFiller2(StringUtils.hasText(fillers[1]) ? fillers[1] : null);
    documentReport.setFiller3(StringUtils.hasText(fillers[2]) ? fillers[2] : null);
    documentReport.setFiller4(StringUtils.hasText(fillers[3]) ? fillers[3] : null);
    documentReport.setFiller5(StringUtils.hasText(fillers[4]) ? fillers[4] : null);
  }
}
