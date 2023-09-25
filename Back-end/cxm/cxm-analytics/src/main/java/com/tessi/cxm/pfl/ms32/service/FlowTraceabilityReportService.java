package com.tessi.cxm.pfl.ms32.service;

import com.tessi.cxm.pfl.ms32.constant.AnalyticsError;
import com.tessi.cxm.pfl.ms32.constant.DepositModeResponseDto;
import com.tessi.cxm.pfl.ms32.constant.FlowDocumentReportMetaData;
import com.tessi.cxm.pfl.ms32.constant.ReportOrderMapper;
import com.tessi.cxm.pfl.ms32.dto.FlowDepositMode;
import com.tessi.cxm.pfl.ms32.dto.FlowDocumentEvolutionReportData;
import com.tessi.cxm.pfl.ms32.dto.FlowDocumentReportData;
import com.tessi.cxm.pfl.ms32.dto.FlowDocumentReportDto;
import com.tessi.cxm.pfl.ms32.dto.FlowDocumentReportMetadata;
import com.tessi.cxm.pfl.ms32.dto.UserFilterPreferenceDto;
import com.tessi.cxm.pfl.ms32.entity.FlowTraceabilityReport;
import com.tessi.cxm.pfl.ms32.entity.projection.FlowDocumentTimeSeriesProjection;
import com.tessi.cxm.pfl.ms32.exception.DomainException;
import com.tessi.cxm.pfl.ms32.exception.FlowTraceabilityReportNotFoundException;
import com.tessi.cxm.pfl.ms32.repository.FlowTraceabilityReportRepository;
import com.tessi.cxm.pfl.ms32.util.DateHelper;
import com.tessi.cxm.pfl.shared.model.UserInfoResponse;
import com.tessi.cxm.pfl.shared.model.UserPrivilegeDetails;
import com.tessi.cxm.pfl.shared.model.kafka.CreateFlowTraceabilityReportModel;
import com.tessi.cxm.pfl.shared.model.kafka.UpdateFlowTraceabilityReportModel;
import com.tessi.cxm.pfl.shared.service.keycloak.KeycloakService;
import com.tessi.cxm.pfl.shared.service.restclient.ProfileFeignClient;
import com.tessi.cxm.pfl.shared.utils.BearerAuthentication;
import com.tessi.cxm.pfl.shared.utils.DepositMode;
import com.tessi.cxm.pfl.shared.utils.FlowDocumentChannelConstant;
import com.tessi.cxm.pfl.shared.utils.FlowDocumentStatus;
import com.tessi.cxm.pfl.shared.utils.FlowDocumentSubChannel;
import com.tessi.cxm.pfl.shared.utils.PrivilegeValidationUtil;
import com.tessi.cxm.pfl.shared.utils.ProfileConstants;
import com.tessi.cxm.pfl.shared.utils.ProfileConstants.Privilege;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * A service for processing Flow traceability report.
 */
@AllArgsConstructor
@Slf4j
@Service
public class FlowTraceabilityReportService {

  private final double scale = Math.pow(10, 4);
  private final FlowTraceabilityReportRepository flowTraceabilityReportRepository;
  private final ModelMapper modelMapper;
  private final FlowTraceabilityReportRepository flowReportRepository;
  private final FilterPreferenceService filterPreferenceService;
  private final ProfileFeignClient profileFeignClient;
  private KeycloakService keycloakService;

  /**
   * Create a new {@link com.tessi.cxm.pfl.ms32.entity.FlowTraceabilityReport}. If it is existed by
   * {@link CreateFlowTraceabilityReportModel#getFlowId()}, do nothing.
   *
   * @param payload {@link CreateFlowTraceabilityReportModel}
   */
  @Transactional(rollbackFor = Exception.class)
  public void create(CreateFlowTraceabilityReportModel payload) {
    var existingFlowReport = this.flowReportRepository.findById(payload.getFlowId());
    if (existingFlowReport.isEmpty()) {
      var keycloakAdminToken =
          BearerAuthentication.PREFIX_TOKEN.concat(this.keycloakService.getToken());
      var flowReportOwner =
          this.profileFeignClient.getUserInfoDetail(payload.getOwnerId(), keycloakAdminToken);

      FlowTraceabilityReport flowReport = new FlowTraceabilityReport();
      this.modelMapper.map(payload, flowReport);
      flowReport.setOwnerId(flowReportOwner.getOwnerId());
      flowReport.setCreatedBy(flowReportOwner.getUsername());

      this.flowReportRepository.save(flowReport);
      return;
    }
    log.debug("Flow is already created: {}.", payload.getFlowId());
  }

