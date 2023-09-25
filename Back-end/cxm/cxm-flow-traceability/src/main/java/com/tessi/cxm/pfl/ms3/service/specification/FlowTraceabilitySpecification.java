package com.tessi.cxm.pfl.ms3.service.specification;

import com.cxm.tessi.pfl.shared.flowtreatment.constant.PortalDepositType;
import com.tessi.cxm.pfl.ms3.entity.BaseEntity_;
import com.tessi.cxm.pfl.ms3.entity.FlowDeposit;
import com.tessi.cxm.pfl.ms3.entity.FlowDeposit_;
import com.tessi.cxm.pfl.ms3.entity.FlowTraceability;
import com.tessi.cxm.pfl.ms3.entity.FlowTraceabilityDetails;
import com.tessi.cxm.pfl.ms3.entity.FlowTraceabilityDetails_;
import com.tessi.cxm.pfl.ms3.entity.FlowTraceability_;
import com.tessi.cxm.pfl.shared.utils.FlowTraceabilityStatus;
import com.tessi.cxm.pfl.shared.utils.SpecificationUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.jpa.domain.Specification;

/**
 * The specification for filtering and matching flowTraceability by {@link FlowTraceability}
 * properties.
 *
 * @author Piseth Khon
 * @author Vichet CHANN
 * @since 10/15/21
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FlowTraceabilitySpecification {

  public static Specification<FlowTraceability> containFlowName(String flowName) {
    return SpecificationUtils.getFilterString(FlowTraceability_.flowName, flowName);
  }

  public static Specification<FlowTraceability> containDepositMode(String depositMode) {
    return SpecificationUtils.getFilterString(FlowTraceability_.depositMode, depositMode);
  }

  public static Specification<FlowTraceability> containDepositDate(String depositDate) {
    return SpecificationUtils.getFilterDate(FlowTraceability_.depositDate, depositDate);
  }

  public static Specification<FlowTraceability> containCreatedBy(String createdBy) {
    return SpecificationUtils.getFilterString(BaseEntity_.createdBy, createdBy);
  }

  public static Specification<FlowTraceability> containChannel(String channel) {
    return SpecificationUtils.getFilterString(FlowTraceability_.channel, channel);
  }

  public static Specification<FlowTraceability> containSubChannel(String subChannel) {
    return SpecificationUtils.getFilterString(FlowTraceability_.subChannel, subChannel);
  }

  public static Specification<FlowTraceability> containStatus(String status) {
    return SpecificationUtils.getFilterString(FlowTraceability_.status, status);
  }

  public static Specification<FlowTraceability> equalStatus(String status) {
    return SpecificationUtils.getEqualString(FlowTraceability_.status, status);
  }

  public static Specification<FlowTraceability> containDateStatus(String dateStatus) {
    return SpecificationUtils.getFilterDate(FlowTraceability_.dateStatus, dateStatus);
  }

  public static Specification<FlowTraceability> betweenOrEqualDepositDate(
      String start, String end) {
    return SpecificationUtils.betweenOrEqual(FlowTraceability_.depositDate, start, end);
  }

  public static Specification<FlowTraceability> statusIn(List<String> status) {
    return SpecificationUtils.in(FlowTraceability_.status, status);
  }

  public static Specification<FlowTraceability> statusNotIn(List<String> status) {
    return SpecificationUtils.notIn(FlowTraceability_.status, status);
  }

  public static Specification<FlowTraceability> subChannelIn(List<String> subChannel) {
    return SpecificationUtils.in(FlowTraceability_.subChannel, subChannel);
  }

  public static Specification<FlowTraceability> depositModeIn(List<String> depositModes) {
    return SpecificationUtils.in(FlowTraceability_.depositMode, depositModes);
  }

  public static Specification<FlowTraceability> createdByIn(Collection<String> users) {
    return SpecificationUtils.in(BaseEntity_.createdBy, users);
  }

  public static Specification<FlowTraceability> ownerIdIn(Collection<Long> ids) {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.in(root.get(FlowTraceability_.OWNER_ID)).value(ids);
  }

  public static Specification<FlowTraceability> fileIdByIn(Collection<String> fileIds) {
    return SpecificationUtils.in(FlowTraceability_.fileId, fileIds);
  }

  public static Specification<FlowTraceability> channelIn(List<String> channels) {
    return SpecificationUtils.in(FlowTraceability_.channel, channels);
  }

  /**
   * To filter the flow create from campaign base on status to exclude from the list.
   *
   * @return the statement of {@link javax.persistence.criteria.CriteriaBuilder} after build
   */
  public static Specification<FlowTraceability> ignoreToFinalizeCampaign() {
    return (root, query, cb) -> {
      final List<Predicate> predicates = new ArrayList<>();

      final Subquery<FlowTraceability> flowSubQuery = query.subquery(FlowTraceability.class);
      final Root<FlowTraceability> flow = flowSubQuery.from(FlowTraceability.class);
      final Path<FlowTraceabilityDetails> detail =
          flow.get(FlowTraceability_.FLOW_TRACEABILITY_DETAILS);
      flowSubQuery.select(flow.get(FlowTraceability_.ID));
      flowSubQuery.where(
          cb.and(
              detail
                  .get(FlowTraceabilityDetails_.portalDepositType)
                  .in(
                      List.of(
                          PortalDepositType.CAMPAIGN_EMAIL.name(),
                          PortalDepositType.CAMPAIGN_SMS.name())),
              root.get(FlowTraceability_.STATUS).in(
                  FlowTraceabilityStatus.TO_FINALIZE.getValue(),
                  FlowTraceabilityStatus.DEPOSITED.getValue())));
      predicates.add(cb.not(root.get(FlowTraceability_.ID).in(flowSubQuery)));
      predicates.add(cb.notEqual(root.get(FlowTraceability_.SUB_CHANNEL), ""));
      return cb.and(predicates.toArray(new Predicate[0]));
    };
  }

  /**
   * logic of {@link FlowTraceability}.
   *
   * @param filter refer to any string filter for query database on table column condition
   */
  public static Specification<FlowTraceability> getFlowTraceabilitySpecificationFilter(
      String filter) {
    if (StringUtils.isEmpty(filter)) {
      return Specification.where(FlowTraceabilitySpecification.containChannel(""));
    }
    return Specification.where(
        FlowTraceabilitySpecification.containChannel(filter)
            .or(FlowTraceabilitySpecification.containDepositMode(filter))
            .or(FlowTraceabilitySpecification.containDepositDate(filter))
            .or(FlowTraceabilitySpecification.containCreatedBy(filter))
            .or(FlowTraceabilitySpecification.containFlowName(filter))
            .or(FlowTraceabilitySpecification.containDateStatus(filter))
            .or(FlowTraceabilitySpecification.containSubChannel(filter))
            .or(FlowTraceabilitySpecification.containStatus(filter)));
  }

  /**
   * The specification for querying {@link FlowTraceability} and
   * {@link com.tessi.cxm.pfl.ms3.entity.FlowTraceabilityValidationDetails}.
   *
   * @param id refers to the identity of {@link FlowTraceability}
   * @return the object of {@link CriteriaBuilder}
   */
  public static Specification<FlowTraceability> getFlowValidationInfo(long id) {
    return (root, query, cb) -> {
      final Path<FlowTraceability> flowTraceability = root.join(
          FlowTraceability_.FLOW_TRACEABILITY_VALIDATION_DETAILS);
      return cb.equal(flowTraceability.get(FlowTraceability_.ID), id);
    };
  }

  /**
   * The specification for filter flow that is not deleted.
   *
   * @return the object of {@link javax.persistence.criteria.CriteriaBuilder}
   */
  public static Specification<FlowTraceability> isNotDelete() {
    return (root, query, cb) -> {
      final List<Predicate> predicates = new ArrayList<>();
      final Subquery<FlowDeposit> flowSubQuery = query.subquery(FlowDeposit.class);
      final Root<FlowDeposit> flow = flowSubQuery.from(FlowDeposit.class);
      flowSubQuery.select(flow.get(FlowDeposit_.ID));

      // to find the flow traceability are deleted or field active is false.
      flowSubQuery.where(
          cb.and(cb.equal(flow.get(FlowDeposit_.ID), root.get(FlowTraceability_.ID)),
              cb.isFalse(flow.get(FlowDeposit_.IS_ACTIVE))));

      predicates.add(cb.not(root.get(FlowTraceability_.ID).in(flowSubQuery)));
      return cb.and(predicates.toArray(new Predicate[0]));
    };
  }
}
