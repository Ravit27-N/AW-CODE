package com.allweb.rms.repository.elastic.impl;

import com.allweb.rms.entity.jpa.MailConfiguration;
import com.allweb.rms.exception.MailConfigurationNotFoundException;
import com.allweb.rms.exception.RelationDatabaseException;
import com.allweb.rms.repository.jpa.MailConfigurationAdvanceFilterRepository;
import com.google.api.client.util.Strings;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Session;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

@Log4j2
@Repository
public class MailConfigurationRepositoryImpl implements MailConfigurationAdvanceFilterRepository {

  private static final String ACTIVE = "active";
  private static final String INACTIVE = "inactive";
  private static final String DELETED = "deleted";
  private static final String TITLE = "title";
  private static final String FROM = "from";
  private static final String TO = "to";
  private static final String CC = "cc";
  private static final String CANDIDATE_STATUS = "candidateStatus";
  private static final String MAIL_TEMPLATE = "mailTemplate";
  private static final String SUBJECT = "subject";
  @PersistenceContext private final EntityManager entityManager;
  private final CriteriaBuilder criteriaBuilder;

  @Value("${spring.jpa.properties.hibernate.jdbc.batch-size}")
  private int batchSize;

  public MailConfigurationRepositoryImpl(EntityManager entityManager) {
    this.entityManager = entityManager;
    this.criteriaBuilder = entityManager.getCriteriaBuilder();
  }

  @Override
  public int insertToMailConfigure(MailConfiguration object) {

    try {
      int id =
          entityManager
              .createNativeQuery(
                  "INSERT INTO mail_configuration"
                      + "(id, created_at, created_by, last_modified_by, updated_at, active, deleted, \"from\", title, candidate_status_id, mail_template_id) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")
              .setParameter(1, object.getId())
              .setParameter(2, object.getCreatedAt())
              .setParameter(3, object.getCreatedBy())
              .setParameter(4, object.getLastModifiedBy())
              .setParameter(5, object.getUpdatedAt())
              .setParameter(6, object.isActive())
              .setParameter(7, object.isDeleted())
              .setParameter(8, object.getFrom())
              .setParameter(9, object.getTitle())
              .setParameter(10, object.getCandidateStatus().getId())
              .setParameter(11, object.getMailTemplate().getId())
              .executeUpdate();
      if (!CollectionUtils.isEmpty(object.getCc())) insertToMailConfigCC(object);
      if (!CollectionUtils.isEmpty(object.getTo())) insertToMailConfigTo(object);
      return id;
    } catch (final PersistenceException e) {
      final Throwable cause = e.getCause();
      if (cause instanceof ConstraintViolationException) {
        throw new RelationDatabaseException(
            "Candidate status id " + object.getCandidateStatus().getId() + " is already in use !");
      }
      throw new RelationDatabaseException(e.getMessage());
    }
  }

  @Override
  public void insertToMailConfigTo(MailConfiguration object) {
    String sql =
        "INSERT INTO mail_configuration_to(mail_configuration_mail_template_id,mail_configuration_candidate_status_id,mail_configuration_id, \"to\") VALUES (?,?,?,?)";
    this.batchInsert(object, object.getTo(), sql);
  }

  @Override
  public void insertToMailConfigCC(MailConfiguration object) {
    String sql =
        "INSERT INTO mail_configuration_cc(mail_configuration_mail_template_id,mail_configuration_candidate_status_id,mail_configuration_id, \"cc\") VALUES (?,?,?,?)";
    this.batchInsert(object, object.getCc(), sql);
  }

