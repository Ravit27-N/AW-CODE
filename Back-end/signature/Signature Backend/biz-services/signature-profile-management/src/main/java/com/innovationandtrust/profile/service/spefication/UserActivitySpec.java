package com.innovationandtrust.profile.service.spefication;

import com.innovationandtrust.profile.model.entity.UserActivity;
import com.innovationandtrust.profile.model.entity.UserActivity_;
import java.util.Date;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserActivitySpec {
  public static Specification<UserActivity> greaterThanDate(Date date) {
    return (root, query, cb) -> cb.greaterThan(root.get(UserActivity_.EXPIRE_TIME), date);
  }
}
