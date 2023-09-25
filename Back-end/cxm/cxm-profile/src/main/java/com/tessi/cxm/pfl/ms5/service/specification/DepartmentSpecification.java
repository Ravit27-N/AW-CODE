package com.tessi.cxm.pfl.ms5.service.specification;

import com.tessi.cxm.pfl.ms5.entity.Client;
import com.tessi.cxm.pfl.ms5.entity.Client_;
import com.tessi.cxm.pfl.ms5.entity.Department;
import com.tessi.cxm.pfl.ms5.entity.Department_;
import com.tessi.cxm.pfl.ms5.entity.Division;
import com.tessi.cxm.pfl.ms5.entity.Division_;
import javax.persistence.criteria.Join;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DepartmentSpecification {

  public static Specification<Department> clientEqual(Long clientId) {
    return (root, query, cb) -> {
      Join<Department, Client> join = root
          .join(Department_.DIVISION)
          .join(Division_.CLIENT);
      return cb.equal(join.get(Client_.ID), clientId);
    };
  }

  public static Specification<Department> divisionEqual(Long divisionId) {
    return (root, query, cb) -> {
      Join<Department, Division> join = root
          .join(Department_.DIVISION);
      return cb.equal(join.get(Division_.ID), divisionId);
    };
  }
}
