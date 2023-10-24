package com.innovationandtrust.utils.chain.handler;

import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.chain.ExecutionState;
import org.springframework.util.StringUtils;

public abstract class AbstractExecutionHandler {
  /**
   * Execute a specific task with the {@code ExecutionContext} supplied.
   *
   * <p>Provided context may be used to get all needed states before chain or put all the changed
   * state for the next execution.
   *
   * @param context Current execution context which holds all the state from previous execution and
   *     for storing all the current state changed.
   */
  public abstract ExecutionState execute(ExecutionContext context);

  /**
   * To get authorization token from {@link ExecutionContext context}.
   *
   * @param context refers to an object of {{@link ExecutionContext}}
   * @param tokenKey refers to an authorization token key in context
   * @return the token as string
   */
  protected String getToken(ExecutionContext context, String tokenKey) {
    var bearerToken = context.get(tokenKey, String.class);
    if (StringUtils.hasText(bearerToken)) {
      bearerToken = BearerAuthentication.PREFIX_TOKEN.concat(bearerToken);
    }
    return bearerToken;
  }
}
