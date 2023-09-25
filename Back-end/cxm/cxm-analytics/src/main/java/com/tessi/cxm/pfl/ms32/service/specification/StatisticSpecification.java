package com.tessi.cxm.pfl.ms32.service.specification;

import com.tessi.cxm.pfl.ms32.dto.StatisticRequestFilter;
import com.tessi.cxm.pfl.ms32.entity.FlowDocumentReport;
import com.tessi.cxm.pfl.ms32.entity.FlowDocumentReportHistory;
import com.tessi.cxm.pfl.ms32.entity.FlowDocumentReportHistory_;
import com.tessi.cxm.pfl.ms32.entity.FlowDocumentReport_;
import com.tessi.cxm.pfl.ms32.entity.FlowTraceabilityReport;
import com.tessi.cxm.pfl.ms32.entity.FlowTraceabilityReport_;
import com.tessi.cxm.pfl.ms32.util.DateHelper;
import com.tessi.cxm.pfl.shared.utils.FlowDocumentChannelConstant;
import com.tessi.cxm.pfl.shared.utils.FlowDocumentStatus;
import com.tessi.cxm.pfl.shared.utils.SpecificationUtils;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Selection;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StatisticSpecification {

  public static Specification<FlowTraceabilityReport> ownerIdIn(List<Long> ownerIds) {
    return (root, query, cb) -> cb.in(root.get(FlowTraceabilityReport_.OWNER_ID)).value(ownerIds);
  }

  public static Specification<FlowTraceabilityReport> withinRequestDateRange(
      Date startDate, Date endDate, Date requestedAt) {

    return (root, query, criteriaBuilder) -> {
      var joinFlowDoc =
          SpecificationUtils.getOrCreateJoin(root, FlowTraceabilityReport_.flowDocumentReports);

      return criteriaBuilder.and(
          criteriaBuilder.between(
              joinFlowDoc.get(FlowDocumentReport_.dateReception),
              startDate,
              DateHelper.endOfDate(endDate)),
          criteriaBuilder.lessThanOrEqualTo(
              joinFlowDoc.get(FlowDocumentReport_.dateReception), requestedAt));
    };
  }

  public static Specification<FlowTraceabilityReport> withCategories(
      List<String> categories, boolean ignoreCase) {
    if (!ignoreCase) {
      return withCategories(categories);
    }
    List<String> refCategories =
        categories.stream().map(String::toLowerCase).collect(Collectors.toList());
    return (root, query, criteriaBuilder) -> {
      var joinFlowDoc =
          SpecificationUtils.getOrCreateJoin(root, FlowTraceabilityReport_.flowDocumentReports);
      return criteriaBuilder.and(
          criteriaBuilder.lower(joinFlowDoc.get(FlowDocumentReport_.subChannel)).in(refCategories));
    };
  }

  public static Specification<FlowTraceabilityReport> withCategories(List<String> categories) {

    return (root, query, criteriaBuilder) -> {
      var joinFlowDoc =
          SpecificationUtils.getOrCreateJoin(root, FlowTraceabilityReport_.flowDocumentReports);
      return criteriaBuilder.and(joinFlowDoc.get(FlowDocumentReport_.subChannel).in(categories));
    };
  }

  public static Specification<FlowTraceabilityReport> withGroupFillers(
      Map<String, String> fillerMap) {

    return (root, query, criteriaBuilder) -> {
      var joinFlowDoc =
          SpecificationUtils.getOrCreateJoin(root, FlowTraceabilityReport_.flowDocumentReports);

      List<Predicate> predicates = new ArrayList<>();
      fillerMap.forEach(
          (fillerKey, fillerValue) ->
              predicates.add(
                  criteriaBuilder.like(
                      criteriaBuilder.lower(joinFlowDoc.get(fillerKey.toLowerCase())),
                      "%" + fillerValue.toLowerCase() + "%")));
      return criteriaBuilder.and(criteriaBuilder.and(predicates.toArray(Predicate[]::new)));
    };
  }

  public static Specification<FlowTraceabilityReport> withStatuses(List<String> statuses) {

    return (root, query, criteriaBuilder) -> {
      var joinFlowDoc =
          SpecificationUtils.getOrCreateJoin(root, FlowTraceabilityReport_.flowDocumentReports);
      return criteriaBuilder.and(joinFlowDoc.get(FlowDocumentReport_.status).in(statuses));
    };
  }

  public static Specification<FlowTraceabilityReport> withSubStatuses(List<String> subStatuses) {
    return (root, query, criteriaBuilder) -> {
      var joinFlowDoc =
          SpecificationUtils.getOrCreateJoin(root, FlowTraceabilityReport_.flowDocumentReports);
      joinFlowDoc.get(FlowDocumentReport_.flowDocumentHistories);
      var joinFlowDocHist =
          SpecificationUtils.getOrCreateJoin(
              joinFlowDoc, FlowDocumentReport_.flowDocumentHistories);
      return criteriaBuilder.and(
          joinFlowDocHist.get(FlowDocumentReportHistory_.status).in(subStatuses));
    };
  }

  public static Specification<FlowTraceabilityReport> withLatestSubStatus() {
    return (root, query, criteriaBuilder) -> {
      var joinFlowDoc =
          SpecificationUtils.getOrCreateJoin(root, FlowTraceabilityReport_.flowDocumentReports);
      var joinFlowDocHist =
          SpecificationUtils.getOrCreateJoin(
              joinFlowDoc, FlowDocumentReport_.flowDocumentHistories);

      // Sub-Query
      var subQuery = query.subquery(Date.class); // History or Sub-Status
      var rootDoc = subQuery.from(FlowDocumentReportHistory.class);

      subQuery
          .select(criteriaBuilder.greatest(rootDoc.get(FlowDocumentReportHistory_.dateStatus)))
          .where(
              criteriaBuilder.equal(
                  rootDoc.get(FlowDocumentReportHistory_.flowDocumentReport),
                  joinFlowDoc.get(FlowDocumentReport_.id)));

      return criteriaBuilder.equal(
          joinFlowDocHist.get(FlowDocumentReportHistory_.dateStatus), subQuery);
    };
  }

  public static List<Path<Object>> generateFillersPath(
      StatisticRequestFilter requestFilter,
      Join<FlowTraceabilityReport, FlowDocumentReport> joinFlowDoc) {
    return requestFilter.getGroupFillers().stream()
        .map(fillerKey -> joinFlowDoc.get(fillerKey.toLowerCase()))
        .collect(Collectors.toList());
  }

  public static Specification<FlowTraceabilityReport> docSummaryPNDMNDSpec(
      StatisticRequestFilter requestFilter) {
    return (root, query, criteriaBuilder) -> {
      var joinFlowDoc =
          SpecificationUtils.getOrCreateJoin(root, FlowTraceabilityReport_.flowDocumentReports);
      var joinFlowDocHist =
          SpecificationUtils.getOrCreateJoin(
              joinFlowDoc, FlowDocumentReport_.flowDocumentHistories);
      List<Selection<?>> selection = new ArrayList<>();
      List<Expression<?>> grouping = new ArrayList<>();
      selection.add(criteriaBuilder.count(joinFlowDoc.get(FlowDocumentReport_.id)));
      selection.add(joinFlowDocHist.get(FlowDocumentReportHistory_.STATUS));
      grouping.add(joinFlowDocHist.get(FlowDocumentReportHistory_.STATUS));
      if (!requestFilter.getGroupFillers().isEmpty()) {
        List<Path<Object>> fillersPath = generateFillersPath(requestFilter, joinFlowDoc);
        selection.addAll(fillersPath);
        grouping.addAll(fillersPath);
      }
      query.multiselect(selection).groupBy(grouping);
      return null;
    };
  }

  public static Specification<FlowTraceabilityReport> docPndMndFillersGrouping(
      StatisticRequestFilter requestFilter) {
    return (root, query, criteriaBuilder) -> {
      var joinFlowDoc =
          SpecificationUtils.getOrCreateJoin(root, FlowTraceabilityReport_.flowDocumentReports);

      List<Selection<?>> selection = new ArrayList<>();
      selection.add(criteriaBuilder.count(joinFlowDoc.get(FlowDocumentReport_.id)));
      List<Path<Object>> fillersPath = generateFillersPath(requestFilter, joinFlowDoc);
      List<Expression<?>> grouping = new ArrayList<>();
      if (!fillersPath.isEmpty()) {
        selection.addAll(fillersPath);
        grouping.addAll(fillersPath);
      }
      query.multiselect(selection).groupBy(grouping);

      return null;
    };
  }

  public static Specification<FlowTraceabilityReport> docSummaryFillersGrouping(
      StatisticRequestFilter requestFilter) {
    return (root, query, criteriaBuilder) -> {
      var joinFlowDoc =
          SpecificationUtils.getOrCreateJoin(root, FlowTraceabilityReport_.flowDocumentReports);

      List<Selection<?>> selection = new ArrayList<>();
      selection.add(criteriaBuilder.count(joinFlowDoc.get(FlowDocumentReport_.id)));
      selection.add(joinFlowDoc.get(FlowDocumentReport_.status));
      List<Path<Object>> fillersPath = generateFillersPath(requestFilter, joinFlowDoc);
      List<Expression<?>> grouping = new ArrayList<>();
      grouping.add(joinFlowDoc.get(FlowDocumentReport_.status));
      if (!fillersPath.isEmpty()) {
        selection.addAll(fillersPath);
        grouping.addAll(fillersPath);
      }
      query.multiselect(selection).groupBy(grouping);

      return null;
    };
  }

  public static Specification<FlowTraceabilityReport> genDocSumBySubStatusSpecs(
      StatisticRequestFilter requestFilter) {
    Specification<FlowTraceabilityReport> querySpecs = ownerIdIn(requestFilter.getOwnerIds());
    querySpecs =
        querySpecs.and(
            withinRequestDateRange(
                requestFilter.getStartDate(),
                requestFilter.getEndDate(),
                requestFilter.getRequestedAt()));

    querySpecs = querySpecs.and(withCategories(requestFilter.getCategories()));
    if (!CollectionUtils.isEmpty(requestFilter.getFillerKeyText())) {
      querySpecs = querySpecs.and(withGroupFillers(requestFilter.getFillerKeyText()));
    }
    if (!CollectionUtils.isEmpty(requestFilter.getSubStatuses())) {
      querySpecs = querySpecs.and(withLatestSubStatus());
      querySpecs = querySpecs.and(withSubStatuses(requestFilter.getSubStatuses()));
    }
    querySpecs = querySpecs.and(docSummaryPNDMNDSpec(requestFilter));
    return querySpecs;
  }

  public static Specification<FlowTraceabilityReport> generatePostPndMndSpecs(
      StatisticRequestFilter requestFilter) {

    Specification<FlowTraceabilityReport> querySpecs = ownerIdIn(requestFilter.getOwnerIds());

    querySpecs =
        querySpecs.and(
            withinRequestDateRange(
                requestFilter.getStartDate(),
                requestFilter.getEndDate(),
                requestFilter.getRequestedAt()));

    var pndMndStatus = new ArrayList<>(List.of(FlowDocumentStatus.COMPLETED.getValue()));
    if (FlowDocumentChannelConstant.DIGITAL.equals(requestFilter.getChannels().get(0))) {
      pndMndStatus.add(FlowDocumentStatus.IN_ERROR.getValue());
    }
    querySpecs = querySpecs.and(withStatuses(pndMndStatus));

    querySpecs = querySpecs.and(withCategories(requestFilter.getCategories()));

    if (!requestFilter.getFillerKeyText().isEmpty()) {
      querySpecs = querySpecs.and(withGroupFillers(requestFilter.getFillerKeyText()));
    }

    if (!CollectionUtils.isEmpty(requestFilter.getSubStatuses())) {
      querySpecs = querySpecs.and(withSubStatuses(requestFilter.getSubStatuses()));
    }

    querySpecs = querySpecs.and(docPndMndFillersGrouping(requestFilter));

    return querySpecs;
  }

  public static Specification<FlowTraceabilityReport> generatePNDMNDPostSpec(
      StatisticRequestFilter requestFilter) {
    Specification<FlowTraceabilityReport> querySpecs = ownerIdIn(requestFilter.getOwnerIds());
    querySpecs =
        querySpecs.and(
            withinRequestDateRange(
                requestFilter.getStartDate(),
                requestFilter.getEndDate(),
                requestFilter.getRequestedAt()));
    querySpecs = querySpecs.and(withStatuses(List.of(FlowDocumentStatus.COMPLETED.getValue())));
    querySpecs = querySpecs.and(withCategories(requestFilter.getCategories()));
    if (!CollectionUtils.isEmpty(requestFilter.getFillerKeyText())) {
      querySpecs = querySpecs.and(withGroupFillers(requestFilter.getFillerKeyText()));
    }
    if (!CollectionUtils.isEmpty(requestFilter.getSubStatuses())) {
      querySpecs = querySpecs.and(withSubStatuses(requestFilter.getSubStatuses()));
    }
    querySpecs = querySpecs.and(docSummaryPNDMNDSpec(requestFilter));
    return querySpecs;
  }

  public static Specification<FlowTraceabilityReport> generatePostSpecs(
      StatisticRequestFilter requestFilter) {

    Specification<FlowTraceabilityReport> querySpecs = ownerIdIn(requestFilter.getOwnerIds());

    querySpecs =
        querySpecs.and(
            withinRequestDateRange(
                requestFilter.getStartDate(),
                requestFilter.getEndDate(),
                requestFilter.getRequestedAt()));

    if (!CollectionUtils.isEmpty(requestFilter.getStatuses())) {
      querySpecs = querySpecs.and(withStatuses(requestFilter.getStatuses()));
    }

    querySpecs = querySpecs.and(withCategories(requestFilter.getCategories()));

    if (!requestFilter.getFillerKeyText().isEmpty()) {
      querySpecs = querySpecs.and(withGroupFillers(requestFilter.getFillerKeyText()));
    }

    querySpecs = querySpecs.and(docSummaryFillersGrouping(requestFilter));

    return querySpecs;
  }
}
