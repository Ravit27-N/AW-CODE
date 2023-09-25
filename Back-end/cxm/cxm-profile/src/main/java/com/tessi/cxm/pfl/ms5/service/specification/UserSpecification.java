package com.tessi.cxm.pfl.ms5.service.specification;

import com.tessi.cxm.pfl.ms5.entity.Client;
import com.tessi.cxm.pfl.ms5.entity.Client_;
import com.tessi.cxm.pfl.ms5.entity.Department;
import com.tessi.cxm.pfl.ms5.entity.Department_;
import com.tessi.cxm.pfl.ms5.entity.Division;
import com.tessi.cxm.pfl.ms5.entity.Division_;
import com.tessi.cxm.pfl.ms5.entity.Profile;
import com.tessi.cxm.pfl.ms5.entity.Profile_;
import com.tessi.cxm.pfl.ms5.entity.UserEntity;
import com.tessi.cxm.pfl.ms5.entity.UserEntity_;
import com.tessi.cxm.pfl.ms5.entity.UserProfiles;
import com.tessi.cxm.pfl.ms5.entity.UserProfiles_;
import com.tessi.cxm.pfl.shared.utils.SpecificationUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public class UserSpecification {

  public static Specification<UserEntity> contains(String filter) {
    if (!StringUtils.hasText(filter)) {
      return null;
    }
    var filterFormatted = SpecificationUtils.getStringLikeFormat(filter.toLowerCase());
    return (root, query, cb) -> cb.or(cb.like(cb.lower(root.get(UserEntity_.FIRST_NAME)), filterFormatted),
        cb.like(cb.lower(root.get(UserEntity_.LAST_NAME)), filterFormatted),
        cb.like(cb.lower(root.get(UserEntity_.EMAIL)), filterFormatted));
  }

  public static Specification<UserEntity> createdByIn(Collection<String> users) {
    return SpecificationUtils.in(UserEntity_.createdBy, users);
  }

  public static Specification<UserEntity> usernameIn(Collection<String> users) {
    return SpecificationUtils.in(UserEntity_.username, users);
  }

  public static Specification<UserEntity> inProfiles(Collection<Long> ids) {
    if (ids.isEmpty()) {
      return null;
    }
    return (root, query, cb) -> {
      final List<Predicate> predicates = new ArrayList<>();

      final Subquery<UserEntity> userSubQuery = query.subquery(UserEntity.class);
      final Root<UserEntity> user = userSubQuery.from(UserEntity.class);
      final Path<Collection<UserProfiles>> userProfiles = user.join(UserEntity_.USER_PROFILES,
          JoinType.INNER);
      final Path<Profile> profile = userProfiles.get(UserProfiles_.PROFILE);
      userSubQuery.select(user.get(UserEntity_.ID));
      userSubQuery.where(profile.get(Profile_.ID).in(ids));
      predicates.add(root.get(UserEntity_.ID).in(userSubQuery));
      return cb.and(predicates.toArray(new Predicate[0]));
    };
  }

  public static Specification<UserEntity> isActive() {
    return (root, query, cb) -> cb.isTrue(root.get(UserEntity_.IS_ACTIVE));
  }

  public static Specification<UserEntity> serviceIn(Collection<Long> serviceIds) {
    return (root, query, criteriaBuilder) -> root.get(UserEntity_.department).get(Department_.ID)
        .in(serviceIds);
  }

  public static Specification<UserEntity> clientEqual(Long clientId) {
    return (root, query, cb) -> {
      Join<UserEntity, Client> join = root
          .join(UserEntity_.DEPARTMENT, JoinType.INNER)
          .join(Department_.DIVISION, JoinType.INNER)
          .join(Division_.CLIENT, JoinType.INNER);
      return cb.equal(join.get(Client_.ID), clientId);
    };
  }

  public static Specification<UserEntity> onlyAdminUser() {
    return (root, query, cb) -> cb.isTrue(root.get(UserEntity_.IS_ADMIN));
  }

  public static Specification<UserEntity> onlyNormalUser() {
    return (root, query, cb) -> cb.isFalse(root.get(UserEntity_.IS_ADMIN));
  }

  // add new
  public static Specification<UserEntity> clientsIn(List<Long> clientIds) {
    if (clientIds.isEmpty()) {
      return null;
    }
    return (root, query, cb) -> {
      Join<UserEntity, Client> join = root
          .join(UserEntity_.DEPARTMENT, JoinType.INNER)
          .join(Department_.DIVISION, JoinType.INNER)
          .join(Division_.CLIENT, JoinType.INNER);
      return join.get(Client_.ID).in(clientIds);
    };
  }

  public static Specification<UserEntity> divisionsIn(List<Long> divisionIds) {
    if (divisionIds.isEmpty()) {
      return null;
    }
    return (root, query, cb) -> {
      Join<UserEntity, Division> join = root
          .join(UserEntity_.DEPARTMENT, JoinType.INNER)
          .join(Department_.DIVISION, JoinType.INNER);
      return join.get(Division_.ID).in(divisionIds);
    };
  }

  public static Specification<UserEntity> servicesIn(List<Long> serviceIds) {
    if (serviceIds.isEmpty()) {
      return null;
    }
    return (root, query, cb) -> {
      Join<UserEntity, Department> join = root
          .join(UserEntity_.DEPARTMENT, JoinType.INNER);
      return join.get(Department_.ID).in(serviceIds);
    };
  }
}
