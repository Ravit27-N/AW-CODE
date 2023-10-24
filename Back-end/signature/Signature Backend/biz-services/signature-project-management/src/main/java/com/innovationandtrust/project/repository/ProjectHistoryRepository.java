package com.innovationandtrust.project.repository;

import com.innovationandtrust.project.model.entity.ProjectHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/** ProjectHistory has findMaxSortOrder method. */
@Repository
public interface ProjectHistoryRepository
    extends JpaRepository<ProjectHistory, Long>, JpaSpecificationExecutor<ProjectHistory> {

  @Query("select max(ph.sortOrder) from ProjectHistory ph where ph.project.id = :projectId")
  Long findMaxSortOrder(@Param("projectId") Long projectId);
}
