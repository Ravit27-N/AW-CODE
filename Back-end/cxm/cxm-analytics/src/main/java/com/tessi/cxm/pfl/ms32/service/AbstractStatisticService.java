package com.tessi.cxm.pfl.ms32.service;

import com.tessi.cxm.pfl.ms32.constant.AnalyticsConstants;
import com.tessi.cxm.pfl.ms32.constant.DepositModeResponseDto;
import com.tessi.cxm.pfl.ms32.constant.ProductionDetailMetaData;
import com.tessi.cxm.pfl.ms32.dto.DocumentDetailSummary;
import com.tessi.cxm.pfl.ms32.dto.DocumentStatistics;
import com.tessi.cxm.pfl.ms32.dto.DocumentSummary;
import com.tessi.cxm.pfl.ms32.dto.DocumentTotalDto;
import com.tessi.cxm.pfl.ms32.dto.FlowDocumentProductionDetail;
import com.tessi.cxm.pfl.ms32.dto.GlobalStatisticRequestFilter;
import com.tessi.cxm.pfl.ms32.dto.StatisticRequestFilter;
import com.tessi.cxm.pfl.ms32.dto.ProductionDetails;
import com.tessi.cxm.pfl.ms32.dto.ProductionDetailsFillers;
import com.tessi.cxm.pfl.ms32.entity.FlowTraceabilityReport;
import com.tessi.cxm.pfl.ms32.exception.ChannelNotFoundException;
import com.tessi.cxm.pfl.ms32.exception.DateInvalidException;
import com.tessi.cxm.pfl.ms32.exception.FillerNotFoundException;
import com.tessi.cxm.pfl.ms32.exception.SubChannelNotFoundException;
import com.tessi.cxm.pfl.ms32.repository.FlowTraceabilityReportRepository;
import com.tessi.cxm.pfl.ms32.service.specification.StatisticSpecification;
import com.tessi.cxm.pfl.ms32.util.AnalyticsCalculatorUtils;
import com.tessi.cxm.pfl.shared.exception.BadRequestException;
import com.tessi.cxm.pfl.shared.model.SharedClientFillersDTO;
import com.tessi.cxm.pfl.shared.model.UserPrivilegeDetails;
import com.tessi.cxm.pfl.shared.model.setting.criteria.CriteriaDistributionsResponse;
import com.tessi.cxm.pfl.shared.model.setting.criteria.DigitalPreference;
import com.tessi.cxm.pfl.shared.model.setting.criteria.Preference;
import com.tessi.cxm.pfl.shared.service.ServiceUtils;
import com.tessi.cxm.pfl.shared.service.restclient.ProfileFeignClient;
import com.tessi.cxm.pfl.shared.service.restclient.SettingFeignClient;
import com.tessi.cxm.pfl.shared.utils.CustomCollectionUtils;
import com.tessi.cxm.pfl.shared.utils.FlowDocumentChannelConstant;
import com.tessi.cxm.pfl.shared.utils.FlowDocumentStatus;
import com.tessi.cxm.pfl.shared.utils.FlowDocumentSubChannel;
import com.tessi.cxm.pfl.shared.utils.PrivilegeValidationUtil;
import com.tessi.cxm.pfl.shared.utils.ProfileConstants;
import com.tessi.cxm.pfl.shared.utils.ProfileConstants.Privilege.Statistic;
import com.tessi.cxm.pfl.shared.utils.TupleUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.persistence.Tuple;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

@Slf4j
@Getter
@RequiredArgsConstructor
public abstract class AbstractStatisticService implements ServiceUtils {
  protected static final String CATEGORY_UNAUTHORIZED_TO_ACCESS =
      "Category unauthorized to access: ";
  private static final String NOT_ACTIVATED_MESSAGE = " is not activated";
  protected static final List<String> SUPPORTED_DIGITAL_CATEGORIES =
      List.of(FlowDocumentSubChannel.EMAIL.getValue(), FlowDocumentSubChannel.SMS.getValue());
  private final FlowTraceabilityReportRepository flowTraceabilityReportRepository;
  private final SettingFeignClient settingFeignClient;

  private final ProfileFeignClient profileFeignClient;

  protected DocumentSummary fetchVolumeReceiveDetails(GlobalStatisticRequestFilter requestFilter) {
    StatisticRequestFilter postalStatisticRequestFilter =
        mapGlobalStatisticRequestToPostal(requestFilter);
    return this.fetchVolumeReceiveDetails(postalStatisticRequestFilter);
  }

  private static StatisticRequestFilter mapGlobalStatisticRequestToPostal(
      GlobalStatisticRequestFilter requestFilter) {
    StatisticRequestFilter postalStatisticRequestFilter = new StatisticRequestFilter();
    BeanUtils.copyProperties(requestFilter, postalStatisticRequestFilter);
    postalStatisticRequestFilter.setGlobalFillers(true);
    return postalStatisticRequestFilter;
  }

  protected DocumentSummary fetchVolumeReceiveDetails(StatisticRequestFilter requestFilter) {
    List<String> statuses =
        List.of(
            FlowDocumentStatus.IN_PROGRESS.getValue(),
            FlowDocumentStatus.COMPLETED.getValue(),
            FlowDocumentStatus.IN_ERROR.getValue());

    requestFilter.setStatuses(statuses);
    List<DocumentTotalDto> channelTotals =
        this.flowTraceabilityReportRepository.reportDocument(requestFilter);
    return this.mapVolumeReceived(channelTotals);
  }

