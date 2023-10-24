package com.innovationandtrust.profile.service.spefication;

import com.innovationandtrust.profile.model.entity.Company;
import com.innovationandtrust.profile.model.entity.Company_;
import com.innovationandtrust.share.utils.SpecUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CompanySpec {

  public static Specification<Company> search(String search) {
    if (!StringUtils.hasText(search)) {
      return null;
    }
    return (root, query, cb) ->
        cb.like(cb.lower(root.get(Company_.NAME)), SpecUtils.likeQuery(search));
  }

  public static Specification<Company> findBySiret(String siret) {
    if (!StringUtils.hasText(siret)) {
      return null;
    }
    return (root, query, cb) -> cb.equal(cb.lower(root.get(Company_.SIRET)), siret);
  }

  public static Specification<Company> findByName(String name) {
    if (!StringUtils.hasText(name)) {
      return null;
    }
    return (root, query, cb) -> cb.equal(cb.lower(root.get(Company_.NAME)), name);
  }

  public static Specification<Company> findByNotEqualId(Long id) {
    return (root, query, cb) -> cb.notEqual(root.get(Company_.ID), id);
  }

  public static Specification<Company> findByUuid(String uuid) {
    return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Company_.UUID), uuid));
  }
}