  /**
   * Update a new {@link com.tessi.cxm.pfl.ms32.entity.FlowTraceabilityReport} by
   * {@link UpdateFlowTraceabilityReportModel#getFlowId()}
   *
   * @param payload {@link UpdateFlowTraceabilityReportModel}
   */
  @Transactional(rollbackFor = Exception.class)
  @Retryable(
      value = FlowTraceabilityReportNotFoundException.class,
      maxAttempts = 10,
      backoff = @Backoff(delay = 300))
  public void update(UpdateFlowTraceabilityReportModel payload) {
    final var flowTraceabilityReport =
        this.flowTraceabilityReportRepository
            .findById(payload.getFlowId())
            .orElseThrow(() -> new FlowTraceabilityReportNotFoundException(payload.getFlowId()));
    flowTraceabilityReport.setSubChannel(payload.getSubChannel());
    this.flowTraceabilityReportRepository.save(flowTraceabilityReport);
  }

  /**
   * Get all deposit modes by using token user invoke.
   *
   * @param token - value of {@link String}.
   * @return - collections of {@link DepositModeResponseDto}.
   * @throws DomainException When user is a Super-Admin
   */
  public List<DepositModeResponseDto> getDepositModes(String token, Date requestedAt) throws DomainException {

    this.checkAdminCannotSeeDashboard(token);

    // Get UserPrivilegeRelatedOwner.
    UserPrivilegeDetails userPrivilegeDetails =
        PrivilegeValidationUtil.getUserPrivilegeDetails(
            ProfileConstants.CXM_FLOW_TRACEABILITY, Privilege.LIST, true, true);
    // Get user filter preference.
    UserFilterPreferenceDto filterPreference = this.getUserFilterPreferenceDto(token);

    final var depositModes =
        List.of(
            DepositMode.BATCH, DepositMode.PORTAL, DepositMode.VIRTUAL_PRINTER, DepositMode.API);
    final var subTotal =
        new ConcurrentHashMap<>(
            depositModes.stream()
                .collect(Collectors.toMap(depositMode -> depositMode, depositMode -> 0L)));

    List<FlowDepositMode> flowDepositModes =
        this.flowTraceabilityReportRepository.countTotalFlowPerDepositModes(
            userPrivilegeDetails.getRelatedOwners(),
            filterPreference.getCustomStartDate(),
            filterPreference.getCustomEndDate(),
            requestedAt);

    // Calculate total foreach deposit mode
    depositModes.forEach(
        depositMode ->
            flowDepositModes.stream()
                .filter(mode -> mode.getKey().equalsIgnoreCase(depositMode.getValue()))
                .findFirst()
                .ifPresent(entity -> subTotal.put(depositMode, entity.getValue())));

    final var totalDocs = flowDepositModes.stream().mapToLong(FlowDepositMode::getValue).sum();

    return subTotal.entrySet().stream()
        .map(depositEntry -> this.calculateAndMap(totalDocs, depositEntry))
        .sorted((a, b) -> b.getKey().compareToIgnoreCase(a.getKey()))
        .collect(Collectors.toList());
  }

  private DepositModeResponseDto calculateAndMap(
      Long totalDoc, Entry<DepositMode, Long> depositEntry) {
    final DepositMode depositModeEnum = depositEntry.getKey();
    final double depositTotal = depositEntry.getValue();
    double percentage = 0;
    if (totalDoc != 0) {
      percentage = (depositTotal / totalDoc) * 100.0;
      percentage = Math.round(percentage * scale) / scale;
    }
    return new DepositModeResponseDto(depositModeEnum.getKey(), percentage);
  }

  /**
   * Get user filter preference.
   *
   * @param token - value of {@link String}.
   * @return - object of {@link UserFilterPreferenceDto}.
   */
  UserFilterPreferenceDto getUserFilterPreferenceDto(String token) {
    return DateHelper.mappingUserFilterPreferenceDto(
        this.filterPreferenceService.getFilterPreference(token));
  }

  private List<FlowDocumentReportMetadata> getColumns() {
    List<FlowDocumentReportMetadata> flowDocumentReportMetadata = new ArrayList<>();
    for (var metadata : FlowDocumentReportMetaData.values()) {
      flowDocumentReportMetadata.add(
          FlowDocumentReportMetadata.builder()
              .col(metadata.getValue())
              .label(metadata.getKey())
              .type(metadata.getDataType())
              .build());
    }

    return flowDocumentReportMetadata;
  }

