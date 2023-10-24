package com.innovationandtrust.profile.repository;

import com.innovationandtrust.profile.model.entity.CompanySetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/** Company setting repository. */
public interface CompanySettingRepository
    extends JpaRepository<CompanySetting, Long>, JpaSpecificationExecutor<CompanySetting> {}
