package com.innovationandtrust.corporate.repository;

import com.innovationandtrust.corporate.model.entity.CompanyDetail;
import com.innovationandtrust.share.model.project.CorporateInfo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyDetailRepository
    extends JpaRepository<CompanyDetail, Long>, JpaSpecificationExecutor<CompanyDetail> {

  @Query(
      "select new com.innovationandtrust.share.model.project.CorporateInfo(cs.id, cd.companyId, cd.name, cs.mainColor, cs.secondaryColor, cs.linkColor, cs.logo, cd.companyUuid)"
          + " from CompanyDetail cd"
          + " inner join CorporateSetting cs on cs.companyId = cd.companyId"
          + " where cd.companyId = (select bu.companyDetail.companyId from BusinessUnit bu "
          + " inner join Employee e on bu.id = e.businessUnit.id where e.userId = :userId)")
  List<CorporateInfo> findCorporateInfo(@Param("userId") Long userId);
}
