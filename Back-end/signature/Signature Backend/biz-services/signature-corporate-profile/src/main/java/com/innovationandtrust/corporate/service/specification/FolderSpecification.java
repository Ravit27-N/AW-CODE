package com.innovationandtrust.corporate.service.specification;

import com.innovationandtrust.corporate.model.entity.AbstractEntity_;
import com.innovationandtrust.corporate.model.entity.Folder;
import com.innovationandtrust.corporate.model.entity.Folder_;
import com.innovationandtrust.share.utils.SpecUtils;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FolderSpecification {
  public static Specification<Folder> findByCreatedBy(List<Long> createdBy) {
    return (root, query, cb) -> root.get(AbstractEntity_.CREATED_BY).in(createdBy);
  }

  public static Specification<Folder> search(String search) {
    if (!StringUtils.hasText(search)) {
      return null;
    }
    return (root, query, cb) ->
        cb.like(cb.lower(root.get(Folder_.UNIT_NAME)), SpecUtils.likeQuery(search));
  }
}