  protected void validateAndResolve(GlobalStatisticRequestFilter requestFilter) {
    UserPrivilegeDetails userPrivilegeDetails =
        PrivilegeValidationUtil.getUserPrivilegeDetails(
            ProfileConstants.CXM_STATISTIC_REPORT, Statistic.GENERATE_STATISTIC, true, true);
    requestFilter.setOwnerIds(userPrivilegeDetails.getRelatedOwners());
    validateAndResolveGlobal(requestFilter);
  }

  protected void validateAndResolveGlobal(GlobalStatisticRequestFilter requestFilter) {
    CriteriaDistributionsResponse criteriaDistributionsResponse =
        this.getSettingFeignClient().getCriteriaDistributions(null, getBearerPrefixedAuthToken());

    List<SharedClientFillersDTO> clientFillers =
        this.profileFeignClient.getAllClientFillers(getBearerPrefixedAuthToken());
    requestFilter.setClientFillers(clientFillers);
    this.validateGlobalRequest(requestFilter, clientFillers);

    this.resolveRequestCategories(requestFilter, criteriaDistributionsResponse);

    if (CollectionUtils.isEmpty(requestFilter.getFillers())) {
      requestFilter.setFillers(
          clientFillers.stream().map(SharedClientFillersDTO::getKey).collect(Collectors.toList()));
    }
  }

  protected void validateAndResolve(StatisticRequestFilter requestFilter) {
    UserPrivilegeDetails userPrivilegeDetails =
        PrivilegeValidationUtil.getUserPrivilegeDetails(
            ProfileConstants.CXM_STATISTIC_REPORT, Statistic.GENERATE_STATISTIC, true, true);
    requestFilter.setOwnerIds(userPrivilegeDetails.getRelatedOwners());
    this.validateAndResolveSpecific(requestFilter);
  }

  protected void validateAndResolveSpecific(StatisticRequestFilter requestFilter) {
    CriteriaDistributionsResponse criteriaDistributions =
        this.getSettingFeignClient().getCriteriaDistributions(null, getBearerPrefixedAuthToken());
    List<SharedClientFillersDTO> clientFillers =
        this.profileFeignClient.getAllClientFillers(getBearerPrefixedAuthToken());
    requestFilter.setClientFillers(clientFillers);
    if (requestFilter.getStartDate().after(requestFilter.getEndDate())) {
      throw new DateInvalidException("endDate cannot be less than startDate");
    }

    this.validatePostalChannelAndCategories(requestFilter);
    this.validatePostalCriteria(requestFilter, criteriaDistributions);
    this.validateGroupFillers(requestFilter, clientFillers);

    this.resolvePostalRequest(requestFilter);
  }

  protected void validatePostalChannelAndCategories(StatisticRequestFilter requestFilter) {
    if (CollectionUtils.isEmpty(requestFilter.getChannels())) {
      throw new BadRequestException("Channel must not be empty");
    }
    this.validateRequestingChannels(requestFilter.getChannels());

    if (!CollectionUtils.isEmpty(requestFilter.getChannels())
        && requestFilter.getChannels().size() != 1) {
      throw new BadRequestException("Only one channel is supported");
    }

    if (FlowDocumentChannelConstant.POSTAL.equals(requestFilter.getChannels().get(0))
        && !CollectionUtils.isEmpty(requestFilter.getCategories())
        && !FlowDocumentSubChannel.allPostalChannel(requestFilter.getCategories(), true)) {
      throw new BadRequestException("Some categories are not Postal");
    } else if (FlowDocumentChannelConstant.DIGITAL.equals(requestFilter.getChannels().get(0))) {
      if (CollectionUtils.isEmpty(requestFilter.getCategories())) {
        throw new BadRequestException("Digital category is not provided");
      }
      if (!CollectionUtils.isEmpty(requestFilter.getCategories())
          && requestFilter.getCategories().size() != 1) {
        throw new BadRequestException("Only one sub-channel is supported");
      }
      if (!SUPPORTED_DIGITAL_CATEGORIES.contains(requestFilter.getCategories().get(0))) {
        throw new BadRequestException("Either Email or SMS sub-channel is supported");
      }
    }
  }

  protected void validatePostalCriteria(
      StatisticRequestFilter requestFilter, CriteriaDistributionsResponse refCriteria) {
    var channel = requestFilter.getChannels().get(0);
    Preference preference = refCriteria.getCriteria(channel);

    if (preference == null) {
      throw new BadRequestException(
          "Channel " + requestFilter.getChannels().get(0) + NOT_ACTIVATED_MESSAGE);
    }
    if (!preference.isEnabled() || !preference.isActive()) {
      throw new BadRequestException(
          "Channel " + requestFilter.getChannels().get(0) + NOT_ACTIVATED_MESSAGE);
    }

    if (FlowDocumentChannelConstant.POSTAL.equalsIgnoreCase(channel)) {
      return;
    }

    String subChannel = requestFilter.getCategories().get(0);
    if (FlowDocumentSubChannel.SMS.getValue().equalsIgnoreCase(subChannel)) {
      subChannel = "Sms";
    }

    Preference preferenceSubChannel = refCriteria.getCriteria(subChannel);
    if (preferenceSubChannel == null) {
      throw new BadRequestException("Sub-channel " + subChannel + NOT_ACTIVATED_MESSAGE);
    }

    if (!preferenceSubChannel.isEnabled() || !preferenceSubChannel.isActive()) {
      throw new BadRequestException("Sub-channel " + subChannel + NOT_ACTIVATED_MESSAGE);
    }
  }

