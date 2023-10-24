package com.innovationandtrust.corporate.repository;

import com.innovationandtrust.corporate.model.entity.CompanySetting;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/** Company setting repository. */
public interface CompanySettingRepository
    extends JpaRepository<CompanySetting, Long>, JpaSpecificationExecutor<CompanySetting> {
  List<CompanySetting> findCompanySettingsByCompanyUuid(String companyUuid);
}
