package com.innovationandtrust.utils.commons;

import static com.innovationandtrust.utils.commons.CommonUsages.castToRequiredType;

import com.innovationandtrust.share.utils.SpecUtils;
import com.innovationandtrust.utils.exception.exceptions.QueryOperationNotSupportedException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Selection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

/** Advanced filter with specification. */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AdvancedFilter {

  public static <T> Specification<T> searchByField(Filter filter) {
    log.info(filter.toString());
    return specification(filter);
  }

  /**
   * Use for getting specifications with filters.
   *
   * @param filters list of filer {@link Filter}
   * @return Specification of T class
   */
  public static <T> Specification<T> searchByFields(List<Filter> filters) {

    // Copy to prevent remove source element at index 0
    var filterList = new ArrayList<>(filters);

    Specification<T> specification = Specification.where(specification(filterList.remove(0)));

    for (Filter filter : filters) {
      log.info(filter.toString());
      if (Objects.nonNull(filter.getLogicalOperator())
          && Objects.equals(LogicalOperator.OR, filter.getLogicalOperator())) {
        specification = specification.or(specification(filter));
      } else {
        specification = specification.and(specification(filter));
      }
    }

    return specification;
  }

  private static <T> Specification<T> specification(Filter filter) {
    if (!validate(filter)) {
      return null;
    }
    switch (filter.getOperator()) {
      case EQUALS -> {
        return (root, query, cb) -> {
          var value =
              castToRequiredType(getCriteriaPath(root, filter).getJavaType(), filter.getValue());
          return Objects.nonNull(value) ? cb.equal(getCriteriaPath(root, filter), value) : null;
        };
      }
      case NOT_EQUALS -> {
        return (root, query, cb) ->
            cb.notEqual(
                getCriteriaPath(root, filter),
                castToRequiredType(getCriteriaPath(root, filter).getJavaType(), filter.getValue()));
      }
      case IN -> {
        return (root, query, cb) -> getCriteriaPath(root, filter).in(filter.getValues());
      }
      case LIKE -> {
        return (root, query, cb) ->
            cb.like(
                replaceSpaces(cb, getCriteriaPath(root, filter).as(String.class)),
                SpecUtils.likeQuery(filter.getValue()));
      }
      case GREATER_THAN -> {
        return (root, query, cb) -> {
          var value =
              (Number)
                  castToRequiredType(root.get(filter.getField()).getJavaType(), filter.getValue());
          return Objects.nonNull(value) ? cb.gt(root.get(filter.getField()), value) : null;
        };
      }
      case LESS_THAN -> {
        return (root, query, cb) -> {
          var value =
              (Number)
                  castToRequiredType(root.get(filter.getField()).getJavaType(), filter.getValue());
          return Objects.nonNull(value) ? cb.lt(root.get(filter.getField()), value) : null;
        };
      }
      case BETWEEN -> {
        return (root, query, cb) ->
            cb.between(
                root.get(filter.getField()), filter.getValues().get(0), filter.getValues().get(1));
      }
      case BETWEEN_DATE -> {
        return (root, query, cb) -> {
          var startDate =
              (Date)
                  castToRequiredType(
                      root.get(filter.getField()).getJavaType(), filter.getValues().get(0));
          var endDate =
              (Date)
                  castToRequiredType(
                      root.get(filter.getField()).getJavaType(), filter.getValues().get(1));

          return Objects.nonNull(startDate) && Objects.nonNull(endDate)
              ? cb.between(root.get(filter.getField()), startDate, endDate)
              : null;
        };
      }
      case OR -> {
        return (root, query, cb) -> getPredicateSameColumns(root, cb, filter);
      }
      case OR_FIELDS -> {
        return (root, query, cb) -> getPredicateDifferentColumns(root, cb, filter);
      }
      case IS_NOT_NULL -> {
        return (root, query, cb) -> cb.isNotNull(getCriteriaPath(root, filter));
      }
      case IS_NULL -> {
        return (root, query, cb) -> cb.isNull(getCriteriaPath(root, filter));
      }
      case IS_TRUE -> {
        return (root, query, cb) -> cb.isTrue(root.get(filter.getField()));
      }
      case IS_FALSE -> {
        return (root, query, cb) -> cb.isFalse(root.get(filter.getField()));
      }

      default -> throw new QueryOperationNotSupportedException("Operation not supported yet");
    }
  }

  private static <T> Path<T> getCriteriaPath(Root<T> root, Filter filter) {
    Path<T> paths = root;

    if (!StringUtils.hasText(filter.getField())) {
      for (String field : filter.getReferenceField()) {
        paths = paths.get(field);
      }
    } else {
      paths = paths.get(filter.getField());
    }

    return paths;
  }

  // Get predicate with the same columns but different values
  private static <T> Predicate getPredicateSameColumns(
      Root<T> root, CriteriaBuilder cb, Filter filter) {
    return filter.getValues().isEmpty() || !Objects.nonNull(filter.getField())
        ? cb.or()
        : cb.or(
            filter.getValues().stream()
                .map(value -> cb.equal(getCriteriaPath(root, filter), value))
                .toArray(Predicate[]::new));
  }

  // Get predicate with different columns but the same value
  private static <T> Predicate getPredicateDifferentColumns(
      Root<T> root, CriteriaBuilder cb, Filter filter) {
    return filter.getFields().isEmpty() || !Objects.nonNull(filter.getValue())
        ? cb.or()
        : cb.or(
            filter.getFields().stream()
                .map(field -> cb.equal(root.get(field), filter.getValue()))
                .toArray(Predicate[]::new));
  }

  private static List<Selection<?>> getCriteriaSelectColumns(Root<?> root, Filter filter) {
    if (Objects.nonNull(filter.getSelectColumns())) {
      List<Selection<?>> columns = new ArrayList<>();
      for (String column : filter.getSelectColumns()) {
        columns.add(root.get(column));
      }
      return columns;
    }
    return Collections.emptyList();
  }

  private static boolean validate(Filter filter) {
    switch (filter.getOperator()) {
      case EQUALS, NOT_EQUALS, LIKE, GREATER_THAN, LESS_THAN -> {
        return hasText(filter.getValue());
      }
      case IN -> {
        if (!Objects.nonNull(filter.getValues()) || filter.getValues().isEmpty()) {
          return false;
        }
      }
      case BETWEEN, BETWEEN_DATE -> {
        if (noField(filter)
            || !Objects.nonNull(filter.getValues())
            || filter.getValues().isEmpty()) {
          return false;
        }
      }
      case IS_NOT_NULL, IS_NULL, IS_FALSE, IS_TRUE -> {
        if (noField(filter)) {
          return false;
        }
      }
      default -> {
        return false;
      }
    }

    return true;
  }

  private static boolean hasText(String value) {
    return StringUtils.hasText(value);
  }

  private static boolean noField(Filter filter) {
    if (!Objects.nonNull(filter.getField())) {
      return true;
    }
    return Objects.nonNull(filter.getReferenceField());
  }

  public static Expression<String> replaceSpaces(
      CriteriaBuilder cb, Expression<String> expression) {
    return cb.function(
        "REPLACE", String.class, cb.lower(expression), cb.literal(" "), cb.literal(""));
  }
}
