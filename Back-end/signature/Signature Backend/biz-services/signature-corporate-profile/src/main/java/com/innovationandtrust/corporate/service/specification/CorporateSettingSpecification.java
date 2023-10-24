package com.innovationandtrust.corporate.service.specification;

import com.innovationandtrust.corporate.model.entity.CorporateSetting;
import com.innovationandtrust.corporate.model.entity.CorporateSetting_;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CorporateSettingSpecification {
  public static Specification<CorporateSetting> findByCompanyId(Long companyId) {
    return (root, query, cb) -> cb.equal(root.get(CorporateSetting_.COMPANY_ID), companyId);
  }

  public static Specification<CorporateSetting> findByCompanyIdWhereDefault(
      Long companyId, boolean isDefault) {
    return (root, query, cb) ->
        cb.and(
            cb.equal(root.get(CorporateSetting_.COMPANY_ID), companyId),
            cb.equal(root.get(CorporateSetting_.IS_DEFAULT), isDefault));
  }

  public static Specification<CorporateSetting> findByCompanyIds(
      List<Long> companyIds, boolean isDefault) {
    return (root, query, cb) ->
        cb.and(
            cb.equal(
                root.get(CorporateSetting_.COMPANY_ID).in(companyIds),
                cb.equal(root.get(CorporateSetting_.IS_DEFAULT), isDefault)));
  }
}
