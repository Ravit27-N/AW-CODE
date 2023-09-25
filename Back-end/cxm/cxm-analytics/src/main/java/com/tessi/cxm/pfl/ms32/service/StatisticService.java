package com.tessi.cxm.pfl.ms32.service;

import com.tessi.cxm.pfl.ms32.constant.DepositModeResponseDto;
import com.tessi.cxm.pfl.ms32.constant.ExportType;
import com.tessi.cxm.pfl.ms32.constant.ProductionDetailMetaData;
import com.tessi.cxm.pfl.ms32.dto.DocumentDetailSummary;
import com.tessi.cxm.pfl.ms32.dto.FlowProductionDetailMetaData;
import com.tessi.cxm.pfl.ms32.dto.GlobalStatisticRequestFilter;
import com.tessi.cxm.pfl.ms32.dto.StatisticExportingRequestFilter;
import com.tessi.cxm.pfl.ms32.dto.StatisticRequestFilter;
import com.tessi.cxm.pfl.ms32.repository.FlowTraceabilityReportRepository;
import com.tessi.cxm.pfl.ms32.util.AnalyticsCalculatorUtils;
import com.tessi.cxm.pfl.shared.model.SharedClientFillersDTO;
import com.tessi.cxm.pfl.shared.model.UserPrivilegeDetails;
import com.tessi.cxm.pfl.shared.service.ServiceUtils;
import com.tessi.cxm.pfl.shared.service.restclient.ProfileFeignClient;
import com.tessi.cxm.pfl.shared.service.restclient.SettingFeignClient;
import com.tessi.cxm.pfl.shared.utils.FlowDocumentChannelConstant;
import com.tessi.cxm.pfl.shared.utils.FlowDocumentStatus;
import com.tessi.cxm.pfl.shared.utils.PrivilegeValidationUtil;
import com.tessi.cxm.pfl.shared.utils.ProfileConstants;
import com.tessi.cxm.pfl.shared.utils.ProfileConstants.Privilege.Statistic;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Slf4j
@Service
public class StatisticService extends AbstractStatisticService implements ServiceUtils {

  public StatisticService(
      FlowTraceabilityReportRepository flowTraceabilityReportRepository,
      SettingFeignClient settingFeignClient,
      ProfileFeignClient profileFeignClient) {
    super(flowTraceabilityReportRepository, settingFeignClient, profileFeignClient);
  }

  public DocumentDetailSummary calculateProductionDetails(StatisticRequestFilter requestFilter) {
    this.validateAndResolve(requestFilter);
    List<String> volumeReceivedStatus = FlowDocumentStatus.getVolumeReceivedStatus();
    requestFilter.setStatuses(volumeReceivedStatus);

    requestFilter.setSubStatuses(
        FlowDocumentChannelConstant.POSTAL.equals(requestFilter.getChannels().get(0))
            ? FlowDocumentStatus.getPNDStatus()
            : FlowDocumentStatus.getMNDStatus());

    DocumentDetailSummary documentDetailSummary = this.summaryDocsByFillerGrouping(requestFilter);

    var productionDetailsFillers = this.fetchProdDetailsPndMnd(requestFilter);

    this.mapAndCalProdDetails(documentDetailSummary, productionDetailsFillers, requestFilter);

    this.calPercentage(documentDetailSummary);

    if (requestFilter.isIncludeMetadata()) {
      boolean includeFillers =
          !requestFilter.getGroupFillers().isEmpty()
              && documentDetailSummary.getData().stream()
                  .allMatch(docSum -> StringUtils.hasText(docSum.getFiller()));
      documentDetailSummary.setMetaData(this.getColumnMetaData(requestFilter, includeFillers));
    }

    return documentDetailSummary;
  }

  public List<DepositModeResponseDto> calculateDistributionVolumeReceive(
      StatisticRequestFilter requestFilter) {
    this.validateAndResolve(requestFilter);
    List<String> volumeReceivedStatus = FlowDocumentStatus.getVolumeReceivedStatus();
    requestFilter.setStatuses(volumeReceivedStatus);
    DocumentDetailSummary documentDetailSummary = this.summaryDocsByFillerGrouping(requestFilter);
    return this.calculatePercentageVolumeReceived(documentDetailSummary);
  }

  private List<DepositModeResponseDto> calculatePercentageVolumeReceived(
      DocumentDetailSummary request) {

    AtomicReference<Long> totalDoc = new AtomicReference<>(0L);

    request
        .getData()
        .forEach(
            documentStatistics ->
                totalDoc.set(totalDoc.get() + documentStatistics.getVolumeReceived()));
    if (totalDoc.get() != 0) {
      return request.getData().stream()
          .map(
              documentStatistics -> {
                DepositModeResponseDto response = new DepositModeResponseDto();
                if (documentStatistics.getFiller() == null) {
                  documentStatistics.setFiller("Total");
                }
                double percentage =
                    AnalyticsCalculatorUtils.getTotalPercentage(
                        documentStatistics.getVolumeReceived(), totalDoc.get());
                response.setKey(documentStatistics.getFiller());
                response.setValue(percentage);

                return response;
              })
          .collect(Collectors.toList());
    } else {
      return Collections.singletonList(new DepositModeResponseDto("Total", 0));
    }
  }

  private List<FlowProductionDetailMetaData> getColumnMetaData(
      StatisticRequestFilter requestFilter, boolean includeFillers) {
    if (CollectionUtils.isEmpty(requestFilter.getChannels())) {
      return List.of();
    }
    return ProductionDetailMetaData.getMetadata(requestFilter.getChannels().get(0)).stream()
        .filter(
            meta -> {
              if (!includeFillers) {
                return meta != ProductionDetailMetaData.FILLERS;
              }
              return true;
            })
        .filter(meta -> !meta.getValue().equalsIgnoreCase("total"))
        .map(
            metaData ->
                FlowProductionDetailMetaData.builder()
                    .col(metaData.getValue())
                    .label(metaData.getKey())
                    .type(metaData.getDataType())
                    .build())
        .collect(Collectors.toList());
  }
}
