package com.innovationandtrust.corporate.repository;

import com.innovationandtrust.corporate.model.entity.TemplateDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TemplateDetailRepository extends JpaRepository<TemplateDetail, Long> {}
