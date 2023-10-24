package com.innovationandtrust.project.service.specification;

import com.innovationandtrust.project.model.entity.ProjectDetail;
import com.innovationandtrust.project.model.entity.ProjectDetail_;
import com.innovationandtrust.project.model.entity.Project_;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

/** ProjectDetailSpecification use for query, or filter from project detail table. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProjectDetailSpecification {
  /**
   * Find project detail by project id and type.
   *
   * @param projectId refers to project's id
   * @param type refers to type of project detail
   * @return ProjectDetail
   */
  public static Specification<ProjectDetail> findByProjectIdAndType(Long projectId, String type) {
    return (root, query, cb) ->
        cb.and(
            cb.equal(root.get(ProjectDetail_.PROJECT).get(Project_.ID), projectId),
            cb.equal(root.get(ProjectDetail_.TYPE), type));
  }
}
