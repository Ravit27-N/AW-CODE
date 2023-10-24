package com.innovationandtrust.profile.service.spefication;

import com.innovationandtrust.profile.model.entity.Template_;
import com.innovationandtrust.profile.model.entity.UserTemplates;
import com.innovationandtrust.profile.model.entity.UserTemplates_;
import com.innovationandtrust.profile.model.entity.User_;
import com.innovationandtrust.share.utils.SpecUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserTemplatesSpec {
  public static Specification<UserTemplates> findByUserId(Long userId) {
    return ((root, query, criteriaBuilder) ->
        criteriaBuilder.equal(root.get(UserTemplates_.USER).get(User_.ID), userId));
  }

  public static Specification<UserTemplates> findByTemplateName(String name) {
    return ((root, query, criteriaBuilder) ->
            criteriaBuilder.like(root.get(UserTemplates_.TEMPLATE).get(Template_.NAME), SpecUtils.likeQuery(name)));
  }
}