  /**
   * This method use to perform advanceFilter of {@link MailConfiguration}
   *
   * @implNote 1. {@link CriteriaQuery} : create a {@link CriteriaQuery} object for a query that
   *     return 2. {@link Root}: create root entity of the query (FROM clause) 3. {@link Predicate}:
   *     create condition
   * @param filter is value of string that u want to get it from {@link MailConfiguration}
   * @param selectType is type of result select
   * @param pageable is pagination information. ref {@link Pageable}
   */
  @Override
  public Page<MailConfiguration> advanceFilters(
      Pageable pageable, String filter, String selectType) {
    CriteriaQuery<MailConfiguration> query = criteriaBuilder.createQuery(MailConfiguration.class);
    Root<MailConfiguration> root = query.from(MailConfiguration.class);
    List<Order> order = this.getOrderClauses(root, pageable);
    List<Predicate> whereClauseFilterPredicates =
        this.getWhereClauseFilterPredicates(root, filter, selectType);
    if (whereClauseFilterPredicates != null) {
      query
          .select(root)
          .where(whereClauseFilterPredicates.toArray(new Predicate[0]))
          .orderBy(order);
    } else {
      query.select(root).orderBy(order);
    }
    TypedQuery<MailConfiguration> typedQuery = entityManager.createQuery(query);
    typedQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
    typedQuery.setMaxResults(pageable.getPageSize());
    List<MailConfiguration> mailConfigurationList = typedQuery.getResultList();
    return new PageImpl<>(
        mailConfigurationList, pageable, this.getTotalNumberOfEntities(filter, selectType));
  }
  // set order by clause for statement
  private List<Order> getOrderClauses(Root<MailConfiguration> root, Pageable pageable) {
    List<Order> orderClauses = new ArrayList<>();
    pageable
        .getSort()
        .forEach(
            e -> {
              if (e.getDirection().isAscending()) {
                orderClauses.add(criteriaBuilder.asc(root.get(e.getProperty())));
              } else {
                orderClauses.add(criteriaBuilder.desc(root.get(e.getProperty())));
              }
            });
    return orderClauses;
  }
  // get total row of entity
  private Long getTotalNumberOfEntities(String filters, String selectType) {
    CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
    Root<MailConfiguration> root = countQuery.from(MailConfiguration.class);
    countQuery.select(criteriaBuilder.count(root));
    List<Predicate> whereClauses = this.getWhereClauseFilterPredicates(root, filters, selectType);
    if (whereClauses != null) {
      countQuery.where(whereClauses.toArray(new Predicate[0]));
    }
    return entityManager.createQuery(countQuery).getSingleResult();
  }
  // set where clause of statement
  private List<Predicate> getWhereClauseFilterPredicates(
      Root<MailConfiguration> root, String filter, String selectType) {
    List<Predicate> predicates = new ArrayList<>();
    Predicate filterPredicates = this.getFilterPredicates(root, filter);
    Predicate selectTypePredicate = this.getSelectTypePredicate(root, selectType);
    if (filterPredicates != null && selectTypePredicate != null) {
      predicates.add(criteriaBuilder.and(filterPredicates, selectTypePredicate));
    } else if (filterPredicates != null) {
      predicates.add(criteriaBuilder.or(filterPredicates));
    } else if (selectTypePredicate != null) {
      predicates.add(criteriaBuilder.and(selectTypePredicate));
    } else {
      predicates = null;
    }
    return predicates;
  }
  // set type for select statement of entity
  private Predicate getSelectTypePredicate(Root<MailConfiguration> root, String selectType) {
    switch (selectType.toLowerCase()) {
      case ACTIVE:
        return criteriaBuilder.and(criteriaBuilder.equal(root.get(ACTIVE), true));
      case INACTIVE:
        return criteriaBuilder.and(criteriaBuilder.equal(root.get(ACTIVE), false));
      case DELETED:
        return criteriaBuilder.and(criteriaBuilder.equal(root.get(DELETED), true));
      default:
        return null;
    }
  }
  // set column filter
  private Predicate getFilterPredicates(Root<MailConfiguration> root, String filter) {
    if (Strings.isNullOrEmpty(filter)) {
      return null;
    }
    List<Predicate> filterPredicates = new ArrayList<>();
    String likePattern = "%" + filter.toLowerCase() + "%";
    filterPredicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get(TITLE)), likePattern));
    filterPredicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get(FROM)), likePattern));
    filterPredicates.add(this.getCandidateFilter(root, likePattern));
    filterPredicates.add(this.getMailTemplateFilter(root, likePattern));
    filterPredicates.add(this.getMailConfigTo(root, filter));
    filterPredicates.add(this.getMailConfigCC(root, filter));
    return criteriaBuilder.or(filterPredicates.toArray(new Predicate[0]));
  }
  // get candidateStatus column filter
  private Predicate getCandidateFilter(Root<MailConfiguration> root, String likePattern) {
    Join<Object, Object> candidateStatus = root.join(CANDIDATE_STATUS, JoinType.LEFT);
    return criteriaBuilder.like(criteriaBuilder.lower(candidateStatus.get(TITLE)), likePattern);
  }
  // get mailTemplate column filter
  private Predicate getMailTemplateFilter(Root<MailConfiguration> root, String likePattern) {
    Join<Object, Object> mailTemplate = root.join(MAIL_TEMPLATE, JoinType.LEFT);
    return criteriaBuilder.like(criteriaBuilder.lower(mailTemplate.get(SUBJECT)), likePattern);
  }

  private Predicate getMailConfigTo(Root<MailConfiguration> root, String likePattern) {
    Path<Set<String>> objectPath = root.join(TO);
    return criteriaBuilder.and(objectPath.in(likePattern));
  }

  private Predicate getMailConfigCC(Root<MailConfiguration> root, String likePattern) {
    Path<Set<String>> objectPath = root.join(CC);
    return criteriaBuilder.and(objectPath.in(likePattern));
  }

  // perform batchInsert using prepareStatement
  private void batchInsert(MailConfiguration object, List<String> list, String sql) {
    Session session = entityManager.unwrap(Session.class);
    session.doWork(
        connection -> {
          try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            IntStream.range(0, list.size())
                .forEach(
                    index -> {
                      try {
                        preparedStatement.setInt(1, object.getMailTemplate().getId());
                        preparedStatement.setInt(2, object.getCandidateStatus().getId());
                        preparedStatement.setInt(3, object.getId());
                        preparedStatement.setString(4, list.get(index));
                        preparedStatement.addBatch();
                        // Batch size: 20
                        if (index % batchSize == 0) {
                          preparedStatement.executeBatch();
                          session.flush();
                          session.clear();
                        }
                      } catch (SQLException e) {
                        log.error("Could not execute statement: " + e);
                        throw new MailConfigurationNotFoundException(e.getMessage());
                      }
                    });
            preparedStatement.executeUpdate();
          } catch (SQLException e) {
            log.error("Could not execute statement: " + e);
            throw new MailConfigurationNotFoundException(e.getMessage());
          }
        });
    session.close();
  }
}
