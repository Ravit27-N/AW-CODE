package com.tessi.cxm.pfl.ms3.service.specification;

import com.tessi.cxm.pfl.ms3.entity.FlowDeposit;
import com.tessi.cxm.pfl.ms3.entity.FlowDeposit_;
import com.tessi.cxm.pfl.ms3.entity.FlowTraceability;
import com.tessi.cxm.pfl.ms3.entity.FlowTraceability_;
import com.tessi.cxm.pfl.shared.utils.SpecificationUtils;
import java.util.Collection;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
/**
 * Generate a specification from {@link com.tessi.cxm.pfl.ms3.entity.FlowDeposit}.
 *
 * @author Piseth KHON
 * @since 09/29/2022
 */
public class FlowDepositSpecification {
  private Specification<FlowDeposit> spec;
  private Specification<FlowDeposit> other;
  private WhereClues whereClues;

  public FlowDepositSpecification() {}

  private FlowDepositSpecification(Specification<FlowDeposit> spec, WhereClues whereClues) {
    this.spec = spec;
    this.whereClues = whereClues;
  }
  /**
   * Searching value by attribute names.
   *
   * @since 1.0
   */
  public SpecificationCompositionDeposit searching(List<String> attributeNames, String searcher) {
    this.other =
        (root, query, criteriaBuilder) -> {
          if (!StringUtils.hasText(searcher)) {
            return null;
          }
          return criteriaBuilder.or(
              attributeNames.stream()
                  .map(attributeName -> this.toPath(attributeName, root))
                  .filter(Objects::nonNull)
                  .map(
                      path ->
                          criteriaBuilder.like(
                              criteriaBuilder.lower(path),
                              SpecificationUtils.getStringLikeFormat(searcher)))
                  .toArray(Predicate[]::new));
        };
    return new SpecificationCompositionDeposit(this.spec, this.other, whereClues);
  }

  public SpecificationCompositionDeposit matching(String attributeName, List<String> matcher) {
    return matching(attributeName, matcher, false);
  }

  public SpecificationCompositionDeposit noneMatching(String attributeName, boolean matcher) {
    return matching(attributeName, Collections.singletonList(String.valueOf(matcher)), true);
  }

  public SpecificationCompositionDeposit matching(String attributeName, boolean matcher) {
    return matching(attributeName, Collections.singletonList(String.valueOf(matcher)), false);
  }

  public SpecificationCompositionDeposit noneMatching(String attributeName, List<String> matcher) {
    return matching(attributeName, matcher, true);
  }
  /**
   * Finding a match value with attribute name;
   *
   * @since 1.0
   */
  public SpecificationCompositionDeposit matching(
      String attributeName, List<String> matcher, boolean noneMatch) {
    this.other =
        (root, query, criteriaBuilder) -> {
          if (CollectionUtils.isEmpty(matcher)) {
            return null;
          }
          final List<Boolean> booleanMatches =
              matcher.stream()
                  .filter(this::isBoolean)
                  .map(Boolean::valueOf)
                  .collect(Collectors.toList());
          if (!booleanMatches.isEmpty()) {
            if (noneMatch) {
              return criteriaBuilder.notEqual(
                  this.toPath(attributeName, root), booleanMatches.stream().findFirst().get());
            }
            return criteriaBuilder.equal(
                this.toPath(attributeName, root), booleanMatches.stream().findFirst().get());
          }

          final Predicate in =
              criteriaBuilder
                  .lower(this.toPath(attributeName, root))
                  .in(matcher.stream().map(String::toLowerCase).collect(Collectors.toList()));
          return (noneMatch) ? in.not() : in;
        };
    return new SpecificationCompositionDeposit(this.spec, this.other, whereClues);
  }

  /**
   * Generate Path expression from given attribute name.
   *
   * @since 1.0
   */
  private Path<String> toPath(String attributeName, Root<FlowDeposit> root) {
    if (isRoot(attributeName, root)) {
      return root.get(attributeName);
    }
    if (isJoin(attributeName, root) != null) {
      return isJoin(attributeName, root);
    }
    return null;
  }

  private boolean isBoolean(String value) {
    return "true".equals(value) || "false".equals(value);
  }

  private boolean isRoot(String attributeName, Root<FlowDeposit> root) {
    try {
      root.get(attributeName);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  private Path<String> isJoin(String attributeName, Root<FlowDeposit> root) {
    final Path<FlowTraceability> flowTraceabilityPath = root.get(FlowDeposit_.flowTraceability);
    try {
      return flowTraceabilityPath.get(attributeName);
    } catch (Exception e) {
      return null;
    }
  }
  /**
   * Merge the current one to a where clause.
   *
   * @since 1.0
   */
  public Specification<FlowDeposit> toSpecification() {
    return Specification.where(this.spec);
  }
  /**
   * Helper class to support specification compositions.
   *
   * @see Specification
   * @author Piseth KHON
   * @since 09/29/2022
   */
  public static class SpecificationCompositionDeposit {
    private Specification<FlowDeposit> spec;
    private final Specification<FlowDeposit> other;
    WhereClues whereClues;

    SpecificationCompositionDeposit(
        Specification<FlowDeposit> spec, Specification<FlowDeposit> other, WhereClues whereClues) {
      this.other = other;
      this.spec = spec;
      this.whereClues = whereClues;
    }
    /**
     * ANDs the given {@link Specification} to the current one.
     *
     * @since 1.0
     */
    public FlowDepositSpecification and() {
      if (ObjectUtils.isEmpty(this.spec)) {
        this.spec = this.other;
        return new FlowDepositSpecification(this.spec, WhereClues.AND);
      }
      this.spec = this.spec.and(other);
      return new FlowDepositSpecification(this.spec, WhereClues.AND);
    }

    /**
     * ORs the given specification to the current one.
     *
     * @since 1.0
     */
    public FlowDepositSpecification or() {
      if (ObjectUtils.isEmpty(this.spec)) {
        this.spec = this.other;
        return new FlowDepositSpecification(this.spec, WhereClues.OR);
      }
      this.spec = this.spec.or(other);
      return new FlowDepositSpecification(this.spec, WhereClues.OR);
    }

    /**
     * Merge the last given specification to the current one.
     *
     * @since 1.0
     */
    public FlowDepositSpecification end() {
      switch (whereClues) {
        case OR:
          return or();
        case AND:
          return and();
        default:
          return new FlowDepositSpecification(this.spec, WhereClues.OR);
      }
    }
  }

  private enum WhereClues {
    OR,
    AND
  }

  public static Specification<FlowDeposit> containsFlowIds(List<Long> flowIds) {
    return (root, query, cb) -> {
      final Path<FlowDeposit> flowDeposit = root.join(
          FlowDeposit_.FLOW_TRACEABILITY);
      return cb.in(flowDeposit.get(FlowTraceability_.ID)).value(flowIds);
    };
  }

  public static Specification<FlowDeposit> flowOwnerIdIn(Collection<Long> ids) {
    return (root, query, criteriaBuilder) -> {
      final Path<FlowDeposit> flowDeposit = root.join(FlowDeposit_.FLOW_TRACEABILITY);
      return criteriaBuilder.in(flowDeposit.get(FlowTraceability_.OWNER_ID)).value(ids);
    };
  }
}