  public List<DepositModeResponseDto> getByGroupSubChannel(String token, Date requestedAt) throws DomainException {
    this.checkAdminCannotSeeDashboard(token);

    // Get UserPrivilegeRelatedOwner.
    UserPrivilegeDetails userPrivilegeDetails =
        PrivilegeValidationUtil.getUserPrivilegeDetails(
            ProfileConstants.CXM_FLOW_TRACEABILITY, Privilege.LIST, true, true);

    UserFilterPreferenceDto userFilterPreferenceDto = this.getUserFilterPreferenceDto(token);

    var reportGroup = this.getDefault();
    final var totalDoc = new AtomicLong(0L);

    var conMap =
        new ConcurrentHashMap<>(
            reportGroup.keySet().stream()
                .collect(Collectors.toMap(channel -> channel, channel -> 0L)));

    reportGroup.forEach(
        (k, subChannel) -> {
          long count =
              this.flowReportRepository.countDocumentPerChannel(
                  userPrivilegeDetails.getRelatedOwners(),
                  subChannel,
                  userFilterPreferenceDto.getCustomStartDate(),
                  userFilterPreferenceDto.getCustomEndDate(),
                  requestedAt);

          conMap.put(k, count);
          totalDoc.getAndAdd(count);
        });

    return conMap.entrySet().stream()
        .map(channel -> this.mapToChannelResult(totalDoc, channel))
        .sorted(Comparator.comparingInt(channel -> ReportOrderMapper.orderLabel(channel.getKey())))
        .collect(Collectors.toList());
  }

  public FlowDocumentReportDto getFlowDocumentReport(String token, Date requestedAt) throws DomainException {
    this.checkAdminCannotSeeDashboard(token);

    UserPrivilegeDetails userPrivilegeDetails =
        PrivilegeValidationUtil.getUserPrivilegeDetails(
            ProfileConstants.CXM_FLOW_TRACEABILITY, Privilege.LIST, true, true);
    UserFilterPreferenceDto userFilterPreferenceDto = this.getUserFilterPreferenceDto(token);

    var reportGroup = this.getDefault();

    List<FlowDocumentReportData> rows = new ArrayList<>();

    // calculate each sub channel row
    reportGroup.forEach(
        (k, subChannel) -> {
          var groupResult =
              this.flowReportRepository.reportDocument(
                  userPrivilegeDetails.getRelatedOwners(),
                  subChannel,
                  userFilterPreferenceDto.getCustomStartDate(),
                  userFilterPreferenceDto.getCustomEndDate(),
                  requestedAt);

          var row = new FlowDocumentReportData();
          groupResult.forEach(
              status -> {
                row.setVolume(row.getVolume() + status.getTotal());

                if (FlowDocumentStatus.TO_VALIDATE.getValue().equals(status.getStatus())) {
                  row.setToValidate(status.getTotal());
                } else if (FlowDocumentStatus.IN_PROGRESS.getValue().equals(status.getStatus())) {
                  row.setInProgress(status.getTotal());
                } else if (FlowDocumentStatus.SCHEDULED.getValue().equals(status.getStatus())) {
                  row.setScheduled(status.getTotal());
                } else if (FlowDocumentStatus.COMPLETED.getValue().equals(status.getStatus())) {
                  row.setTerminated(status.getTotal());
                } else if (FlowDocumentStatus.IN_ERROR.getValue().equals(status.getStatus())
                    || FlowDocumentStatus.CANCELED.getValue().equals(status.getStatus())
                    || FlowDocumentStatus.REFUSED.getValue().equals(status.getStatus())) {
                  row.setOther(status.getTotal() + row.getOther());
                }
              });

          row.setCompletedPercentage(
              Math.round(((row.getTerminated() / Math.max(row.getVolume() * 1.0, 1)) * 100) * scale)
                  / scale);
          row.setSubChannel(ReportOrderMapper.mapCapitalizeLabel(k));

          rows.add(row);
        });

    // Specific order: Portal, Email, SMS
    var sortedRows =
        rows.stream()
            .sorted(
                Comparator.comparingInt(row -> ReportOrderMapper.orderLabel(row.getSubChannel())))
            .collect(Collectors.toList());

    // calculate total row
    var totalRows = new FlowDocumentReportData();
    sortedRows.forEach(
        item -> {
          totalRows.setVolume(totalRows.getVolume() + item.getVolume());
          totalRows.setToValidate(totalRows.getToValidate() + item.getToValidate());
          totalRows.setScheduled(totalRows.getScheduled() + item.getScheduled());
          totalRows.setInProgress(totalRows.getInProgress() + item.getInProgress());
          totalRows.setOther(totalRows.getOther() + item.getOther());
          totalRows.setTerminated(totalRows.getTerminated() + item.getTerminated());
        });

    totalRows.setCompletedPercentage(
        Math.round(
            ((totalRows.getTerminated() / Math.max(totalRows.getVolume() * 1.0, 1)) * 100)
                * scale)
            / scale);
    totalRows.setSubChannel("TOTAL");
    sortedRows.add(totalRows);

    FlowDocumentReportDto flowDocumentReportDto = new FlowDocumentReportDto();
    flowDocumentReportDto.setResult(sortedRows);
    flowDocumentReportDto.setMetaData(getColumns());

    return flowDocumentReportDto;
  }

