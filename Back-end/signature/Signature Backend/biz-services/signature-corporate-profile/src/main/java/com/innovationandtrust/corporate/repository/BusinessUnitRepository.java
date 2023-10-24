package com.innovationandtrust.corporate.repository;

import com.innovationandtrust.corporate.model.entity.BusinessUnit;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BusinessUnitRepository
    extends JpaRepository<BusinessUnit, Long>, JpaSpecificationExecutor<BusinessUnit> {
  List<BusinessUnit> findBusinessUnitByParentId(@Param("parentId") long parentId);
}