  protected void validateGroupFillers(
      StatisticRequestFilter requestFilter, List<SharedClientFillersDTO> clientFillers) {
    if (!CollectionUtils.isEmpty(requestFilter.getFillers())
        && requestFilter.getFillers().size() > 1) {
      throw new BadRequestException("First filler has more than 1 keys");
    }
    var groupFillers = requestFilter.getGroupFillers();
    if (!CollectionUtils.isEmpty(groupFillers)) {
      if (groupFillers.size() > 3) {
        throw new BadRequestException("Fillers support only 3 fillers");
      }

      if (!CustomCollectionUtils.hasUniqueElement(groupFillers)) {
        throw new BadRequestException("Fillers must not use more than 1 time");
      }
      var fillersOrderNum = (groupFillers.size() * (groupFillers.size() + 1)) / 2;
      if (fillersOrderNum != requestFilter.getGroupFillersOffset()) {
        throw new BadRequestException("Filler grouping is not in order");
      }
    }

    List<String> configuredFillers =
        clientFillers.stream().map(SharedClientFillersDTO::getKey).collect(Collectors.toList());
    boolean notInConfiguredFillers =
        groupFillers.stream().anyMatch(filler -> !configuredFillers.contains(filler));
    if (notInConfiguredFillers) {
      throw new BadRequestException("Some fillers are not supported");
    }
  }

  protected DocumentSummary mapVolumeReceived(List<DocumentTotalDto> channelTotals) {
    DocumentSummary volumeReceived = new DocumentSummary();
    mapDocumentSummary(channelTotals, volumeReceived);
    return volumeReceived;
  }

  protected void resolveRequestCategories(
      GlobalStatisticRequestFilter requestFilter,
      CriteriaDistributionsResponse refCriteriaDistributions) {
    if (CollectionUtils.isEmpty(requestFilter.getCategories())) {
      var categoriesFilter = new ArrayList<String>();

      var isDigitalRequest =
          requestFilter.getChannels().stream()
              .anyMatch(FlowDocumentChannelConstant.DIGITAL::equalsIgnoreCase);
      if (requestFilter.getChannels().isEmpty() || isDigitalRequest) {
        refCriteriaDistributions.getPreferences().stream()
            .filter(
                preference ->
                    preference.getName().equals(FlowDocumentChannelConstant.DIGITAL)
                        && preference.isActive())
            .findFirst()
            .ifPresent(
                preference -> {
                  DigitalPreference digitalPreference = (DigitalPreference) preference;
                  List<String> activatedSubDigitalPref =
                      digitalPreference.getPreferences().stream()
                          .filter(
                              subDigitalPref ->
                                  subDigitalPref.isEnabled() && subDigitalPref.isActive())
                          .map(subDigitalPref -> subDigitalPref.getName().toLowerCase())
                          .collect(Collectors.toList());
                  categoriesFilter.addAll(activatedSubDigitalPref);
                });
      }

      var isPostalRequest =
          requestFilter.getChannels().stream()
              .anyMatch(FlowDocumentChannelConstant.POSTAL::equalsIgnoreCase);
      if (requestFilter.getChannels().isEmpty() || isPostalRequest) {
        Preference postalPref =
            refCriteriaDistributions.getCriteria(FlowDocumentChannelConstant.POSTAL);
        if (postalPref != null && postalPref.isActive()) {
          categoriesFilter.addAll(
              FlowDocumentSubChannel.postalSubChannels().stream()
                  .map(String::toLowerCase)
                  .collect(Collectors.toList()));
        }
      }
      requestFilter.setCategories(categoriesFilter);
    } else {
      removeNonActiveCategories(requestFilter, refCriteriaDistributions);
    }
  }

  private void removeNonActiveCategories(
      GlobalStatisticRequestFilter requestFilter,
      CriteriaDistributionsResponse refCriteriaDistributions) {
    final var activeCategories = new ArrayList<String>();
    requestFilter
        .getCategories()
        .forEach(
            category -> {
              if (FlowDocumentSubChannel.isPostalChannel(category, true)) {
                Preference postalPref =
                    refCriteriaDistributions.getCriteria(FlowDocumentChannelConstant.POSTAL);
                if (postalPref != null && postalPref.isActive()) {
                  activeCategories.add(category);
                }
              } else if (FlowDocumentSubChannel.isDigitalChannel(category)) {
                DigitalPreference digitalPref =
                    (DigitalPreference)
                        refCriteriaDistributions.getCriteria(FlowDocumentChannelConstant.DIGITAL);
                if (digitalPref.isActive()
                    && digitalPref.getPreferences().stream()
                        .anyMatch(
                            preference ->
                                preference.getName().equalsIgnoreCase(category)
                                    && preference.isActive()
                                    && preference.isEnabled())) {
                  activeCategories.add(category);
                }
              }
            });
    requestFilter.setCategories(activeCategories);
  }

  protected void resolvePostalRequest(StatisticRequestFilter requestFilter) {
    if (FlowDocumentChannelConstant.POSTAL.equals(requestFilter.getChannels().get(0))
        && CollectionUtils.isEmpty(requestFilter.getCategories())) {
      requestFilter.setCategories(FlowDocumentSubChannel.postalSubChannels());
    }
  }

