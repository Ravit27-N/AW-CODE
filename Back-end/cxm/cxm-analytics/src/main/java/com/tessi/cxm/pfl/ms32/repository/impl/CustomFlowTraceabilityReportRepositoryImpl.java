package com.tessi.cxm.pfl.ms32.repository.impl;

import com.tessi.cxm.pfl.ms32.dto.DocumentTotalDto;
import com.tessi.cxm.pfl.ms32.dto.StatisticRequestFilter;
import com.tessi.cxm.pfl.ms32.dto.ProductionDetails;
import com.tessi.cxm.pfl.ms32.dto.ProductionDetailsProjection;
import com.tessi.cxm.pfl.ms32.entity.FlowDocumentReport;
import com.tessi.cxm.pfl.ms32.entity.FlowDocumentReportHistory;
import com.tessi.cxm.pfl.ms32.entity.FlowDocumentReportHistory_;
import com.tessi.cxm.pfl.ms32.entity.FlowDocumentReport_;
import com.tessi.cxm.pfl.ms32.entity.FlowTraceabilityReport;
import com.tessi.cxm.pfl.ms32.entity.FlowTraceabilityReport_;
import com.tessi.cxm.pfl.ms32.repository.AbstractStatisticRepository;
import com.tessi.cxm.pfl.ms32.repository.CustomFlowTraceabilityReportRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.SetJoin;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

@Repository
public class CustomFlowTraceabilityReportRepositoryImpl extends AbstractStatisticRepository
    implements CustomFlowTraceabilityReportRepository {

  @PersistenceContext private EntityManager entityManager;

  @Override
  public List<DocumentTotalDto> reportDocument(StatisticRequestFilter requestFilter) {

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    var query = criteriaBuilder.createQuery(DocumentTotalDto.class);

    Root<FlowTraceabilityReport> root = query.from(FlowTraceabilityReport.class);
    SetJoin<FlowTraceabilityReport, FlowDocumentReport> joinFlowDoc =
        root.join(FlowTraceabilityReport_.flowDocumentReports);

    var predicateList = new ArrayList<Predicate>();
    predicateList.add(this.generateOwnerPredicate(requestFilter.getOwnerIds(), root));
    predicateList.add(this.generateDateRangePredicate(requestFilter, joinFlowDoc, criteriaBuilder));
    if (!CollectionUtils.isEmpty(requestFilter.getStatuses())) {
      predicateList.add(
          this.generateStatusPredicate(requestFilter.getStatuses(), joinFlowDoc, criteriaBuilder));
    }
    predicateList.add(
        this.generateSubChannelPredicate(requestFilter, joinFlowDoc, criteriaBuilder));
    if (StringUtils.isNotBlank(requestFilter.getSearchByFiller())
        && requestFilter.isGlobalFillers()) {
      predicateList.add(this.generateFillerPredicate(requestFilter, joinFlowDoc, criteriaBuilder));
    }
    if (!requestFilter.getFillerKeyText().isEmpty() && !requestFilter.isGlobalFillers()) {
      predicateList.add(
          this.generateFillerGroupTextPredicate(requestFilter, joinFlowDoc, criteriaBuilder));
    }

    query
        .where(predicateList.toArray(new Predicate[0]))
        .multiselect(
            root.get(FlowTraceabilityReport_.channel),
            joinFlowDoc.get(FlowDocumentReport_.subChannel),
            criteriaBuilder.count(joinFlowDoc.get(FlowDocumentReport_.subChannel)))
        .groupBy(
            root.get(FlowTraceabilityReport_.channel),
            joinFlowDoc.get(FlowDocumentReport_.subChannel));

    return this.entityManager.createQuery(query).getResultList().stream()
        .map(DocumentTotalDto.class::cast)
        .collect(Collectors.toList());
  }

  @Override
  public List<ProductionDetails> reportProductionDetails(
      StatisticRequestFilter requestFilter) {
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    var query = criteriaBuilder.createQuery(ProductionDetailsProjection.class);

    Root<FlowTraceabilityReport> root = query.from(FlowTraceabilityReport.class);
    SetJoin<FlowTraceabilityReport, FlowDocumentReport> joinFlowDoc =
        root.join(FlowTraceabilityReport_.flowDocumentReports);
    SetJoin<FlowDocumentReport, FlowDocumentReportHistory> joinDocHistory =
        joinFlowDoc.join(FlowDocumentReport_.flowDocumentHistories);
    var predicateList = new ArrayList<Predicate>();
    predicateList.add(this.generateOwnerPredicate(requestFilter.getOwnerIds(), root));
    predicateList.add(this.generateDateRangePredicate(requestFilter, joinFlowDoc, criteriaBuilder));
    predicateList.add(
        this.generateSubStatusPredicate(
            requestFilter.getStatuses(), joinDocHistory, criteriaBuilder));
    predicateList.add(
        this.generateSubChannelPredicate(requestFilter, joinFlowDoc, criteriaBuilder));
    if (StringUtils.isNotBlank(requestFilter.getSearchByFiller())
        && requestFilter.isGlobalFillers()) {
      predicateList.add(this.generateFillerPredicate(requestFilter, joinFlowDoc, criteriaBuilder));
    } else if (!requestFilter.getFillerKeyText().isEmpty() && !requestFilter.isGlobalFillers()) {
      predicateList.add(
          this.generateFillerGroupTextPredicate(requestFilter, joinFlowDoc, criteriaBuilder));
    }

    query
        .where(predicateList.toArray(new Predicate[0]))
        .multiselect(
            root.get(FlowTraceabilityReport_.channel),
            joinFlowDoc.get(FlowDocumentReport_.subChannel),
            joinDocHistory.get(FlowDocumentReportHistory_.status),
            criteriaBuilder.count(joinFlowDoc.get(FlowDocumentReport_.subChannel)))
        .groupBy(
            root.get(FlowTraceabilityReport_.channel),
            joinFlowDoc.get(FlowDocumentReport_.subChannel),
            joinDocHistory.get(FlowDocumentReportHistory_.status));

    return this.entityManager.createQuery(query).getResultList().stream()
        .map(ProductionDetails.class::cast)
        .collect(Collectors.toList());
  }
}
