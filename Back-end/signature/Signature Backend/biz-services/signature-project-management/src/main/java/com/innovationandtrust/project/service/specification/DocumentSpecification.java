package com.innovationandtrust.project.service.specification;

import com.innovationandtrust.project.model.entity.Document;
import com.innovationandtrust.project.model.entity.Document_;
import com.innovationandtrust.project.model.entity.Project_;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

/** DocumentSpecification use for query, or filter from Document table. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DocumentSpecification {
  public static Specification<Document> findAllByProjectId(Long projectId) {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.equal(root.get(Document_.PROJECT).get(Project_.ID), projectId);
  }
}
