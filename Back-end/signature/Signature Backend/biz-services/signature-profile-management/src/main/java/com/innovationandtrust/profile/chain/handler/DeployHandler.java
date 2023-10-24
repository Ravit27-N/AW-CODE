package com.innovationandtrust.profile.chain.handler;

import com.innovationandtrust.profile.constant.GraviteeConstant;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.chain.ExecutionState;
import com.innovationandtrust.utils.chain.handler.AbstractExecutionHandler;
import com.innovationandtrust.utils.gravitee.GraviteeFeignClient;
import com.innovationandtrust.utils.gravitee.GraviteeProperty;
import com.innovationandtrust.utils.gravitee.model.DeploymentRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeployHandler extends AbstractExecutionHandler {
  private final GraviteeFeignClient graviteeFeignClient;

  @Override
  public ExecutionState execute(ExecutionContext context) {
    log.info("Deploy start...");
    this.deploy(
        context.get(GraviteeConstant.API_ID, String.class),
        context.get(GraviteeConstant.GRAVITEE_PROPERTY_KEY, GraviteeProperty.class));
    log.info("Deploy api facade successfully...");
    return ExecutionState.END;
  }

  private void deploy(String apiId, GraviteeProperty graviteeProperty) {
    try {
      this.graviteeFeignClient.deploy(
          graviteeProperty.getOrganizations(),
          graviteeProperty.getEnvironment(),
          apiId,
          new DeploymentRequest("deployment label"));
    } catch (Exception e) {
      log.error("Failed to deploy...[{}]", apiId, e);
      throw new IllegalArgumentException("Failed to deploy..." + e);
    }
  }
}
