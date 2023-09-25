package com.tessi.cxm.pfl.ms32.service;

import com.tessi.cxm.pfl.ms32.constant.AnalyticsConstants;
import com.tessi.cxm.pfl.ms32.constant.StatisticsExportingCSVHeader;
import com.tessi.cxm.pfl.ms32.dto.GlobalProductionRecord;
import com.tessi.cxm.pfl.ms32.dto.GlobalStatisticRequestFilter;
import com.tessi.cxm.pfl.ms32.dto.StatisticRequestFilter;
import com.tessi.cxm.pfl.ms32.entity.FlowTraceabilityReport;
import com.tessi.cxm.pfl.ms32.repository.FlowTraceabilityReportRepository;
import com.tessi.cxm.pfl.ms32.service.specification.ProductionExportingSpecification;
import com.tessi.cxm.pfl.shared.core.Context;
import com.tessi.cxm.pfl.shared.model.SharedClientFillersDTO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.Tuple;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GlobalProductionDataExporter
    extends PageableProductionDataCSVExporter<GlobalProductionRecord> {
  private final FlowTraceabilityReportRepository flowTraceabilityReportRepository;
  public static final String KEY = "GlobalProductionDataExporter";

  @Override
  public String getKey() {
    return KEY;
  }

  @Override
  protected String[] getHeaders(Context context) {
    var nonGlobalCsvHeader =
        List.of(
            StatisticsExportingCSVHeader.PND_DATE.getFr(),
            StatisticsExportingCSVHeader.MND_DATE.getFr(),
            StatisticsExportingCSVHeader.TOTAL_PAGE.getFr(),
            StatisticsExportingCSVHeader.NUM_RECO.getFr(),
            StatisticsExportingCSVHeader.RECIPIENT.getFr());
    var clientFillers = this.getClientFillers(context);
    var headers = StatisticsExportingCSVHeader.getHeaders(clientFillers);
    headers.removeAll(nonGlobalCsvHeader);
    return headers.toArray(String[]::new);
  }

  @Override
  protected Page<GlobalProductionRecord> readByPageable(Context context, Pageable pageable) {
    var productionRecords = this.getProductionRecords(context, pageable);
    mapUserDetailsAndRemoveDisableFiller(productionRecords, context);
    return productionRecords;
  }

  @Override
  protected void doExportInternal(List<GlobalProductionRecord> productionRecords, CSVPrinter writer)
      throws IOException {
    for (var productionRecord : productionRecords) {
      List<String> row = new ArrayList<>(productionRecord.getGlobalCsvRow().values());
      writer.printRecord(row);
    }
    writer.flush();
  }

  private Page<GlobalProductionRecord> getProductionRecords(Context context, Pageable pageable) {
    final var timezone = context.get(AnalyticsConstants.TARGET_EXPORTING_TIMEZONE, String.class);
    GlobalStatisticRequestFilter requestFilter =
        this.getRequestFilter(context, GlobalStatisticRequestFilter.class);
    StatisticRequestFilter statisticRequestFilter = new StatisticRequestFilter();
    BeanUtils.copyProperties(requestFilter, statisticRequestFilter);
    Specification<FlowTraceabilityReport> flowTraceabilityReportSpecification =
        ProductionExportingSpecification.generateProductionExportingSpec(
            statisticRequestFilter, context);
    return this.flowTraceabilityReportRepository
        .findAll(
            flowTraceabilityReportSpecification,
            FlowTraceabilityReport.class,
            Tuple.class,
            pageable)
        .map(tupleRec -> new GlobalProductionRecord(tupleRec, timezone));
  }

  private void mapUserDetailsAndRemoveDisableFiller(
      Page<GlobalProductionRecord> productionRecords, Context context) {
    var requestFilter = this.getRequestFilter(context, GlobalStatisticRequestFilter.class);
    var clientFillers = this.getClientFillers(context);
    var disableFillerOrderKeys =
        StatisticsExportingCSVHeader.defaultCSVFillers().stream()
            .filter(
                csvHeaderFiller ->
                    clientFillers.stream()
                        .map(SharedClientFillersDTO::getKey)
                        .noneMatch(s -> s.equalsIgnoreCase(csvHeaderFiller.getEn())))
            .map(StatisticsExportingCSVHeader::getOrder)
            .collect(Collectors.toList());
    var userDetailsOwnerMap = requestFilter.getOwnerDetails();
    productionRecords.forEach(
        productionRecord -> {
          var userDetailsOwner = userDetailsOwnerMap.get(productionRecord.getOwnerId());
          if (userDetailsOwner != null) {
            productionRecord.addCsvRow(
                StatisticsExportingCSVHeader.DIVISION.getOrder(),
                userDetailsOwner.getDivisionName());
            productionRecord.addCsvRow(
                StatisticsExportingCSVHeader.SERVICE.getOrder(), userDetailsOwner.getServiceName());
            productionRecord.addCsvRow(
                StatisticsExportingCSVHeader.USER.getOrder(), userDetailsOwner.getUsername());
            // remove filler from record.
            productionRecord.removeCsvRow(disableFillerOrderKeys);
          }
        });
  }
}
