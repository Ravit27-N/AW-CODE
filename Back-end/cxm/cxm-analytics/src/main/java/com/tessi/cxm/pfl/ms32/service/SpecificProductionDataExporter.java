package com.tessi.cxm.pfl.ms32.service;

import com.tessi.cxm.pfl.ms32.constant.AnalyticsConstants;
import com.tessi.cxm.pfl.ms32.constant.ExportType;
import com.tessi.cxm.pfl.ms32.constant.StatisticsExportingCSVHeader;
import com.tessi.cxm.pfl.ms32.dto.GlobalStatisticRequestFilter;
import com.tessi.cxm.pfl.ms32.dto.SpecificProductionRecord;
import com.tessi.cxm.pfl.ms32.dto.StatisticRequestFilter;
import com.tessi.cxm.pfl.ms32.entity.FlowDocumentReport;
import com.tessi.cxm.pfl.ms32.entity.FlowDocumentReportHistory_;
import com.tessi.cxm.pfl.ms32.entity.FlowDocumentReport_;
import com.tessi.cxm.pfl.ms32.entity.FlowTraceabilityReport;
import com.tessi.cxm.pfl.ms32.entity.FlowTraceabilityReport_;
import com.tessi.cxm.pfl.ms32.exception.CSVFailureException;
import com.tessi.cxm.pfl.ms32.repository.FlowTraceabilityReportRepository;
import com.tessi.cxm.pfl.ms32.service.specification.ProductionExportingSpecification;
import com.tessi.cxm.pfl.ms32.service.specification.StatisticSpecification;
import com.tessi.cxm.pfl.shared.core.Context;
import com.tessi.cxm.pfl.shared.model.SharedClientFillersDTO;
import com.tessi.cxm.pfl.shared.utils.FlowDocumentChannelConstant;
import com.tessi.cxm.pfl.shared.utils.FlowDocumentStatus;
import com.tessi.cxm.pfl.shared.utils.FlowDocumentSubChannel;
import com.tessi.cxm.pfl.shared.utils.SpecificationUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.Tuple;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Selection;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
@Slf4j
public class SpecificProductionDataExporter
    extends PageableProductionDataCSVExporter<SpecificProductionRecord> {

  public static final String KEY = "SpecificProductionDataExporter";
  private final FlowTraceabilityReportRepository flowTraceabilityReportRepository;
  private static final String CSV_WRITING_FAILURE = "Fail to write CSV file";

  public SpecificProductionDataExporter(
      FlowTraceabilityReportRepository flowTraceabilityReportRepository) {
    this.flowTraceabilityReportRepository = flowTraceabilityReportRepository;
  }

  @Override
  public String getKey() {
    return KEY;
  }

  @Override
  protected Page<SpecificProductionRecord> readByPageable(Context context, Pageable pageable) {
    var productionRecodes = this.getAnalyticsCsvDTO(context, pageable);
    this.mapUserDetailsAndRemoveDisableFiller(productionRecodes, context);
    return productionRecodes;
  }

  @Override
  protected void doExportInternal(List<SpecificProductionRecord> datas, CSVPrinter csvPrinter)
      throws IOException {
    datas.forEach(
        data -> {
          try {
            List<String> row = new ArrayList<>(data.getSpecificCSVRow().values());
            csvPrinter.printRecord(row);
          } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new CSVFailureException(CSV_WRITING_FAILURE);
          }
        });
    csvPrinter.flush();
  }

  @Override
  protected String[] getHeaders(Context context) {
    var clientFillers = this.getClientFillers(context);
    GlobalStatisticRequestFilter requestFilter =
        this.getRequestFilter(context, GlobalStatisticRequestFilter.class);
    List<String> headers = StatisticsExportingCSVHeader.getHeaders(clientFillers);
    if (FlowDocumentChannelConstant.DIGITAL.equalsIgnoreCase(requestFilter.getChannels().get(0))) {
      headers.remove(StatisticsExportingCSVHeader.PND_DATE.getFr());
    } else {
      headers.remove(StatisticsExportingCSVHeader.MND_DATE.getFr());
    }
    return headers.toArray(new String[0]);
  }

  protected Specification<FlowTraceabilityReport> generatePostSpecs(
      StatisticRequestFilter requestFilter, Context context) {
    Specification<FlowTraceabilityReport> querySpecs =
        StatisticSpecification.ownerIdIn(requestFilter.getOwnerIds());
    querySpecs =
        querySpecs.and(
            StatisticSpecification.withinRequestDateRange(
                requestFilter.getStartDate(),
                requestFilter.getEndDate(),
                requestFilter.getRequestedAt()));
    querySpecs =
        querySpecs.and(StatisticSpecification.withCategories(requestFilter.getCategories()));
    if (!CollectionUtils.isEmpty(requestFilter.getFillerKeyText())) {
      querySpecs =
          querySpecs.and(StatisticSpecification.withGroupFillers(requestFilter.getFillerKeyText()));
    }
    querySpecs = querySpecs.and(ProductionExportingSpecification.generateSubQueryWhereClause());
    return querySpecs.and(this.generateSelection(context));
  }

  protected Specification<FlowTraceabilityReport> generateSelection(Context context) {
    return (root, query, criteriaBuilder) -> {
      var joinFlowDoc =
          SpecificationUtils.getOrCreateJoin(root, FlowTraceabilityReport_.flowDocumentReports);
      var joinFlowDocHist =
          SpecificationUtils.getOrCreateJoin(
              joinFlowDoc, FlowDocumentReport_.flowDocumentHistories);
      List<Selection<?>> selections = new ArrayList<>();
      selections.add(root.get(FlowTraceabilityReport_.ownerId));
      selections.add(joinFlowDoc.get(FlowDocumentReport_.idDoc));
      selections.add(root.get(FlowTraceabilityReport_.depositMode));
      selections.add(root.get(FlowTraceabilityReport_.channel));
      selections.add(joinFlowDoc.get(FlowDocumentReport_.subChannel));
      selections.add(joinFlowDoc.get(FlowDocumentReport_.totalPage));
      selections.add(joinFlowDoc.get(FlowDocumentReport_.recipient));
      selections.add(joinFlowDoc.get(FlowDocumentReport_.numReco));
      selections.add(joinFlowDocHist.get(FlowDocumentReportHistory_.status));
      selections.add(joinFlowDoc.get(FlowDocumentReport_.dateReception));
      selections.add(joinFlowDoc.get(FlowDocumentReport_.dateSending));
      selections.add(joinFlowDocHist.get(FlowDocumentReportHistory_.dateStatus));
      selections.addAll(ProductionExportingSpecification.generateFillersPath(joinFlowDoc, context));
      query.multiselect(selections);
      return null;
    };
  }

  public Page<SpecificProductionRecord> getAnalyticsCsvDTO(Context context, Pageable pageable) {
    var requestFilter = this.getRequestFilter(context, StatisticRequestFilter.class);
    final var timezone = context.get(AnalyticsConstants.TARGET_EXPORTING_TIMEZONE, String.class);

    Specification<FlowTraceabilityReport> flowTraceabilityReportSpecification =
        this.generatePostSpecs(requestFilter, context);

    return this.flowTraceabilityReportRepository
        .findAll(
            flowTraceabilityReportSpecification,
            FlowTraceabilityReport.class,
            Tuple.class,
            pageable)
        .map(tupleRecord -> new SpecificProductionRecord(tupleRecord, timezone));
  }

  private void mapUserDetailsAndRemoveDisableFiller(
      Page<SpecificProductionRecord> specificProductionRecords, Context context) {
    var requestFilter = this.getRequestFilter(context, StatisticRequestFilter.class);
    List<SharedClientFillersDTO> fillers = this.getClientFillers(context);

    List<Integer> disableFillerOrderKeys =
        StatisticsExportingCSVHeader.defaultCSVFillers().stream()
            .filter(
                csvHeaderFiller ->
                    fillers.stream()
                        .map(SharedClientFillersDTO::getKey)
                        .noneMatch(s -> s.equalsIgnoreCase(csvHeaderFiller.getEn())))
            .map(StatisticsExportingCSVHeader::getOrder)
            .collect(Collectors.toList());

    specificProductionRecords.forEach(
        specificProductionRecord -> {
          var ownerDetail =
              requestFilter.getOwnerDetails().get(specificProductionRecord.getOwnerId());
          if (ownerDetail != null) {
            specificProductionRecord.addRow(
                StatisticsExportingCSVHeader.DIVISION.getOrder(), ownerDetail.getDivisionName());
            specificProductionRecord.addRow(
                StatisticsExportingCSVHeader.SERVICE.getOrder(), ownerDetail.getServiceName());
            specificProductionRecord.addRow(
                StatisticsExportingCSVHeader.USER.getOrder(), ownerDetail.getUsername());
            specificProductionRecord.removeRow(disableFillerOrderKeys);
          }
        });
  }
}
