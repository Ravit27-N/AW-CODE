package com.innovationandtrust.project.repository;

import com.innovationandtrust.project.model.entity.Signatory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/** SignatoryRepository. */
public interface SignatoryRepository
    extends JpaRepository<Signatory, Long>, JpaSpecificationExecutor<Signatory> {}
