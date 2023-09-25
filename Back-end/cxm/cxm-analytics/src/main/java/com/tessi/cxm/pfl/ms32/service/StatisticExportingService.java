package com.tessi.cxm.pfl.ms32.service;

import com.tessi.cxm.pfl.ms32.config.CSVExportingProperties;
import com.tessi.cxm.pfl.ms32.constant.AnalyticsConstants;
import com.tessi.cxm.pfl.ms32.constant.CSVExportingConstant;
import com.tessi.cxm.pfl.ms32.constant.ExportType;
import com.tessi.cxm.pfl.ms32.dto.GlobalStatisticRequestFilter;
import com.tessi.cxm.pfl.ms32.dto.StatisticExportingRequestFilter;
import com.tessi.cxm.pfl.ms32.dto.StatisticRequestFilter;
import com.tessi.cxm.pfl.ms32.repository.FlowTraceabilityReportRepository;
import com.tessi.cxm.pfl.shared.core.Context;
import com.tessi.cxm.pfl.shared.exception.FileErrorException;
import com.tessi.cxm.pfl.shared.model.UserPrivilegeDetailsOwner;
import com.tessi.cxm.pfl.shared.model.UserPrivilegeDetailsOwner.UserDetailsOwner;
import com.tessi.cxm.pfl.shared.service.ServiceUtils;
import com.tessi.cxm.pfl.shared.service.restclient.ProfileFeignClient;
import com.tessi.cxm.pfl.shared.service.restclient.SettingFeignClient;
import com.tessi.cxm.pfl.shared.utils.FlowDocumentChannelConstant;
import com.tessi.cxm.pfl.shared.utils.PrivilegeValidationUtil;
import com.tessi.cxm.pfl.shared.utils.ProfileConstants;
import com.tessi.cxm.pfl.shared.utils.ProfileConstants.Privilege.Statistic;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class StatisticExportingService extends AbstractStatisticService implements ServiceUtils {
  private final CSVExportingProperties csvExportingProperties;
  private final SimpleDateFormat dateFormat =
      new SimpleDateFormat(CSVExportingConstant.CSV_NAME_DATE_FORMAT);

  public StatisticExportingService(
      FlowTraceabilityReportRepository flowTraceabilityReportRepository,
      SettingFeignClient settingFeignClient,
      ProfileFeignClient profileFeignClient,
      CSVExportingProperties csvExportingProperties) {
    super(flowTraceabilityReportRepository, settingFeignClient, profileFeignClient);
    this.csvExportingProperties = csvExportingProperties;
  }

  public java.nio.file.Path generateAndExport(StatisticExportingRequestFilter requestFilter) {
    UserPrivilegeDetailsOwner userDetails =
        PrivilegeValidationUtil.getUserPrivilegeRelatedOwnerDetails(
            ProfileConstants.CXM_STATISTIC_REPORT, Statistic.DOWNLOAD_STATISTIC, true, true);
    GlobalStatisticRequestFilter refReqFilter = validateAndResolve(userDetails, requestFilter);
    final var exportingType = requestFilter.getExportingType();
    ProductionDataExporter exporter = getInstanceKey(exportingType);
    java.nio.file.Path targetCsvPath = this.initCsvFile(requestFilter);
    try (BufferedWriter writer = Files.newBufferedWriter(targetCsvPath, StandardCharsets.UTF_8)) {
      Context context = new Context();
      context.put(AnalyticsConstants.EXPORTING_REQUEST_FILTER_KEY, refReqFilter);
      context.put(AnalyticsConstants.CLIENT_FILLERS_KEY, refReqFilter.getClientFillers());
      context.put(AnalyticsConstants.CSV_DELIMITER_KEY, ";");
      context.put(AnalyticsConstants.PAGE_SIZE_KEY, this.csvExportingProperties.getPageSize());
      context.put(AnalyticsConstants.TARGET_EXPORTING_TIMEZONE, requestFilter.getTimeZone());
      exporter.export(context, writer);
      return targetCsvPath;
    } catch (IOException ioe) {
      // close stream and delete error file
      FileUtils.deleteQuietly(targetCsvPath.toFile());
      // throw specific error
      throw new FileErrorException("Failed to export statistics reports to CSV", ioe);
    }
  }

  private GlobalStatisticRequestFilter validateAndResolve(
      UserPrivilegeDetailsOwner userDetails, StatisticExportingRequestFilter requestFilter) {
    requestFilter.setOwnerIds(
        userDetails.getUserDetailsOwners().stream()
            .map(UserDetailsOwner::getId)
            .collect(Collectors.toList()));
    Map<Long, UserDetailsOwner> userDetailsOwnerMap =
        userDetails.getUserDetailsOwners().stream()
            .collect(
                Collectors.toMap(UserDetailsOwner::getId, userDetailsOwner -> userDetailsOwner));
    requestFilter.setOwnerDetails(userDetailsOwnerMap);
    if (ExportType.GLOBAL.equalsIgnoreCase(requestFilter.getExportingType())) {
      var refGlobalReqFilter = new GlobalStatisticRequestFilter();
      BeanUtils.copyProperties(requestFilter, refGlobalReqFilter);
      this.validateAndResolveGlobal(refGlobalReqFilter);

      return refGlobalReqFilter;
    } else {
      var refSpecificReqFilter = new StatisticRequestFilter();
      BeanUtils.copyProperties(requestFilter, refSpecificReqFilter);
      this.validateAndResolveSpecific(refSpecificReqFilter);
      return refSpecificReqFilter;
    }
  }

  private java.nio.file.Path initCsvFile(StatisticExportingRequestFilter requestFilter) {
    final var exportingType = requestFilter.getExportingType();
    String csvPath =
        ExportType.GLOBAL.equalsIgnoreCase(exportingType)
            ? globalCSVFileName(requestFilter)
            : specificCSVFileName(requestFilter);
    return java.nio.file.Path.of(this.csvExportingProperties.getDirectory(), csvPath);
  }

  private String globalCSVFileName(StatisticExportingRequestFilter requestFilter) {
    return CSVExportingConstant.GLOBAL_PREFIX
        .concat("_")
        .concat(dateFormat.format(requestFilter.getRequestedAt()))
        .concat("." + CSVExportingConstant.EXTENSION);
  }

  private String specificCSVFileName(GlobalStatisticRequestFilter requestFilter) {
    var type = FlowDocumentChannelConstant.POSTAL;
    if (FlowDocumentChannelConstant.DIGITAL.equalsIgnoreCase(requestFilter.getChannels().get(0))) {
      type = requestFilter.getCategories().get(0);
    }
    return CSVExportingConstant.SPECIFIC_PREFIX
        .concat("_")
        .concat(type)
        .concat("_")
        .concat(dateFormat.format(requestFilter.getRequestedAt()))
        .concat("." + CSVExportingConstant.EXTENSION);
  }

  private ProductionDataExporter getInstanceKey(String exportingType) {
    var instanceKey =
        ExportType.GLOBAL.equalsIgnoreCase(exportingType)
            ? GlobalProductionDataExporter.KEY
            : SpecificProductionDataExporter.KEY;
    return ProductionDataExporters.getByKey(instanceKey);
  }
}
