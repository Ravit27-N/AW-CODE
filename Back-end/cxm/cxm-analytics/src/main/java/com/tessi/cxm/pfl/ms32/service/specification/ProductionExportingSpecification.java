package com.tessi.cxm.pfl.ms32.service.specification;

import com.tessi.cxm.pfl.ms32.constant.AnalyticsConstants;
import com.tessi.cxm.pfl.ms32.dto.StatisticRequestFilter;
import com.tessi.cxm.pfl.ms32.entity.FlowDocumentReport;
import com.tessi.cxm.pfl.ms32.entity.FlowDocumentReportHistory;
import com.tessi.cxm.pfl.ms32.entity.FlowDocumentReportHistory_;
import com.tessi.cxm.pfl.ms32.entity.FlowDocumentReport_;
import com.tessi.cxm.pfl.ms32.entity.FlowTraceabilityReport;
import com.tessi.cxm.pfl.ms32.entity.FlowTraceabilityReport_;
import com.tessi.cxm.pfl.shared.core.Context;
import com.tessi.cxm.pfl.shared.model.SharedClientFillersDTO;
import com.tessi.cxm.pfl.shared.utils.SpecificationUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.persistence.criteria.Subquery;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductionExportingSpecification {

  public static Specification<FlowTraceabilityReport> generateProductionExportingSpec(
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
        querySpecs.and(StatisticSpecification.withCategories(requestFilter.getCategories(), true));
    if (!CollectionUtils.isEmpty(requestFilter.getFillerKeyText())) {
      querySpecs =
          querySpecs.and(StatisticSpecification.withGroupFillers(requestFilter.getFillerKeyText()));
    }
    querySpecs = querySpecs.and(generateSubQueryWhereClause());
    return querySpecs.and(generateSelection(requestFilter, context));
  }

  public static Specification<FlowTraceabilityReport> generateSubQueryWhereClause() {
    return (root, query, criteriaBuilder) -> {
      var joinFlowDoc =
          SpecificationUtils.getOrCreateJoin(root, FlowTraceabilityReport_.flowDocumentReports);
      var joinFlowDocHist =
          SpecificationUtils.getOrCreateJoin(
              joinFlowDoc, FlowDocumentReport_.flowDocumentHistories);
      Subquery<Date> flowDocHistorySubQuery = query.subquery(Date.class);
      Root<FlowDocumentReportHistory> reportHistoryRoot =
          flowDocHistorySubQuery.from(FlowDocumentReportHistory.class);
      Subquery<Date> dateSubQuery =
          flowDocHistorySubQuery
              .select(
                  criteriaBuilder.greatest(
                      reportHistoryRoot.get(FlowDocumentReportHistory_.dateStatus)))
              .where(
                  criteriaBuilder.equal(
                      joinFlowDoc.get(FlowDocumentReport_.id),
                      reportHistoryRoot
                          .get(FlowDocumentReportHistory_.flowDocumentReport)
                          .get(FlowDocumentReport_.id)));

      return criteriaBuilder.and(
          criteriaBuilder.equal(
              joinFlowDocHist.get(FlowDocumentReportHistory_.dateStatus), dateSubQuery));
    };
  }

  public static Specification<FlowTraceabilityReport> generateSelection(
      StatisticRequestFilter requestFilter, Context context) {
    return (root, query, criteriaBuilder) -> {
      var joinFlowDoc =
          SpecificationUtils.getOrCreateJoin(root, FlowTraceabilityReport_.flowDocumentReports);
      var joinFlowDocHist =
          SpecificationUtils.getOrCreateJoin(
              joinFlowDoc, FlowDocumentReport_.flowDocumentHistories);
      List<Selection<?>> selection = new ArrayList<>();
      selection.add(root.get(FlowTraceabilityReport_.ownerId));
      selection.add(joinFlowDoc.get(FlowDocumentReport_.idDoc));
      selection.add(root.get(FlowTraceabilityReport_.depositMode));
      selection.add(root.get(FlowTraceabilityReport_.channel));
      selection.add(joinFlowDoc.get(FlowDocumentReport_.subChannel));
      selection.add(joinFlowDocHist.get(FlowDocumentReportHistory_.status));
      selection.add(joinFlowDoc.get(FlowDocumentReport_.dateReception));
      selection.add(joinFlowDoc.get(FlowDocumentReport_.dateSending));
      if (!requestFilter.getFillers().isEmpty()) {
        selection.addAll(generateFillersPath(joinFlowDoc, context));
      }
      query.multiselect(selection);
      return null;
    };
  }

  @SuppressWarnings("unchecked")
  public static List<Selection<Object>> generateFillersPath(
      Join<FlowTraceabilityReport, FlowDocumentReport> joinFlowDoc, Context context) {
    List<SharedClientFillersDTO> fillers =
        context.get(AnalyticsConstants.CLIENT_FILLERS_KEY, List.class);
    return fillers.stream()
        .map(SharedClientFillersDTO::getKey)
        .map(fillerKey -> joinFlowDoc.get(fillerKey.toLowerCase()).alias(fillerKey.toLowerCase()))
        .collect(Collectors.toList());
  }
}
