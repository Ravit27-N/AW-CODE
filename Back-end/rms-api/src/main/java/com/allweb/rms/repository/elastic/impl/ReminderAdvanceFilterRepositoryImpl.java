package com.allweb.rms.repository.elastic.impl;

import com.allweb.rms.entity.dto.ReminderAdvanceFilterRequest;
import com.allweb.rms.entity.jpa.Reminder;
import com.allweb.rms.exception.SystemMailConfigurationNotFoundException;
import com.allweb.rms.repository.jpa.ReminderAdvanceFilterRepository;
import com.allweb.rms.service.SystemConfigurationService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaBuilder.In;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.stereotype.Repository;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class ReminderAdvanceFilterRepositoryImpl implements ReminderAdvanceFilterRepository {
  private static final String DEFAULT_SQL_DATE_FORMAT = "DD-MM-YYYY HH24:MI";
  private static final String INTERVIEW_FIELD = "interview";
  private static final String DESCRIPTION_FIELD = "description";
  private static final String LASTNAME_FIELD = "lastname";
  private static final String FIRSTNAME_FIELD = "firstname";
  private static final String SALUTATION_FIELD = "salutation";
  private static final String REMINDER_TYPE_FIELD = "reminderType";
  private static final String REMINDER_TYPE_ID_FIELD = "id";
  private static final String CANDIDATE_FIELD = "candidate";
  private static final String TITLE_FIELD = "title";
  private static final String DATE_REMINDER_FIELD = "dateReminder";
  private static final String DELETED_FIELD = "deleted";
  private static final String IS_SEND_FIELD = "isSend";

  private static final String CONCAT_SQL_FUNCTION = "concat";
  private static final String TO_CHAR_SQL_FUNCTION = "to_char";

  private final EntityManager entityManager;
  private final CriteriaBuilder criteriaBuilder;
  private final SystemConfigurationService systemConfigurationService;

  @Autowired
  public ReminderAdvanceFilterRepositoryImpl(
      EntityManager entityManager, SystemConfigurationService systemConfigurationService) {
    this.entityManager = entityManager;
    this.criteriaBuilder = entityManager.getCriteriaBuilder();
    this.systemConfigurationService = systemConfigurationService;
  }

  @Override
  public Page<Reminder> findByAdvanceFilters(
      ReminderAdvanceFilterRequest request, Pageable pageable) {
    CriteriaQuery<Reminder> reminderQuery = criteriaBuilder.createQuery(Reminder.class);
    Root<Reminder> reminderEntity = reminderQuery.from(Reminder.class);

    List<Predicate> whereClausePredicates =
        this.getWhereClauseFilterPredicates(reminderEntity, request);

    List<Order> orders = this.getOrderClauses(reminderEntity, pageable);
    reminderQuery.where(whereClausePredicates.toArray(new Predicate[0])).orderBy(orders);

    TypedQuery<Reminder> typedQuery = entityManager.createQuery(reminderQuery);
    typedQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
    typedQuery.setMaxResults(pageable.getPageSize());

    List<Reminder> reminderList = typedQuery.getResultList();
    Long countReminder = countReminder(request);
    return new PageImpl<>(reminderList, pageable, countReminder);
  }

  private Long countReminder(ReminderAdvanceFilterRequest request) {
    CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
    Root<Reminder> reminderEntity = countQuery.from(Reminder.class);
    List<Predicate> whereClausePredicates =
        this.getWhereClauseFilterPredicates(reminderEntity, request);
    countQuery
        .select(criteriaBuilder.count(reminderEntity))
        .where(whereClausePredicates.toArray(new Predicate[0]));
    return entityManager.createQuery(countQuery).getSingleResult();
  }

  private String getSqlDateFormat(String format) {
    format = format.replace("MMM", "mon");
    format = format.replace("MMMM", "month");
    format = format.replace("HH", "HH24");
    format = format.replace("hh", "HH12");
    format = format.replace("mm", "MI");
    format = format.replace("ss", "SS");
    format = format.replace("a", "am");
    return format;
  }

  private List<Order> getOrderClauses(Root<Reminder> reminderEntity, Pageable pageable) {
    List<Order> orders = new ArrayList<>();
    Optional<Sort.Order> candidateOrderObject =
        pageable
            .getSort()
            .filter(predicate -> CANDIDATE_FIELD.equals(predicate.getProperty()))
            .get()
            .findFirst();
    if (candidateOrderObject.isPresent()) {
      Join<Object, Object> candidateEntity = reminderEntity.join(CANDIDATE_FIELD, JoinType.LEFT);
      if (candidateOrderObject.get().getDirection() == Direction.ASC) {
        orders.add(
            criteriaBuilder.asc(
                criteriaBuilder
                    .<String>selectCase()
                    .when(
                        criteriaBuilder.isNotNull(candidateEntity.get(FIRSTNAME_FIELD)),
                        candidateEntity.get(FIRSTNAME_FIELD))));
        orders.add(
            criteriaBuilder.asc(
                criteriaBuilder
                    .<String>selectCase()
                    .when(
                        criteriaBuilder.isNotNull(candidateEntity.get(LASTNAME_FIELD)),
                        candidateEntity.get(LASTNAME_FIELD))));
      } else {
        orders.add(
            criteriaBuilder.desc(
                criteriaBuilder
                    .<String>selectCase()
                    .when(
                        criteriaBuilder.isNull(candidateEntity.get(FIRSTNAME_FIELD)),
                        candidateEntity.get(FIRSTNAME_FIELD))));
        orders.add(
            criteriaBuilder.desc(
                criteriaBuilder
                    .<String>selectCase()
                    .when(
                        criteriaBuilder.isNull(candidateEntity.get(LASTNAME_FIELD)),
                        candidateEntity.get(LASTNAME_FIELD))));
      }
    }
    orders.addAll(
        QueryUtils.toOrders(
            Sort.by(
                pageable
                    .getSort()
                    .filter(predicate -> !CANDIDATE_FIELD.equals(predicate.getProperty()))
                    .toList()),
            reminderEntity,
            this.criteriaBuilder));
    return orders;
  }

  private List<Predicate> getWhereClauseFilterPredicates(
      Root<Reminder> reminderEntity, ReminderAdvanceFilterRequest request) {
    List<Predicate> predicates = new ArrayList<>();
    In<String> inClause = null;
    if (ArrayUtils.isNotEmpty(request.getReminderTypes())) {
      Join<Object, Object> reminderType = reminderEntity.join(REMINDER_TYPE_FIELD, JoinType.INNER);
      inClause =
          criteriaBuilder.in(criteriaBuilder.lower(reminderType.get(REMINDER_TYPE_ID_FIELD)));
      Arrays.stream(request.getReminderTypes()).map(String::toLowerCase).forEach(inClause::value);
    }
    Predicate activeReminderFilterPredicate =
        criteriaBuilder.and(
            criteriaBuilder.equal(reminderEntity.get(DELETED_FIELD), false),
            criteriaBuilder.equal(reminderEntity.get(IS_SEND_FIELD), false));
    Predicate filterPredicate = this.getFilterPredicates(reminderEntity, request);
    if (inClause != null && filterPredicate != null) {
      predicates.add(criteriaBuilder.and(inClause, filterPredicate, activeReminderFilterPredicate));
    } else if (inClause != null) {
      predicates.add(criteriaBuilder.and(inClause, activeReminderFilterPredicate));
    } else if (filterPredicate != null) {
      predicates.add(criteriaBuilder.and(filterPredicate, activeReminderFilterPredicate));
    } else {
      predicates.add(activeReminderFilterPredicate);
    }
    Predicate reminderDateFilterPredicate =
        this.getReminderDateFilterPredicate(reminderEntity, request);
    if (reminderDateFilterPredicate != null) {
      predicates.add(reminderDateFilterPredicate);
    }
    return predicates;
  }

  private Predicate getReminderDateFilterPredicate(
      Root<Reminder> reminderEntity, ReminderAdvanceFilterRequest request) {
    Predicate predicate = null;
    if (request.getFrom() != null && request.getTo() != null) {
      predicate =
          criteriaBuilder.between(
              reminderEntity.get(DATE_REMINDER_FIELD),
              request.getFrom(),
              Date.from(request.getTo().toInstant().plus(1, ChronoUnit.DAYS)));
    } else if (request.getFrom() != null) {
      predicate =
          criteriaBuilder.greaterThanOrEqualTo(
              reminderEntity.get(DATE_REMINDER_FIELD), request.getFrom());
    } else if (request.getTo() != null) {
      predicate =
          criteriaBuilder.lessThanOrEqualTo(
              reminderEntity.get(DATE_REMINDER_FIELD),
              Date.from(request.getTo().toInstant().plus(1, ChronoUnit.DAYS)));
    }
    return predicate;
  }

  private Predicate getFilterPredicates(
      Root<Reminder> reminderEntity, ReminderAdvanceFilterRequest request) {
    if (StringUtils.isBlank(request.getFilter())) {
      return null;
    }
    List<Predicate> filterPredicates = new ArrayList<>();
    String likePattern = "%" + request.getFilter().toLowerCase() + "%";
    filterPredicates.add(this.getCandidateFilterPredicates(reminderEntity, likePattern));
    filterPredicates.addAll(this.getInterviewFilterPredicates(reminderEntity, likePattern));

    filterPredicates.add(
        criteriaBuilder.like(criteriaBuilder.lower(reminderEntity.get(TITLE_FIELD)), likePattern));
    filterPredicates.add(
        criteriaBuilder.like(
            criteriaBuilder.lower(reminderEntity.get(DESCRIPTION_FIELD)), likePattern));
    String dateFormat;
    try {
      dateFormat =
          this.systemConfigurationService.getByConfigKey("datetime.format").getConfigValue();
      dateFormat = this.getSqlDateFormat(dateFormat);
    } catch (SystemMailConfigurationNotFoundException ex) {
      log.debug(ex.getMessage(), ex);
      dateFormat = DEFAULT_SQL_DATE_FORMAT;
    }
    Expression<String> dateReminder =
        criteriaBuilder.function(
            TO_CHAR_SQL_FUNCTION,
            String.class,
            reminderEntity.get(DATE_REMINDER_FIELD),
            criteriaBuilder.literal(dateFormat));
    filterPredicates.add(criteriaBuilder.like(dateReminder, likePattern));

    return criteriaBuilder.or(filterPredicates.toArray(new Predicate[0]));
  }

  private Predicate getCandidateFilterPredicates(
      Root<Reminder> reminderEntity, String likePattern) {
    Join<Object, Object> candidateEntity = reminderEntity.join(CANDIDATE_FIELD, JoinType.LEFT);
    Expression<String> candidateFullName =
        criteriaBuilder.function(
            CONCAT_SQL_FUNCTION,
            String.class,
            criteriaBuilder.lower(candidateEntity.get(SALUTATION_FIELD)),
            criteriaBuilder.literal(" "),
            criteriaBuilder.lower(candidateEntity.get(FIRSTNAME_FIELD)),
            criteriaBuilder.literal(" "),
            criteriaBuilder.lower(candidateEntity.get(LASTNAME_FIELD)));
    return criteriaBuilder.like(candidateFullName, likePattern);
  }

  private List<Predicate> getInterviewFilterPredicates(
      Root<Reminder> reminderEntity, String likePattern) {
    List<Predicate> predicates = new ArrayList<>();
    Join<Object, Object> interviewEntity = reminderEntity.join(INTERVIEW_FIELD, JoinType.LEFT);
    predicates.add(
        criteriaBuilder.like(criteriaBuilder.lower(interviewEntity.get(TITLE_FIELD)), likePattern));
    return Collections.unmodifiableList(predicates);
  }
}