  public List<FlowDocumentEvolutionReportData> getFlowDocumentEvolutionReport(String token, Date requestedAt)
      throws DomainException {

    this.checkAdminCannotSeeDashboard(token);

    // Get UserPrivilegeRelatedOwner.
    UserPrivilegeDetails userPrivilegeDetails =
        PrivilegeValidationUtil.getUserPrivilegeDetails(
            ProfileConstants.CXM_FLOW_TRACEABILITY, Privilege.LIST, true, true);

    UserFilterPreferenceDto userFilterPreferenceDto = this.getUserFilterPreferenceDto(token);

    var reportGroup = this.getDefault();
    List<FlowDocumentEvolutionReportData> rows = new ArrayList<>();

    var dateformat = new SimpleDateFormat("yyyy-MM-dd");

    reportGroup.forEach(
        (channel, subChannel) -> {
          List<FlowDocumentTimeSeriesProjection> groupResult =
              this.flowReportRepository.reportDocumentTimeSeries(
                  userPrivilegeDetails.getRelatedOwners(),
                  subChannel,
                  userFilterPreferenceDto.getCustomStartDate(),
                  userFilterPreferenceDto.getCustomEndDate(),
                  requestedAt);

          var timelines =
              ReportOrderMapper.listBetweenTwoDate(
                      userFilterPreferenceDto.getCustomStartDate(),
                      userFilterPreferenceDto.getCustomEndDate())
                  .stream()
                  .map(
                      date ->
                          FlowDocumentEvolutionReportData.createValue(dateformat.format(date), 0L))
                  .collect(Collectors.toList());

          groupResult.forEach(
              result -> {
                var found =
                    timelines.stream()
                        .filter(x -> x.getDate().equals(result.getTimeline().toString()))
                        .findAny();
                found.ifPresent(valueByDate -> valueByDate.setValue(result.getCounter()));
              });

          var reportData =
              FlowDocumentEvolutionReportData.builder()
                  .data(timelines)
                  .channel(ReportOrderMapper.mapCapitalizeLabel(channel))
                  .build();

          rows.add(reportData);
        });

    return rows.stream()
        .sorted(Comparator.comparingInt(r -> ReportOrderMapper.orderLabel(r.getChannel())))
        .collect(Collectors.toList());
  }

  private Map<String, List<String>> getDefault() {
    var reportGroup = new HashMap<String, List<String>>();
    reportGroup.put(FlowDocumentChannelConstant.POSTAL, FlowDocumentSubChannel.postalSubChannels());
    reportGroup.put(
        FlowDocumentSubChannel.EMAIL.getValue(), List.of(FlowDocumentSubChannel.EMAIL.getValue()));
    reportGroup.put(
        FlowDocumentSubChannel.SMS.getValue(), List.of(FlowDocumentSubChannel.SMS.getValue()));
    return reportGroup;
  }

  private DepositModeResponseDto mapToChannelResult(
      AtomicLong totalDoc, Entry<String, Long> entry) {
    double percentage = 0;
    final double countGroup = entry.getValue();

    if (totalDoc.get() != 0) {
      percentage = (countGroup / totalDoc.get()) * 100.0;
      percentage = Math.round(percentage * scale) / scale;
    }

    return new DepositModeResponseDto(
        ReportOrderMapper.mapCapitalizeLabel(entry.getKey()), percentage);
  }

  // Return 204 no-content for admin user
  private void checkAdminCannotSeeDashboard(String token) throws DomainException {
    UserInfoResponse userInfoResponse = this.profileFeignClient.checkUserIsAdmin(token);
    if (userInfoResponse.isPlatformAdmin()) {
      return;
    }

    if (userInfoResponse.isAdmin()) {
      throw new DomainException(AnalyticsError.ADMIN_CANNOT_VIEW_REPORT, 204);
    }
  }
}
