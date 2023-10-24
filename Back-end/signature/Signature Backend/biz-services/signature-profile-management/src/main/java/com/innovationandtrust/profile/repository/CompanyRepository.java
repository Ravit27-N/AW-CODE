package com.innovationandtrust.profile.repository;

import com.innovationandtrust.profile.model.entity.Company;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository
    extends JpaRepository<Company, Long>, JpaSpecificationExecutor<Company> {
  <T> Optional<T> findCompanyById(@Param("id") Long id, Class<T> type);
}
