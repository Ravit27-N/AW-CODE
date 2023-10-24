package com.innovationandtrust.project.repository;

import com.innovationandtrust.project.model.entity.ProjectDetail;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/** ProjectDetailRepository has findByTypeAndProjectId method. */
public interface ProjectDetailRepository
    extends JpaRepository<ProjectDetail, Long>, JpaSpecificationExecutor<ProjectDetail> {
  Optional<ProjectDetail> findByTypeAndProjectId(String type, Long projectId);
}
