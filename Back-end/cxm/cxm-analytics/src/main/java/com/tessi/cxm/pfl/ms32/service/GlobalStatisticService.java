package com.tessi.cxm.pfl.ms32.service;

import com.tessi.cxm.pfl.ms32.constant.DepositModeVolumeResponseDto;
import com.tessi.cxm.pfl.ms32.constant.GlobalProductionDetailMetaData;
import com.tessi.cxm.pfl.ms32.dto.GlobalStatisticRequestFilter;
import com.tessi.cxm.pfl.ms32.dto.DocumentTotalDto;
import com.tessi.cxm.pfl.ms32.dto.FlowDocumentProductionDetail;
import com.tessi.cxm.pfl.ms32.dto.FlowProductionDetailMetaData;
import com.tessi.cxm.pfl.ms32.dto.FlowProductionDetailsDto;
import com.tessi.cxm.pfl.ms32.dto.DocumentSummary;
import com.tessi.cxm.pfl.ms32.repository.FlowTraceabilityReportRepository;
import com.tessi.cxm.pfl.ms32.util.AnalyticsCalculatorUtils;
import com.tessi.cxm.pfl.shared.service.restclient.ProfileFeignClient;
import com.tessi.cxm.pfl.shared.service.restclient.SettingFeignClient;
import com.tessi.cxm.pfl.shared.utils.FlowDocumentChannelConstant;
import com.tessi.cxm.pfl.shared.utils.FlowDocumentSubChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class GlobalStatisticService extends AbstractStatisticService {

  public GlobalStatisticService(
      FlowTraceabilityReportRepository flowTraceabilityReportRepository,
      SettingFeignClient settingFeignClient,
      ProfileFeignClient profileFeignClient) {
    super(flowTraceabilityReportRepository, settingFeignClient, profileFeignClient);
  }

  @Transactional(readOnly = true)
  public FlowProductionDetailsDto getGlobalProductionDetails(
      GlobalStatisticRequestFilter requestFilter) {
    validateAndResolve(requestFilter);

    List<FlowProductionDetailMetaData> metaData = this.getColumnMetaData();
    List<FlowDocumentProductionDetail> rows = this.getDefaultProductionDetails(requestFilter);
    DocumentSummary volumeReceiveDTO = fetchVolumeReceiveDetails(requestFilter);
    DocumentSummary totalInProgress = fetchTotalInProgress(requestFilter);
    List<DocumentTotalDto> documentProcessed = this.getReportDocumentProcess(requestFilter);
    this.mapProcessedDocToResult(documentProcessed, rows, volumeReceiveDTO);
    this.mapInProgress(rows, totalInProgress);
    this.mapPNDAndMND(rows, requestFilter, volumeReceiveDTO);
    this.mapTotalToResult(rows);
    return new FlowProductionDetailsDto(metaData, rows);
  }

  @Transactional(readOnly = true)
  public List<DepositModeVolumeResponseDto> getProductionProgress(
      GlobalStatisticRequestFilter requestFilter) {
    validateAndResolve(requestFilter);

    DocumentSummary documentProcess = this.getDocumentProcess(requestFilter);

    DocumentSummary volumeReceiveDTO = fetchVolumeReceiveDetails(requestFilter);

    List<DepositModeVolumeResponseDto> productionProgressed = new ArrayList<>();
    List<String> orderedChannels = getOrderChannels(requestFilter);

    orderedChannels.forEach(
        orderedChannel -> {
          var totalProcessedDocs =
              ObjectUtils.defaultIfNull(documentProcess.get(orderedChannel), 0L);
          var totalVolRec = ObjectUtils.defaultIfNull(volumeReceiveDTO.get(orderedChannel), 0L);
          double percentage =
              totalVolRec == 0
                  ? 0
                  : AnalyticsCalculatorUtils.getTotalPercentage(totalProcessedDocs, totalVolRec);
          DepositModeVolumeResponseDto build =
              DepositModeVolumeResponseDto.builder()
                  .key(orderedChannel)
                  .value(percentage)
                  .volume(totalProcessedDocs)
                  .build();
          productionProgressed.add(build);
        });

    return productionProgressed;
  }

  @Transactional(readOnly = true)
  public List<DepositModeVolumeResponseDto> getVolumeReceived(
      GlobalStatisticRequestFilter requestFilter) {

    validateAndResolve(requestFilter);

    DocumentSummary volumeReceiveDTO = fetchVolumeReceiveDetails(requestFilter);

    Map<String, DepositModeVolumeResponseDto> depositModeResponseDtoMap =
        volumeReceiveDTO.entrySet().stream()
            .collect(
                Collectors.toMap(
                    Entry::getKey,
                    volumeReceiveEntry ->
                        DepositModeVolumeResponseDto.builder()
                            .key(volumeReceiveEntry.getKey())
                            .value(
                                AnalyticsCalculatorUtils.getTotalPercentage(
                                    volumeReceiveEntry.getValue(),
                                    volumeReceiveDTO.getTotalDocument()))
                            .volume(volumeReceiveEntry.getValue())
                            .build()));

    List<String> orders = getOrderChannels(requestFilter);
    return orders.stream()
        .map(
            key ->
                ObjectUtils.defaultIfNull(
                    depositModeResponseDtoMap.get(key),
                    DepositModeVolumeResponseDto.builder().key(key).value(0.0).volume(0).build()))
        .collect(Collectors.toList());
  }

  private List<FlowProductionDetailMetaData> getColumnMetaData() {
    return Arrays.stream(GlobalProductionDetailMetaData.values())
        .map(
            metaData ->
                FlowProductionDetailMetaData.builder()
                    .col(metaData.getValue())
                    .label(metaData.getKey())
                    .type(metaData.getDataType())
                    .build())
        .collect(Collectors.toList());
  }

  private void mapProcessedDocToResult(
      List<DocumentTotalDto> reportDocumentTreaties,
      List<FlowDocumentProductionDetail> productionDetails,
      DocumentSummary volumeReceived) {
    Map<String, List<DocumentTotalDto>> totalDocProcess =
        reportDocumentTreaties.stream()
            .collect(Collectors.groupingBy(DocumentTotalDto::getChannel));

    productionDetails.forEach(
        proDetail -> {
          if (FlowDocumentChannelConstant.POSTAL.equalsIgnoreCase(proDetail.getChannel())) {
            Long totalVolumeReceived =
                ObjectUtils.defaultIfNull(
                    volumeReceived.get(FlowDocumentChannelConstant.POSTAL), 0L);
            List<DocumentTotalDto> postalTotal =
                ObjectUtils.defaultIfNull(
                    totalDocProcess.get(FlowDocumentChannelConstant.POSTAL), new ArrayList<>());
            long totalPostalChannel =
                postalTotal.stream().mapToLong(DocumentTotalDto::getTotal).sum();
            proDetail.setTreaty(totalPostalChannel);
            proDetail.setTreatyPercentage(
                AnalyticsCalculatorUtils.getTotalPercentage(
                    totalPostalChannel, totalVolumeReceived));
          }
          if (FlowDocumentSubChannel.EMAIL.getValue().equalsIgnoreCase(proDetail.getChannel())) {
            Long totalVolumeReceived =
                ObjectUtils.defaultIfNull(
                    volumeReceived.get(FlowDocumentSubChannel.EMAIL.getValue()), 0L);
            List<DocumentTotalDto> digitalTotal =
                ObjectUtils.defaultIfNull(
                        totalDocProcess.get(FlowDocumentChannelConstant.DIGITAL),
                        new ArrayList<DocumentTotalDto>())
                    .stream()
                    .filter(
                        documentTotalDto ->
                            FlowDocumentSubChannel.EMAIL
                                .getValue()
                                .equalsIgnoreCase(documentTotalDto.getSubChannel().toLowerCase()))
                    .collect(Collectors.toList());
            long totalPostalChannel =
                digitalTotal.stream().mapToLong(DocumentTotalDto::getTotal).sum();
            proDetail.setTreaty(totalPostalChannel);
            proDetail.setTreatyPercentage(
                AnalyticsCalculatorUtils.getTotalPercentage(
                    totalPostalChannel, totalVolumeReceived));
          }
          if (FlowDocumentSubChannel.SMS.getValue().equalsIgnoreCase(proDetail.getChannel())) {
            Long totalVolumeReceived =
                ObjectUtils.defaultIfNull(
                    volumeReceived.get(FlowDocumentSubChannel.SMS.getValue()), 0L);
            List<DocumentTotalDto> digitalTotal =
                ObjectUtils.defaultIfNull(
                        totalDocProcess.get(FlowDocumentChannelConstant.DIGITAL),
                        new ArrayList<DocumentTotalDto>())
                    .stream()
                    .filter(
                        documentTotalDto ->
                            FlowDocumentSubChannel.SMS
                                .getValue()
                                .equalsIgnoreCase(documentTotalDto.getSubChannel().toLowerCase()))
                    .collect(Collectors.toList());
            long totalPostalChannel =
                digitalTotal.stream().mapToLong(DocumentTotalDto::getTotal).sum();
            proDetail.setTreaty(totalPostalChannel);
            proDetail.setTreatyPercentage(
                AnalyticsCalculatorUtils.getTotalPercentage(
                    totalPostalChannel, totalVolumeReceived));
          }
        });
  }

  private void mapTotalToResult(List<FlowDocumentProductionDetail> rows) {
    FlowDocumentProductionDetail totalRow = new FlowDocumentProductionDetail();
    AtomicLong totalVolumeReceived = new AtomicLong(0);
    AtomicLong totalPNDMND = new AtomicLong(0);
    AtomicLong totalInProgress = new AtomicLong(0);
    AtomicLong totalInProcess = new AtomicLong(0);

    rows.forEach(
        row -> {
          totalVolumeReceived.getAndAdd(row.getVolumeReceived());
          totalPNDMND.getAndAdd(row.getPndMnd());
          totalInProcess.getAndAdd(row.getTreaty());
          totalInProgress.getAndAdd(row.getInProgress());
        });

    totalRow.setVolumeReceived(totalVolumeReceived.get());
    totalRow.setPndMnd(totalPNDMND.get());
    totalRow.setTreaty(totalInProcess.get());
    totalRow.setInProgress(totalInProgress.get());
    totalRow.setPndMndPercentage(
        AnalyticsCalculatorUtils.getTotalPercentage(totalPNDMND.get(), totalVolumeReceived.get()));
    totalRow.setTreatyPercentage(
        AnalyticsCalculatorUtils.getTotalPercentage(
            totalInProcess.get(), totalVolumeReceived.get()));
    totalRow.setChannel("Total");
    rows.add(totalRow);
  }

  private void mapPNDAndMND(
      List<FlowDocumentProductionDetail> productionDetails,
      GlobalStatisticRequestFilter baseFilterVolumeReceived,
      DocumentSummary volumeReceived) {
    long totalVolumePND = this.getTotalPND(baseFilterVolumeReceived);
    DocumentSummary totalVolumeMND = this.getTotalMND(baseFilterVolumeReceived);
    productionDetails.forEach(
        proDetail -> {
          if (FlowDocumentChannelConstant.POSTAL.equalsIgnoreCase(proDetail.getChannel())) {
            Long totalVolumeReceived =
                ObjectUtils.defaultIfNull(
                    volumeReceived.get(FlowDocumentChannelConstant.POSTAL), 0L);
            proDetail.setVolumeReceived(totalVolumeReceived);
            proDetail.setPndMnd(totalVolumePND);
            proDetail.setPndMndPercentage(
                AnalyticsCalculatorUtils.getTotalPercentage(totalVolumePND, totalVolumeReceived));
          }
          if (FlowDocumentSubChannel.EMAIL.getValue().equalsIgnoreCase(proDetail.getChannel())) {
            Long totalVolumeReceived =
                ObjectUtils.defaultIfNull(
                    volumeReceived.get(FlowDocumentSubChannel.EMAIL.getValue()), 0L);
            Long totalMND =
                ObjectUtils.defaultIfNull(
                    totalVolumeMND.get(FlowDocumentSubChannel.EMAIL.getValue()), 0L);
            proDetail.setVolumeReceived(totalVolumeReceived);
            proDetail.setPndMnd(totalMND);
            proDetail.setPndMndPercentage(
                AnalyticsCalculatorUtils.getTotalPercentage(totalMND, totalVolumeReceived));
          }
          if (FlowDocumentSubChannel.SMS.getValue().equalsIgnoreCase(proDetail.getChannel())) {
            Long totalMND =
                ObjectUtils.defaultIfNull(
                    totalVolumeMND.get(FlowDocumentSubChannel.SMS.getValue()), 0L);
            Long totalVolumeReceived =
                ObjectUtils.defaultIfNull(
                    volumeReceived.get(FlowDocumentSubChannel.SMS.getValue()), 0L);
            proDetail.setVolumeReceived(totalVolumeReceived);
            proDetail.setPndMnd(totalMND);
            proDetail.setPndMndPercentage(
                AnalyticsCalculatorUtils.getTotalPercentage(totalMND, totalVolumeReceived));
          }
        });
  }

  private void mapInProgress(
      List<FlowDocumentProductionDetail> productionDetails, DocumentSummary volumeReceived) {

    productionDetails.forEach(
        proDetail -> {
          if (FlowDocumentChannelConstant.POSTAL.equalsIgnoreCase(proDetail.getChannel())) {
            Long totalVolumeReceived =
                ObjectUtils.defaultIfNull(
                    volumeReceived.get(FlowDocumentChannelConstant.POSTAL), 0L);
            proDetail.setInProgress(totalVolumeReceived);
          }
          if (FlowDocumentSubChannel.EMAIL.getValue().equalsIgnoreCase(proDetail.getChannel())) {
            Long totalVolumeReceived =
                ObjectUtils.defaultIfNull(
                    volumeReceived.get(FlowDocumentSubChannel.EMAIL.getValue()), 0L);
            proDetail.setInProgress(totalVolumeReceived);
          }
          if (FlowDocumentSubChannel.SMS.getValue().equalsIgnoreCase(proDetail.getChannel())) {
            Long totalVolumeReceived =
                ObjectUtils.defaultIfNull(
                    volumeReceived.get(FlowDocumentSubChannel.SMS.getValue()), 0L);
            proDetail.setInProgress(totalVolumeReceived);
          }
        });
  }
}
