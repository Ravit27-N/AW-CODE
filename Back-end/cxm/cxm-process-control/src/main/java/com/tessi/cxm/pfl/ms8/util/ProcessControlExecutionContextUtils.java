package com.tessi.cxm.pfl.ms8.util;

import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionContext;
import com.tessi.cxm.pfl.shared.utils.BearerAuthentication;
import org.apache.commons.lang3.StringUtils;

/** Contains utilities method for accessing {@code ExecutionContext} in ProcessControl. */
public class ProcessControlExecutionContextUtils {

  private ProcessControlExecutionContextUtils() {}

  /**
   * Get Jwt token with bearer prefixed from {@code ExecutionContext}.
   *
   * @param context Execution context.
   * @return Bearer prefixed token or null if it is missing from {@code ExecutionContext}.
   */
  public static String getBearerToken(ExecutionContext context) {
    var bearerToken = context.get(FlowTreatmentConstants.BEARER_TOKEN, String.class);
    if (StringUtils.isNotBlank(bearerToken)) {
      bearerToken = BearerAuthentication.PREFIX_TOKEN.concat(bearerToken);
    }
    return bearerToken;
  }

  public static String getBearerTokenWithPrefix(String token) {
    return BearerAuthentication.PREFIX_TOKEN.concat(token);
  }
}
