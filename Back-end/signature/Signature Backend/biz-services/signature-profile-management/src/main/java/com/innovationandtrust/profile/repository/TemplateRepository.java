package com.innovationandtrust.profile.repository;

import com.innovationandtrust.profile.model.entity.Template;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TemplateRepository extends JpaRepository<Template, Long>, JpaSpecificationExecutor<Template> {

    @Modifying
    @Query("UPDATE Template set businessUnitId = :businessUnitId where id in :templateIds")
    void updateBusinessId(@Param("templateIds")List<Long> templateIds, @Param("businessUnitId") Long businessUnitId);
}
