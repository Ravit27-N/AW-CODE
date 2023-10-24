package com.innovationandtrust.profile.chain.handler;

import com.innovationandtrust.profile.constant.GraviteeConstant;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.chain.ExecutionState;
import com.innovationandtrust.utils.chain.handler.AbstractExecutionHandler;
import com.innovationandtrust.utils.gravitee.GraviteeFeignClient;
import com.innovationandtrust.utils.gravitee.GraviteeProperty;
import com.innovationandtrust.utils.gravitee.model.ApplicationResponseList;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SubscribeHandler extends AbstractExecutionHandler {
  private final GraviteeFeignClient graviteeFeignClient;

  @Override
  public ExecutionState execute(ExecutionContext context) {
    log.info("Start subscribe...");
    String planId = context.get(GraviteeConstant.PLAN_ID, String.class);
    List<ApplicationResponseList> applicationResponseLists =
        context.get(GraviteeConstant.LIST_APPLICATION_RESPONSE, ArrayList.class);

    this.subscribe(
        applicationResponseLists,
        planId,
        context.get(GraviteeConstant.API_ID, String.class),
        context.get(GraviteeConstant.GRAVITEE_PROPERTY_KEY, GraviteeProperty.class));
    log.info("Subscribe successfully...");
    return ExecutionState.NEXT;
  }

  private void subscribe(
      List<ApplicationResponseList> applicationResponseLists,
      String plan,
      String apiId,
      GraviteeProperty property) {
    try {
      applicationResponseLists.forEach(
          application ->
              this.graviteeFeignClient.subscribe(
                  property.getOrganizations(),
                  property.getEnvironment(),
                  apiId,
                  application.getId(),
                  plan));
    } catch (Exception e) {
      log.error("Failed to subscribe: [{}] [{}]", plan, apiId, e);
      throw new IllegalArgumentException("Failed to subscribe: " + e);
    }
  }
}