  protected void validateGlobalRequest(
      GlobalStatisticRequestFilter filter, List<SharedClientFillersDTO> clientFillers) {

    // validate date

    if (filter.getStartDate().after(filter.getEndDate())) {
      throw new DateInvalidException("endDate cannot be less than startDate");
    }
    // validate channel not support and Unauthorized
    if (!CollectionUtils.isEmpty(filter.getChannels())) {
      this.validateRequestingChannels(filter.getChannels());
    }
    // validate subChannel not support and Unauthorized
    if (!CollectionUtils.isEmpty(filter.getCategories())) {
      this.validateRequestingCategories(filter.getCategories());
    }
    // validate filler not support
    if (!StringUtils.isEmpty(filter.getSearchByFiller())
        && !CollectionUtils.isEmpty(filter.getFillers())) {
      this.validateRequestingFillers(filter.getFillers(), clientFillers);
    }
  }

  protected long getTotalPND(GlobalStatisticRequestFilter requestFilter) {
    StatisticRequestFilter postalStatisticRequestFilter =
        mapGlobalStatisticRequestToPostal(requestFilter);
    return this.getTotalPND(postalStatisticRequestFilter);
  }

  protected long getTotalPND(StatisticRequestFilter requestFilter) {
    List<String> postalNonDistributedStatus =
        FlowDocumentStatus.getPNDStatus().stream()
            .map(String::toLowerCase)
            .collect(Collectors.toList());
    requestFilter.setStatuses(postalNonDistributedStatus);
    List<ProductionDetails> productionDetails =
        this.flowTraceabilityReportRepository.reportProductionDetails(requestFilter);
    List<ProductionDetails> postalProductionDetails =
        productionDetails.stream()
            .collect(Collectors.groupingBy(ProductionDetails::getChannel))
            .get(FlowDocumentChannelConstant.POSTAL);
    if (CollectionUtils.isEmpty(postalProductionDetails)) {
      return 0;
    }
    return postalProductionDetails.stream().mapToLong(ProductionDetails::getTotal).sum();
  }

  protected DocumentSummary getTotalMND(GlobalStatisticRequestFilter requestFilter) {
    StatisticRequestFilter postalStatisticRequestFilter =
        mapGlobalStatisticRequestToPostal(requestFilter);
    return getTotalMND(postalStatisticRequestFilter);
  }

  protected DocumentSummary getTotalMND(StatisticRequestFilter requestFilter) {
    List<String> digitalNonDistributedStatus =
        FlowDocumentStatus.getMNDStatus().stream()
            .map(String::toLowerCase)
            .collect(Collectors.toList());
    requestFilter.setStatuses(digitalNonDistributedStatus);
    List<ProductionDetails> productionDetails =
        this.flowTraceabilityReportRepository.reportProductionDetails(requestFilter);
    List<ProductionDetails> digitalProductionDetails =
        productionDetails.stream()
            .collect(Collectors.groupingBy(ProductionDetails::getChannel))
            .get(FlowDocumentChannelConstant.DIGITAL);
    if (CollectionUtils.isEmpty(digitalProductionDetails)) {
      return new DocumentSummary();
    }

    DocumentSummary volumeReceiveDTO = new DocumentSummary();
    digitalProductionDetails.forEach(
        proDetail -> {
          String key = proDetail.getSubChannel();
          if (FlowDocumentSubChannel.SMS.getValue().equalsIgnoreCase(key)
              && digitalNonDistributedStatus.contains(proDetail.getStatus().toLowerCase())) {
            var docCountPerKey = volumeReceiveDTO.getOrDefault(key, 0L) + proDetail.getTotal();
            volumeReceiveDTO.put(key, docCountPerKey);
            volumeReceiveDTO.countTotal(docCountPerKey);
          }
          if (FlowDocumentSubChannel.EMAIL.getValue().equalsIgnoreCase(key)
              && digitalNonDistributedStatus.contains(proDetail.getStatus().toLowerCase())) {
            var docCountPerKey = volumeReceiveDTO.getOrDefault(key, 0L) + proDetail.getTotal();
            volumeReceiveDTO.put(key, docCountPerKey);
            volumeReceiveDTO.countTotal(docCountPerKey);
          }
        });
    return volumeReceiveDTO;
  }

  protected List<DocumentTotalDto> getReportDocumentProcess(
      GlobalStatisticRequestFilter requestFilter) {
    StatisticRequestFilter postalStatisticRequestFilter =
        mapGlobalStatisticRequestToPostal(requestFilter);
    return getReportDocumentProcess(postalStatisticRequestFilter);
  }
  /**
   * Generic method to get total of document processes.
   *
   * @param requestFilter - object of {@link GlobalStatisticRequestFilter}.
   * @return - collection of {@link DocumentTotalDto}.
   */
  protected List<DocumentTotalDto> getReportDocumentProcess(StatisticRequestFilter requestFilter) {
    List<String> statuses =
        List.of(FlowDocumentStatus.COMPLETED.getValue(), FlowDocumentStatus.IN_ERROR.getValue());
    requestFilter.setStatuses(statuses);
    return this.flowTraceabilityReportRepository.reportDocument(requestFilter);
  }

  protected DocumentSummary getDocumentProcess(GlobalStatisticRequestFilter requestFilter) {
    StatisticRequestFilter postalStatisticRequestFilter =
        mapGlobalStatisticRequestToPostal(requestFilter);
    return getDocumentProcess(postalStatisticRequestFilter);
  }

  protected DocumentSummary getDocumentProcess(StatisticRequestFilter requestFilter) {
    DocumentSummary processedDocSummary = new DocumentSummary();
    List<DocumentTotalDto> documents = getReportDocumentProcess(requestFilter);

    if (!documents.isEmpty()) {
      mapDocumentSummary(documents, processedDocSummary);
    }
    return processedDocSummary;
  }

