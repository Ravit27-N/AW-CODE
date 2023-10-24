package com.innovationandtrust.corporate.service.specification;

import com.innovationandtrust.corporate.model.entity.BusinessUnit_;
import com.innovationandtrust.corporate.model.entity.CompanyDetail;
import com.innovationandtrust.corporate.model.entity.CompanyDetail_;
import com.innovationandtrust.corporate.model.entity.Employee_;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CompanyDetailSpecification {
  public static Specification<CompanyDetail> findByCompanyId(Long companyId) {
    return (root, query, cb) -> cb.equal(root.get(CompanyDetail_.COMPANY_ID), companyId);
  }

  public static Specification<CompanyDetail> findByBusinessUnitId(Long businessUnitId) {
    return (root, query, cb) ->
        cb.equal(root.get(CompanyDetail_.BUSINESS_UNITS).get(BusinessUnit_.ID), businessUnitId);
  }

  public static Specification<CompanyDetail> findUserId(Long userId) {
    return (root, query, cb) ->
        cb.equal(
            root.get(CompanyDetail_.BUSINESS_UNITS)
                .get(BusinessUnit_.EMPLOYEES)
                .get(Employee_.USER_ID),
            userId);
  }
}
