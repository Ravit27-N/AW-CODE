package com.tessi.cxm.pfl.ms5.service.specification;

import com.tessi.cxm.pfl.ms5.dto.LoadOrganization;
import com.tessi.cxm.pfl.ms5.entity.Client;
import com.tessi.cxm.pfl.ms5.entity.Client_;
import com.tessi.cxm.pfl.ms5.entity.Department;
import com.tessi.cxm.pfl.ms5.entity.Department_;
import com.tessi.cxm.pfl.ms5.entity.Division_;
import com.tessi.cxm.pfl.ms5.entity.UserEntity;
import com.tessi.cxm.pfl.ms5.entity.UserEntity_;
import com.tessi.cxm.pfl.shared.service.restclient.ModificationLevel;
import com.tessi.cxm.pfl.shared.utils.SpecificationUtils;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Selection;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.jpa.domain.Specification;

/**
 * {@link ClientSpecification} to filter data or record of {@link Client}.
 *
 * @author Sokhour LACH
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClientSpecification {

  public static Specification<Client> containName(String name) {
    return SpecificationUtils.getFilterString(Client_.name, name);
  }

  public static Specification<Client> containFilter(
      String filter) {
    if (StringUtils.isEmpty(filter)) {
      return null;
    }
    return Specification.where(
        Specification.where(ClientSpecification.containName(filter)));
  }

  /**
   * To filter record or data of {@link Client} by userId
   *
   * @param technicalRef refer to {@link UserEntity} technicalRef.
   */
  public static Specification<Client> byUserTechnicalRefAndDeletedFalse(String technicalRef) {
    return (root, query, criteriaBuilder) -> {
      Join<Client, UserEntity> join =
          root.join(Client_.DIVISIONS).join(Division_.DEPARTMENTS).join(Department_.USERS);

      List<Predicate> predicates = new ArrayList<>();
      predicates.add(criteriaBuilder.like(join.get(UserEntity_.TECHNICAL_REF), technicalRef));
      predicates.add(criteriaBuilder.isTrue(join.get(UserEntity_.IS_ACTIVE)));
      return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    };
  }

  public static Specification<Client> equalName(String name) {
    return SpecificationUtils.getEqualString(Client_.name, name);
  }

  public static Specification<Client> notEqualId(long id) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.notEqual(root.get(Client_.ID), id);
  }

  public static Specification<Client> equalId(long id) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Client_.ID), id);
  }

  public static Specification<Client> equalEmail(String email) {
    return SpecificationUtils.getEqualString(Client_.email, email);
  }

  public static Specification<Client> equalFileId(String fileId) {
    return SpecificationUtils.getEqualString(Client_.fileId, fileId);
  }

  public static Specification<Department> getOrganization(
      String level, LoadOrganization loadOrganization) {
    return (root, query, cb) -> {
      var joinDivision = root.join(Department_.DIVISION);
      var joinClient = joinDivision.join(Division_.CLIENT);

      switch (ModificationLevel.valuesByKey(level)) {
        case CLIENT:
          return cb.equal(joinClient.get(Client_.ID), loadOrganization.getClientId());
        case DIVISION:
          return cb.equal(joinDivision.get(Division_.ID), loadOrganization.getDivisionId());
        default:
          return cb.equal(root.get(Department_.ID), loadOrganization.getServiceId());
      }
    };
  }

  public static Specification<Department> selectOrganization() {
    return (root, query, criteriaBuilder) -> {
      var joinDivision = SpecificationUtils.getOrCreateJoin(root, Department_.DIVISION);
      var joinClient = SpecificationUtils.getOrCreateJoin(joinDivision, Division_.CLIENT);

      List<Selection<?>> selection = new ArrayList<>();
      selection.add(root.get(Department_.ID));
      selection.add(root.get(Department_.NAME));
      selection.add(joinDivision.get(Division_.ID));
      selection.add(joinDivision.get(Division_.NAME));
      selection.add(joinClient.get(Client_.ID));
      selection.add(joinClient.get(Client_.NAME));
      query.multiselect(selection);
      return null;
    };
  }

  public static Specification<Department> getOrganizationInfo(
      String level, LoadOrganization loadOrganization) {
    Specification<Department> querySpec = getOrganization(level, loadOrganization);
    return querySpec.and(ClientSpecification.selectOrganization());
  }
}