  protected List<FlowDocumentProductionDetail> getDefaultProductionDetails(
      GlobalStatisticRequestFilter requestFilter) {
    List<FlowDocumentProductionDetail> flowDocumentProductionDetails = new ArrayList<>();
    this.getOrderChannels(requestFilter)
        .forEach(
            channel -> {
              FlowDocumentProductionDetail flowDocumentProductionDetail =
                  new FlowDocumentProductionDetail();
              flowDocumentProductionDetail.setChannel(channel);
              flowDocumentProductionDetails.add(flowDocumentProductionDetail);
            });
    return flowDocumentProductionDetails;
  }

  protected DocumentSummary fetchTotalInProgress(GlobalStatisticRequestFilter requestFilter) {
    DocumentSummary progressDocSummary = new DocumentSummary();
    final String status = FlowDocumentStatus.IN_PROGRESS.getValue().toLowerCase();
    requestFilter.setStatuses(Collections.singletonList(status));
    StatisticRequestFilter postalStatisticRequestFilter =
        mapGlobalStatisticRequestToPostal(requestFilter);
    List<DocumentTotalDto> productionDetails =
        this.flowTraceabilityReportRepository.reportDocument(postalStatisticRequestFilter);
    if (!CollectionUtils.isEmpty(productionDetails)) {
      mapDocumentSummary(productionDetails, progressDocSummary);
    }
    return progressDocSummary;
  }

  private void mapDocumentSummary(
      List<DocumentTotalDto> productionDetails, DocumentSummary volumeReceiveDTO) {
    productionDetails.forEach(
        volumeReceivedTotal -> {
          var key = volumeReceivedTotal.getChannel();
          var digitalSubChannels =
              List.of(
                  FlowDocumentSubChannel.EMAIL.getValue(), FlowDocumentSubChannel.SMS.getValue());
          if (digitalSubChannels.stream()
              .anyMatch(
                  digitalSubChannel ->
                      digitalSubChannel.equalsIgnoreCase(volumeReceivedTotal.getSubChannel()))) {
            key = volumeReceivedTotal.getSubChannel();
          }

          var docCountPerKey =
              volumeReceiveDTO.getOrDefault(key, 0L) + volumeReceivedTotal.getTotal();

          volumeReceiveDTO.put(key, docCountPerKey);
          volumeReceiveDTO.countTotal(volumeReceivedTotal.getTotal());
        });
  }

  private void validateRequestingChannels(List<String> reqChannel) {
    reqChannel.forEach(
        channel -> {
          if (!channel.equals(FlowDocumentChannelConstant.DIGITAL)
              && !channel.equals(FlowDocumentChannelConstant.POSTAL)) {
            throw new ChannelNotFoundException("Channel not support:" + channel);
          }
        });
  }

  private void validateRequestingCategories(List<String> reqCategories) {

    reqCategories.forEach(
        category -> {
          // check category support
          if (StringUtils.isEmpty(FlowDocumentSubChannel.subChannelContain(category).getValue())) {
            throw new SubChannelNotFoundException("Category not support:" + category);
          }
        });
  }

  private void validateRequestingFillers(
      List<String> fillers, List<SharedClientFillersDTO> clientFillers) {

    var notFound =
        fillers.stream()
            .anyMatch(
                s ->
                    clientFillers.stream()
                        .noneMatch(
                            sharedClientFillersDTO -> sharedClientFillersDTO.getKey().equals(s)));
    if (notFound) {
      throw new FillerNotFoundException("Filler not found.");
    }
  }

  protected List<String> getOrderChannels(GlobalStatisticRequestFilter requestFilter) {
    List<String> orders = new ArrayList<>();
    if (requestFilter.getCategories().stream()
        .anyMatch(category -> FlowDocumentSubChannel.isPostalChannel(category, true))) {
      orders.add("Postal");
    }
    if (requestFilter.getCategories().stream().anyMatch(s -> s.equalsIgnoreCase("Email"))) {
      orders.add("Email");
    }
    if (requestFilter.getCategories().stream().anyMatch(s -> s.equalsIgnoreCase("SMS"))) {
      orders.add("SMS");
    }
    return orders;
  }

  protected List<ProductionDetailsFillers> documentDetailSummaryPNDMND(
      StatisticRequestFilter requestFilter) {
    var querySpec = StatisticSpecification.generatePNDMNDPostSpec(requestFilter);
    return this.getFlowTraceabilityReportRepository()
        .findAll(querySpec, FlowTraceabilityReport.class, Tuple.class)
        .stream()
        .map(ProductionDetailsFillers::new)
        .collect(Collectors.toList());
  }

