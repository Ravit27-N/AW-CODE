package com.innovationandtrust.profile.service.spefication;

import com.innovationandtrust.profile.model.entity.Role;
import com.innovationandtrust.profile.model.entity.Role_;
import java.util.Set;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RoleSpec {

  public static Specification<Role> findByKeycloakRole(String id) {
    return (root, query, cb) -> cb.equal(root.get(Role_.KEYCLOAK_ROLE_ID), id);
  }

  public static Specification<Role> findByNames(Set<String> names) {
    return (root, query, cb) -> root.get(Role_.NAME).in(names);
  }
}
