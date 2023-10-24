package com.innovationandtrust.profile.chain.handler;

import com.innovationandtrust.profile.constant.GraviteeConstant;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.chain.ExecutionState;
import com.innovationandtrust.utils.chain.handler.AbstractExecutionHandler;
import com.innovationandtrust.utils.gravitee.GraviteeFeignClient;
import com.innovationandtrust.utils.gravitee.GraviteeProperty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class StartHandler extends AbstractExecutionHandler {
  private final GraviteeFeignClient graviteeFeignClient;

  @Override
  public ExecutionState execute(ExecutionContext context) {
    log.info("Start API...");
    this.start(
        context.get(GraviteeConstant.API_ID, String.class),
        context.get(GraviteeConstant.GRAVITEE_PROPERTY_KEY, GraviteeProperty.class));
    log.info("Start API successfully...[{}]", context.get(GraviteeConstant.API_ID, String.class));
    return ExecutionState.NEXT;
  }

  public void start(String apiId, GraviteeProperty graviteeProperty) {
    try {
      this.graviteeFeignClient.start(
          graviteeProperty.getOrganizations(), graviteeProperty.getEnvironment(), apiId, "START");
    } catch (Exception e) {
      log.error("Failed to start API...[{}]" , apiId, e);
      throw new IllegalArgumentException("Failed to start API..." + e);
    }
  }
}