  protected DocumentDetailSummary summaryDocsByFillerGrouping(
      StatisticRequestFilter requestFilter) {

    var querySpec = StatisticSpecification.generatePostSpecs(requestFilter);
    List<ProductionDetailsFillers> productionDetailsFillers =
        this.getFlowTraceabilityReportRepository()
            .findAll(querySpec, FlowTraceabilityReport.class, Tuple.class)
            .stream()
            .map(ProductionDetailsFillers::new)
            .collect(Collectors.toList());
    List<DocumentStatistics> documentStatisticsLevel = new ArrayList<>();
    // return non document fillers.
    if (productionDetailsFillers.isEmpty()) {
      return new DocumentDetailSummary(
          requestFilter.getGroupFillers().size(),
          new ArrayList<>(List.of(getDocumentStatistics(null, 0, new ArrayList<>(), 0, 0, 0))));
    }
    // count all document without fillers.
    if (CollectionUtils.isEmpty(requestFilter.getGroupFillers())) {
      long totalDocument =
          productionDetailsFillers.stream().mapToLong(ProductionDetailsFillers::getTotal).sum();
      long totalDocInProgress = this.getTotalDocInProgress(productionDetailsFillers);
      long totalDocInProcess = this.getTotalDocInProcess(productionDetailsFillers);
      documentStatisticsLevel.add(
          getDocumentStatistics(
              null, 0, new ArrayList<>(), totalDocument, totalDocInProgress, totalDocInProcess));

      return new DocumentDetailSummary(0, documentStatisticsLevel);
    }
    int sizeOfGroupFillers = requestFilter.getGroupFillers().size();
    var groupByFiller = getGroupByFiller(productionDetailsFillers, sizeOfGroupFillers, 1);
    // count all document with fillers.

    groupByFiller.forEach(
        (fillerGroupKey1, prodFillerGroup1) -> {
          List<DocumentStatistics> documentStatisticsLevel2 = new ArrayList<>();
          long totalVolumeReceivedGroup1 = getTotalPerKeyFiller(prodFillerGroup1);
          long totalDocInProgressGroup1 = this.getTotalDocInProgress(prodFillerGroup1);
          long totalDocInProcessGroup1 = this.getTotalDocInProcess(prodFillerGroup1);
          getGroupByFiller(prodFillerGroup1, sizeOfGroupFillers, 2)
              .forEach(
                  (fillerGroupKey2, prodFillerGroup2) -> {
                    List<DocumentStatistics> documentStatisticsLevel3 = new ArrayList<>();
                    long totalVolumeReceivedGroup2 = getTotalPerKeyFiller(prodFillerGroup2);
                    long totalDocInProgressGroup2 = this.getTotalDocInProgress(prodFillerGroup2);
                    long totalDocInProcessGroup2 = this.getTotalDocInProcess(prodFillerGroup2);
                    getGroupByFiller(prodFillerGroup2, sizeOfGroupFillers, 3)
                        .forEach(
                            (fillerGroupKey3, prodFillerGroup3) -> {
                              long totalVolumeReceivedGroup3 =
                                  getTotalPerKeyFiller(prodFillerGroup3);
                              long totalDocInProgressGroup3 =
                                  this.getTotalDocInProgress(prodFillerGroup3);
                              long totalDocInProcessGroup3 =
                                  this.getTotalDocInProcess(prodFillerGroup3);
                              DocumentStatistics documentStatistics =
                                  getDocumentStatistics(
                                      fillerGroupKey3,
                                      3,
                                      new ArrayList<>(),
                                      totalVolumeReceivedGroup3,
                                      totalDocInProgressGroup3,
                                      totalDocInProcessGroup3);
                              documentStatisticsLevel3.add(documentStatistics);
                            });

                    var sortedDocStatG3 =
                        documentStatisticsLevel3.stream()
                            .sorted(Comparator.comparing(DocumentStatistics::getFiller))
                            .collect(Collectors.toList());
                    reOrderDocumentIfBlankIsPresent(sortedDocStatG3);
                    DocumentStatistics documentStatistics =
                        getDocumentStatistics(
                            fillerGroupKey2,
                            2,
                            sortedDocStatG3,
                            totalVolumeReceivedGroup2,
                            totalDocInProgressGroup2,
                            totalDocInProcessGroup2);
                    documentStatisticsLevel2.add(documentStatistics);
                  });
          var sortedDocStatG2 =
              documentStatisticsLevel2.stream()
                  .sorted(Comparator.comparing(DocumentStatistics::getFiller))
                  .collect(Collectors.toList());
          reOrderDocumentIfBlankIsPresent(sortedDocStatG2);
          DocumentStatistics documentStatistics =
              getDocumentStatistics(
                  fillerGroupKey1,
                  1,
                  sortedDocStatG2,
                  totalVolumeReceivedGroup1,
                  totalDocInProgressGroup1,
                  totalDocInProcessGroup1);
          documentStatisticsLevel.add(documentStatistics);
        });
    var sortedDocStatG1 =
        documentStatisticsLevel.stream()
            .sorted(Comparator.comparing(DocumentStatistics::getFiller))
            .collect(Collectors.toList());
    reOrderDocumentIfBlankIsPresent(sortedDocStatG1);
    return new DocumentDetailSummary(requestFilter.getGroupFillers().size(), sortedDocStatG1);
  }

