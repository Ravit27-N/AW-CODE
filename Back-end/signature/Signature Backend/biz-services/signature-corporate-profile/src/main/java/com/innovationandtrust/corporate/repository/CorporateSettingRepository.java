package com.innovationandtrust.corporate.repository;

import com.innovationandtrust.corporate.model.entity.CorporateSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CorporateSettingRepository
    extends JpaRepository<CorporateSetting, Long>, JpaSpecificationExecutor<CorporateSetting> {}
