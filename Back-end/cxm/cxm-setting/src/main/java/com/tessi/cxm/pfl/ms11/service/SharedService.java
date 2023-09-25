package com.tessi.cxm.pfl.ms11.service;

import com.tessi.cxm.pfl.shared.auth.AuthenticationUtils;
import com.tessi.cxm.pfl.shared.utils.BearerAuthentication;

/** A class with all reusable methods for service. */
public interface SharedService {

  /**
   * Retrieve current Authorized token
   *
   * @return Bearer prefixed authorized token
   */
  default String getAuthorizedToken() {
    return BearerAuthentication.PREFIX_TOKEN.concat(AuthenticationUtils.getAuthToken());
  }
}