  protected void mapAndCalProdDetails(
      DocumentDetailSummary refDocSummary,
      List<ProductionDetailsFillers> pndMndProdDetails,
      StatisticRequestFilter requestFilter) {

    if (CollectionUtils.isEmpty(requestFilter.getGroupFillers())) {
      var totalPndMnd =
          pndMndProdDetails.isEmpty()
              ? 0L
              : pndMndProdDetails.stream().mapToLong(ProductionDetailsFillers::getTotal).sum();
      DocumentStatistics documentStatistics = refDocSummary.getData().get(0);
      documentStatistics.setPndMnd(totalPndMnd);
    } else {
      int fillerGroupingCount = requestFilter.getGroupFillers().size();
      var groupByFiller = getGroupByFiller(pndMndProdDetails, fillerGroupingCount, 1);
      groupByFiller.forEach(
          (fillerG1Key, pndMndProdDetailsG1) -> {
            var docStatisticsG1 =
                getDocStatisticsByFillerName(refDocSummary.getData(), fillerG1Key);
            getGroupByFiller(pndMndProdDetailsG1, fillerGroupingCount, 2)
                .forEach(
                    (fillerG2Key, pndMndProdDetailsG2) ->
                        docStatisticsG1.ifPresent(
                            docStatisticsG1Result -> {
                              var docStatisticsG2 =
                                  getDocStatisticsByFillerName(
                                      docStatisticsG1Result.getData(), fillerG2Key);

                              getGroupByFiller(pndMndProdDetailsG2, fillerGroupingCount, 3)
                                  .forEach(
                                      (fillerG3Key, pndMndProdDetailsG3) ->
                                          docStatisticsG2.ifPresent(
                                              docStatisticsG2Result -> {
                                                var docStatisticsG3 =
                                                    getDocStatisticsByFillerName(
                                                        docStatisticsG2Result.getData(),
                                                        fillerG3Key);

                                                docStatisticsG3.ifPresent(
                                                    docStatisticsG3Result ->
                                                        docStatisticsG3Result.setPndMnd(
                                                            pndMndProdDetailsG3.get(0).getTotal()));
                                              }));

                              var totalMndPndG2 =
                                  pndMndProdDetailsG2.stream()
                                      .mapToLong(ProductionDetailsFillers::getTotal)
                                      .sum();

                              docStatisticsG2.ifPresent(
                                  documentStatistics ->
                                      documentStatistics.setPndMnd(totalMndPndG2));
                            }));
            var totalMndPndG1 =
                pndMndProdDetailsG1.stream().mapToLong(ProductionDetailsFillers::getTotal).sum();

            docStatisticsG1.ifPresent(
                documentStatistics -> documentStatistics.setPndMnd(totalMndPndG1));
          });
    }
  }

  protected void calPercentage(DocumentDetailSummary refDocSummary) {
    AtomicLong totalVolumeReceived = new AtomicLong(0);
    AtomicLong totalInProgress = new AtomicLong(0);
    AtomicLong totalProcessed = new AtomicLong(0);
    AtomicLong totalPndMnd = new AtomicLong(0);
    refDocSummary
        .getData()
        .forEach(
            docStatisticsG1 -> {
              docStatisticsG1
                  .getData()
                  .forEach(
                      docStatisticsG2 -> {
                        docStatisticsG2.getData().forEach(this::calProdDetailsPercentage);
                        calProdDetailsPercentage(docStatisticsG2);
                      });
              calProdDetailsPercentage(docStatisticsG1);
              totalVolumeReceived.getAndAdd(docStatisticsG1.getVolumeReceived());
              totalInProgress.getAndAdd(docStatisticsG1.getInProgress());
              totalProcessed.getAndAdd(docStatisticsG1.getProcessed());
              totalPndMnd.getAndAdd(docStatisticsG1.getPndMnd());
            });
    DocumentStatistics totalDocumentStatistic = new DocumentStatistics();
    totalDocumentStatistic.setFiller(ProductionDetailMetaData.TOTAL.getKey());
    totalDocumentStatistic.setVolumeReceived(totalVolumeReceived.get());
    totalDocumentStatistic.setInProgress(totalInProgress.get());
    totalDocumentStatistic.setProcessed(totalProcessed.get());
    totalDocumentStatistic.setPndMnd(totalPndMnd.get());
    this.calProdDetailsPercentage(totalDocumentStatistic);
    refDocSummary.setTotal(totalDocumentStatistic);
  }

  private void calProdDetailsPercentage(DocumentStatistics refDoc) {
    refDoc.setPndMndPercentage(
        AnalyticsCalculatorUtils.getTotalPercentage(
            refDoc.getPndMnd(), refDoc.getVolumeReceived()));
    refDoc.setProcessedPercentage(
        AnalyticsCalculatorUtils.getTotalPercentage(
            refDoc.getProcessed(), refDoc.getVolumeReceived()));
  }

  private Optional<DocumentStatistics> getDocStatisticsByFillerName(
      List<DocumentStatistics> docSummary, String fillerName) {
    return docSummary.stream()
        .filter(documentStatistics -> Objects.equals(fillerName, documentStatistics.getFiller()))
        .findFirst();
  }

  protected List<ProductionDetailsFillers> fetchProdDetailsPndMnd(
      StatisticRequestFilter requestFilter) {
    var querySpec = StatisticSpecification.generatePostPndMndSpecs(requestFilter);
    return this.getFlowTraceabilityReportRepository()
        .findAll(querySpec, FlowTraceabilityReport.class, Tuple.class)
        .stream()
        .map(this::mapPndMnd)
        .collect(Collectors.toList());
  }

  private ProductionDetailsFillers mapPndMnd(Tuple sourceTuple) {
    return ProductionDetailsFillers.builder()
        .total(TupleUtils.defaultIfNull(sourceTuple, 0, Long.class, 0L))
        .fillerGroup1(TupleUtils.defaultIfBlank(sourceTuple, 1, AnalyticsConstants.BLANK))
        .fillerGroup2(TupleUtils.defaultIfBlank(sourceTuple, 2, AnalyticsConstants.BLANK))
        .fillerGroup3(TupleUtils.defaultIfBlank(sourceTuple, 3, AnalyticsConstants.BLANK))
        .build();
  }

