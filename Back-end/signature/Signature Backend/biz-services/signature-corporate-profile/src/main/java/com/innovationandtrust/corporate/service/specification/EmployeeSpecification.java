package com.innovationandtrust.corporate.service.specification;

import com.innovationandtrust.corporate.model.entity.AbstractEntity_;
import com.innovationandtrust.corporate.model.entity.BusinessUnit_;
import com.innovationandtrust.corporate.model.entity.CompanyDetail_;
import com.innovationandtrust.corporate.model.entity.Employee;
import com.innovationandtrust.corporate.model.entity.Employee_;
import com.innovationandtrust.share.utils.SpecUtils;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EmployeeSpecification {

  public static Specification<Employee> findByCorporateId(Long corporateId) {
    return (root, query, cb) ->
        cb.equal(root.get(Employee_.BUSINESS_UNIT).get(AbstractEntity_.CREATED_BY), corporateId);
  }

  public static Specification<Employee> findByCompanyId(List<Long> ids) {
    return ((root, query, cb) ->
        root.get(Employee_.BUSINESS_UNIT)
            .get(BusinessUnit_.COMPANY_DETAIL)
            .get(CompanyDetail_.COMPANY_ID)
            .in(ids));
  }

  public static Specification<Employee> findByBusinessUnit(Long id) {
    return ((root, query, cb) ->
        cb.equal(root.get(Employee_.BUSINESS_UNIT).get(BusinessUnit_.ID), id));
  }

  public static Specification<Employee> findByUserIds(List<Long> userIds) {
    return ((root, query, cb) -> root.get(Employee_.USER_ID).in(userIds));
  }

  public static Specification<Employee> search(String search) {
    if (!StringUtils.hasText(search)) {
      return null;
    }
    return (root, query, cb) ->
        cb.or(
            cb.like(cb.lower(root.get(Employee_.FIRST_NAME)), SpecUtils.likeQuery(search)),
            cb.like(cb.lower(root.get(Employee_.LAST_NAME)), SpecUtils.likeQuery(search)),
            cb.like(
                cb.lower(cb.concat(root.get(Employee_.FIRST_NAME), root.get(Employee_.LAST_NAME))),
                SpecUtils.likeQuery(search)),
            cb.like(
                cb.lower(cb.concat(root.get(Employee_.LAST_NAME), root.get(Employee_.FIRST_NAME))),
                SpecUtils.likeQuery(search)));
  }
}
