package com.innovationandtrust.corporate.service.specification;

import com.innovationandtrust.corporate.model.entity.BusinessUnit;
import com.innovationandtrust.corporate.model.entity.BusinessUnit_;
import com.innovationandtrust.corporate.model.entity.CompanyDetail_;
import com.innovationandtrust.share.utils.SpecUtils;
import com.innovationandtrust.utils.commons.AdvancedFilter;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BusinessSpecification {
  public static Specification<BusinessUnit> searchByName(String filter) {
    return (root, query, cb) ->
        cb.like(
            AdvancedFilter.replaceSpaces(cb, root.get(BusinessUnit_.UNIT_NAME)),
            SpecUtils.likeQuery(filter));
  }

  public static Specification<BusinessUnit> findByCompanyId(Long companyId) {
    return (root, query, cb) ->
        cb.equal(root.get(BusinessUnit_.COMPANY_DETAIL).get(CompanyDetail_.COMPANY_ID), companyId);
  }

  public static Specification<BusinessUnit> findByIds(List<Long> ids) {
    return (root, query, cb) -> root.get(BusinessUnit_.ID).in(ids);
  }
}