  private DocumentStatistics getDocumentStatistics(
      String filler,
      int fillerLevel,
      List<DocumentStatistics> subDocumentStatistics,
      long totalVolumeReceived,
      long totalInProgress,
      long totalInProcess) {
    DocumentStatistics documentStatistics = new DocumentStatistics();
    documentStatistics.setFiller(filler);
    documentStatistics.setFillerGroupingLevel(fillerLevel);
    documentStatistics.setVolumeReceived(totalVolumeReceived);
    documentStatistics.setInProgress(totalInProgress);
    documentStatistics.setProcessed(totalInProcess);
    documentStatistics.setData(subDocumentStatistics);
    return documentStatistics;
  }

  private Map<String, List<ProductionDetailsFillers>> getGroupByFiller(
      List<ProductionDetailsFillers> prodFillerGroup, int requestFillerSize, int level) {
    if (level > requestFillerSize) {
      level = -1;
    }
    switch (level) {
      case 1:
        return prodFillerGroup.stream()
            .collect(Collectors.groupingBy(ProductionDetailsFillers::getFillerGroup1));
      case 2:
        return prodFillerGroup.stream()
            .collect(Collectors.groupingBy(ProductionDetailsFillers::getFillerGroup2));
      case 3:
        return prodFillerGroup.stream()
            .collect(Collectors.groupingBy(ProductionDetailsFillers::getFillerGroup3));
      default:
        return Map.of();
    }
  }

  private long getTotalPerKeyFiller(List<ProductionDetailsFillers> prodFillers) {
    return prodFillers.stream().mapToLong(ProductionDetailsFillers::getTotal).sum();
  }

  private long getTotalDocInProgress(List<ProductionDetailsFillers> prodFillers) {
    return prodFillers.stream()
        .filter(
            proDoc ->
                FlowDocumentStatus.IN_PROGRESS.getValue().equalsIgnoreCase(proDoc.getStatus()))
        .mapToLong(ProductionDetailsFillers::getTotal)
        .sum();
  }

  private long getTotalDocInProcess(List<ProductionDetailsFillers> prodFillers) {
    List<String> statusInProcess =
        Stream.of(FlowDocumentStatus.COMPLETED.getValue(), FlowDocumentStatus.IN_ERROR.getValue())
            .map(String::toLowerCase)
            .collect(Collectors.toList());
    return prodFillers.stream()
        .filter(proDoc -> statusInProcess.contains(proDoc.getStatus().toLowerCase()))
        .mapToLong(ProductionDetailsFillers::getTotal)
        .sum();
  }

  private void reOrderDocumentIfBlankIsPresent(List<DocumentStatistics> documentStatisticsLevel) {
    OptionalInt indexOfBlankItem = getIndexOfBlankItem(documentStatisticsLevel);
    if (indexOfBlankItem.isPresent()) {
      int distance = documentStatisticsLevel.size() - (indexOfBlankItem.getAsInt() + 1);
      Collections.rotate(documentStatisticsLevel, distance);
    }
  }

  private OptionalInt getIndexOfBlankItem(List<DocumentStatistics> documentStatisticsLevel) {
    return IntStream.range(0, documentStatisticsLevel.size())
        .filter(
            index ->
                documentStatisticsLevel
                    .get(index)
                    .getFiller()
                    .equalsIgnoreCase(AnalyticsConstants.BLANK))
        .findFirst();
  }

  protected DocumentSummary getTotalDocumentSummaryPND(StatisticRequestFilter requestFilter) {
    List<ProductionDetailsFillers> productionDetailsFillers =
        this.documentDetailSummaryPNDMND(requestFilter);
    return getDocumentSummary(productionDetailsFillers);
  }

  protected List<DepositModeResponseDto> getDepositModeResponse(
      List<String> subStatus, DocumentSummary docSumByStatus) {
    List<DepositModeResponseDto> depositModeResponse = new ArrayList<>();
    docSumByStatus.forEach(
        (key, totalPerKey) -> {
          double totalPercentagePerKey =
              AnalyticsCalculatorUtils.getTotalPercentage(
                  totalPerKey, docSumByStatus.getTotalDocument());
          depositModeResponse.add(new DepositModeResponseDto(key, totalPercentagePerKey));
        });
    return subStatus.stream()
        .flatMap(
            status ->
                depositModeResponse.stream().filter(res -> status.equalsIgnoreCase(res.getKey())))
        .collect(Collectors.toList());
  }

  protected List<ProductionDetailsFillers> getDocDistByStatus(
      StatisticRequestFilter requestFilter) {
    var querySpec = StatisticSpecification.genDocSumBySubStatusSpecs(requestFilter);
    return this.getFlowTraceabilityReportRepository()
        .findAll(querySpec, FlowTraceabilityReport.class, Tuple.class)
        .stream()
        .map(ProductionDetailsFillers::new)
        .collect(Collectors.toList());
  }

  protected DocumentSummary getTotalDocumentByStatus(StatisticRequestFilter requestFilter) {
    List<ProductionDetailsFillers> productionDetailsFillers =
        this.getDocDistByStatus(requestFilter);
    return getDocumentSummary(productionDetailsFillers);
  }

  private DocumentSummary getDocumentSummary(
      List<ProductionDetailsFillers> productionDetailsFillers) {
    DocumentSummary documentSummary = new DocumentSummary();
    productionDetailsFillers.forEach(
        prodDetails -> {
          String key = prodDetails.getStatus();
          long totalPerKey = documentSummary.getOrDefault(key, 0L) + prodDetails.getTotal();
          documentSummary.put(key, totalPerKey);
        });
    long total = documentSummary.values().stream().mapToLong(Long::longValue).sum();
    documentSummary.countTotal(total);
    return documentSummary;
  }
}
