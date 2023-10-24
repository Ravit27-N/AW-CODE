package com.innovationandtrust.project.service.specification;

import com.innovationandtrust.project.model.entity.ProjectHistory;
import com.innovationandtrust.project.model.entity.ProjectHistory_;
import com.innovationandtrust.project.model.entity.Project_;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

/** ProjectHistorySpecification use for query, or filter from project history table. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ProjectHistorySpecification {

  public static Specification<ProjectHistory> findTheLast(Long projectId) {
    return (root, query, cb) -> cb.equal(root.get(ProjectHistory_.PROJECT).get(Project_.ID), projectId);
  }
}
