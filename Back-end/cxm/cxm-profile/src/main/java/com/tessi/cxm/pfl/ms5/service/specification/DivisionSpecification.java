package com.tessi.cxm.pfl.ms5.service.specification;

import com.tessi.cxm.pfl.ms5.entity.Client_;
import com.tessi.cxm.pfl.ms5.entity.Division;
import com.tessi.cxm.pfl.ms5.entity.Division_;
import com.tessi.cxm.pfl.shared.utils.SpecificationUtils;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DivisionSpecification {

  public static Specification<Division> containName(String name) {
    return SpecificationUtils.getFilterString(Division_.name, name);
  }

  public static Specification<Division> equalName(String name) {
    return SpecificationUtils.getEqualString(Division_.name, name);
  }

  public static Specification<Division> notEqualId(long id) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.notEqual(root.get(Division_.ID), id);
  }

  public static Specification<Division> equalId(long id) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Division_.ID), id);
  }

  public static Specification<Division> equalClientId(long id) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Division_.CLIENT).get(
        Client_.ID), id);
  }

  public static Specification<Division> idIn(List<Long> ids) {
    return (root, query, criteriaBuilder) -> root.get(Division_.ID).in(ids);
  }
}
