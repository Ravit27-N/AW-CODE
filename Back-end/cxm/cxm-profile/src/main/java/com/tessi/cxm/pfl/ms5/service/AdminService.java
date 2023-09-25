package com.tessi.cxm.pfl.ms5.service;

import com.tessi.cxm.pfl.ms5.exception.UserNotFoundException;
import com.tessi.cxm.pfl.ms5.repository.UserRepository;
import com.tessi.cxm.pfl.shared.auth.AuthenticationUtils;

/** Admin service related operations. */
public interface AdminService {

  /**
   * Implemented service must provide
   *
   * @return Admin's user id.
   */
  String getConfiguredUserAdminId();

  UserRepository getUserRepository();

  /**
   * Check if the current invoking user has admin power or not. User could be Super admin or
   * Platform admin
   *
   * @return True if admin, otherwise false.
   */
  default boolean isAdmin() {
    return isSuperAdmin() || isPlatformAdmin();
  }

  /**
   * @return True if super admin, otherwise false.
   */
  default boolean isSuperAdmin() {
    return this.getConfiguredUserAdminId().equals(AuthenticationUtils.getPrincipalIdentifier());
  }

  /**
   * @return True if user admin platform, otherwise false.
   */
  default boolean isPlatformAdmin() {
    return isUserAdministrator(AuthenticationUtils.getPrincipalIdentifier());
  }

  default boolean isUserAdministrator(String technicalRef) {
    var user =
        this.getUserRepository()
            .findByTechnicalRefAndIsActiveTrue(technicalRef)
            .orElseThrow(() -> new UserNotFoundException(technicalRef));
    return user.isAdmin();
  }
}
