package com.tessi.cxm.pfl.ms32.repository;

import com.tessi.cxm.pfl.ms32.dto.GlobalStatisticRequestFilter;
import com.tessi.cxm.pfl.ms32.dto.StatisticRequestFilter;
import com.tessi.cxm.pfl.ms32.entity.FlowDocumentReport;
import com.tessi.cxm.pfl.ms32.entity.FlowDocumentReportHistory;
import com.tessi.cxm.pfl.ms32.entity.FlowDocumentReportHistory_;
import com.tessi.cxm.pfl.ms32.entity.FlowDocumentReport_;
import com.tessi.cxm.pfl.ms32.entity.FlowTraceabilityReport;
import com.tessi.cxm.pfl.ms32.entity.FlowTraceabilityReport_;
import com.tessi.cxm.pfl.ms32.util.DateHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.SetJoin;

public abstract class AbstractStatisticRepository {
  protected Predicate generateOwnerPredicate(
      List<Long> ownerIds, Root<FlowTraceabilityReport> root) {
    return root.get(FlowTraceabilityReport_.ownerId).in(ownerIds);
  }

  protected Predicate generateSubStatusPredicate(
      List<String> status,
      SetJoin<FlowDocumentReport, FlowDocumentReportHistory> joinDocHistory,
      CriteriaBuilder criteriaBuilder) {
    return criteriaBuilder.and(
        criteriaBuilder.lower(joinDocHistory.get(FlowDocumentReportHistory_.status)).in(status));
  }

  protected Predicate generateSubChannelPredicate(
      GlobalStatisticRequestFilter requestFilter,
      SetJoin<FlowTraceabilityReport, FlowDocumentReport> joinFlowDoc,
      CriteriaBuilder criteriaBuilder) {
    List<String> categories =
        requestFilter.getCategories().stream()
            .map(String::toLowerCase)
            .collect(Collectors.toList());
    return criteriaBuilder.and(
        criteriaBuilder.lower(joinFlowDoc.get(FlowDocumentReport_.subChannel)).in(categories));
  }

  protected Predicate generateDateRangePredicate(
      GlobalStatisticRequestFilter requestFilter,
      SetJoin<FlowTraceabilityReport, FlowDocumentReport> joinFlowDoc,
      CriteriaBuilder criteriaBuilder) {
    return criteriaBuilder.and(
        criteriaBuilder.between(
            joinFlowDoc.get(FlowDocumentReport_.dateReception),
            requestFilter.getStartDate(),
            DateHelper.endOfDate(requestFilter.getEndDate())),
        criteriaBuilder.lessThanOrEqualTo(
            joinFlowDoc.get(FlowDocumentReport_.dateReception), requestFilter.getRequestedAt()));
  }

  protected Predicate generateFillerPredicate(
      GlobalStatisticRequestFilter requestFilter,
      SetJoin<FlowTraceabilityReport, FlowDocumentReport> joinFlowDoc,
      CriteriaBuilder criteriaBuilder) {
    Predicate[] fillerPredicates =
        requestFilter.getFillers().stream()
            .map(
                fillerKey ->
                    criteriaBuilder.like(
                        criteriaBuilder.lower(joinFlowDoc.get(fillerKey.toLowerCase())),
                        "%" + requestFilter.getSearchByFiller().toLowerCase() + "%"))
            .toArray(Predicate[]::new);
    return criteriaBuilder.and(criteriaBuilder.or(fillerPredicates));
  }

  protected Predicate generateFillerGroupTextPredicate(
      StatisticRequestFilter requestFilter,
      SetJoin<FlowTraceabilityReport, FlowDocumentReport> joinFlowDoc,
      CriteriaBuilder criteriaBuilder) {
    List<Predicate> predicates = new ArrayList<>();
    requestFilter
        .getFillerKeyText()
        .forEach(
            (key, value) ->
                predicates.add(
                    criteriaBuilder.like(
                        criteriaBuilder.lower(joinFlowDoc.get(key.toLowerCase())),
                        "%" + value.toLowerCase() + "%")));
    return criteriaBuilder.and(criteriaBuilder.and(predicates.toArray(Predicate[]::new)));
  }

  protected Predicate generateStatusPredicate(
      List<String> statuses,
      SetJoin<FlowTraceabilityReport, FlowDocumentReport> joinFlowDoc,
      CriteriaBuilder criteriaBuilder) {
    List<String> statusesLowerCase =
        statuses.stream().map(String::toLowerCase).collect(Collectors.toList());
    return criteriaBuilder.and(
        criteriaBuilder.lower(joinFlowDoc.get(FlowDocumentReport_.status)).in(statusesLowerCase));
  }

  protected List<Path<Object>> generateFillersPath(
      StatisticRequestFilter requestFilter,
      SetJoin<FlowTraceabilityReport, FlowDocumentReport> joinFlowDoc) {
    return requestFilter.getGroupFillers().stream()
        .map(fillerKey -> joinFlowDoc.get(fillerKey.toLowerCase()))
        .collect(Collectors.toList());
  }
}
