package com.tessi.cxm.pfl.ms11.util;

import com.tessi.cxm.pfl.ms11.exception.CustomerNotFoundException;
import com.tessi.cxm.pfl.shared.auth.AuthenticationUtils;
import com.tessi.cxm.pfl.shared.exception.UserAccessDeniedExceptionHandler;
import com.tessi.cxm.pfl.shared.service.restclient.ProfileFeignClient;
import com.tessi.cxm.pfl.shared.utils.BearerAuthentication;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class SettingPrivilegeUtil {

  @Setter private static ProfileFeignClient profileFeignClient;

  /**
   * Method used to validate authorization token is admin && client exist in system. It will throw
   * error when one of both condition is true.
   *
   * @param customer - name of customer {@link String}.
   */
  public static void validateAdminRequest(String customer) {
    final var refBearerToken =
        BearerAuthentication.PREFIX_TOKEN.concat(AuthenticationUtils.getAuthToken());
    if (!profileFeignClient.checkUserIsAdmin(refBearerToken).isAdmin()) {
      var userClientName = profileFeignClient.getUserDetail(refBearerToken).getClientName();
      if (!userClientName.equalsIgnoreCase(customer)) {
        throw new UserAccessDeniedExceptionHandler();
      }
    }
    if (!profileFeignClient.isClientExist(customer, 0L, refBearerToken)) {
      throw new CustomerNotFoundException(customer);
    }
  }

  public static void validateClientExist(String customer) {
    final var refBearerToken =
        BearerAuthentication.PREFIX_TOKEN.concat(AuthenticationUtils.getAuthToken());
    if (!profileFeignClient.isClientExist(customer, 0L, refBearerToken)) {
      throw new CustomerNotFoundException(customer);
    }
  }
}
