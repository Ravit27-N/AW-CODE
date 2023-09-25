package com.tessi.cxm.pfl.ms5.service;

import com.tessi.cxm.pfl.ms5.dto.LoadOrganization;
import com.tessi.cxm.pfl.ms5.exception.NotRegisteredServiceUserException;
import com.tessi.cxm.pfl.ms5.exception.UserNotFoundException;
import com.tessi.cxm.pfl.ms5.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * A mixins interface for share functionalities.
 */
public interface SharedService {

  //region Required implementation
  UserRepository getUserRepository();

  // endregion

  //region Shared functionalities

  /**
   * Get user's organization include service, division and client by user's id.
   *
   * @return {@code LoadOrganization}
   */
  @Transactional(readOnly = true)
  default LoadOrganization getCurrentUserOrganization(String userId) {
    var invokingUser = getUserRepository().findByTechnicalRefAndIsActiveTrue(userId);
    if (invokingUser.isPresent()) {
      var userOrganization = getUserRepository().loadOrganizationUser(userId);
      return userOrganization.orElseThrow(
          () -> new NotRegisteredServiceUserException(userId));
    }
    throw new UserNotFoundException(userId);
  }

  // endregion
}
