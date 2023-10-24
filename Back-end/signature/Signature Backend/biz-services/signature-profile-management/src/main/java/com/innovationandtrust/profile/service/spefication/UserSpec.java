package com.innovationandtrust.profile.service.spefication;

import com.innovationandtrust.profile.model.entity.Role_;
import com.innovationandtrust.profile.model.entity.User;
import com.innovationandtrust.profile.model.entity.User_;
import com.innovationandtrust.share.utils.SpecUtils;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserSpec {

  public static Specification<User> findByEmail(String email) {
    return (root, query, cb) -> cb.equal(root.get(User_.EMAIL), email.trim());
  }

  public static Specification<User> findByExistMail(Long userId, String email) {
    return (root, query, cb) ->
        cb.and(
            cb.equal(root.get(User_.EMAIL), email.trim()), cb.notEqual(root.get(User_.ID), userId));
  }

  public static Specification<User> findByPhone(String phone) {
    return (root, query, cb) -> cb.equal(root.get(User_.PHONE), phone);
  }

  public static Specification<User> findByExistPhone(Long userId, String phone) {
    return (root, query, cb) ->
        cb.and(cb.equal(root.get(User_.PHONE), phone), cb.notEqual(root.get(User_.ID), userId));
  }

  public static Specification<User> search(String search) {
    if (!StringUtils.hasText(search)) {
      return null;
    }
    return (root, query, cb) ->
        cb.or(
            cb.like(cb.lower(root.get(User_.FIRST_NAME)), SpecUtils.likeQuery(search)),
            cb.like(cb.lower(root.get(User_.LAST_NAME)), SpecUtils.likeQuery(search)),
            cb.like(cb.lower(root.get(User_.EMAIL)), SpecUtils.likeQuery(search)),
            cb.like(
                cb.lower(cb.concat(root.get(User_.FIRST_NAME), root.get(User_.LAST_NAME))),
                SpecUtils.likeQuery(search)),
            cb.like(
                cb.lower(cb.concat(root.get(User_.LAST_NAME), root.get(User_.FIRST_NAME))),
                SpecUtils.likeQuery(search)));
  }

  public static Specification<User> findUserByCompanyId(Long companyId) {
    return (root, query, cb) -> cb.equal(root.get(User_.COMPANY_ID), companyId);
  }

  public static Specification<User> findUserByListOfCompanyIds(List<Long> companyIds) {
    return (root, query, cb) ->
        cb.and(root.get(User_.COMPANY_ID).in(companyIds), cb.equal(root.get(User_.DELETED), false));
  }

  public static Specification<User> findUserByDeleted(Boolean deleted) {
    return (root, query, cb) -> cb.equal(root.get(User_.DELETED), deleted);
  }

  public static Specification<User> findByRole(String name) {
    return (root, query, cb) -> cb.equal(root.get(User_.ROLES).get(Role_.NAME), name);
  }

  public static Specification<User> findByRole(Long companyId, String roleName) {
    return (root, query, cb) ->
        cb.and(
            cb.equal(root.get(User_.COMPANY_ID), companyId),
            cb.equal(root.get(User_.ROLES).get(Role_.NAME), roleName),
            cb.isTrue(root.get(User_.ACTIVE)),
            cb.isFalse(root.get(User_.DELETED)));
  }
}
