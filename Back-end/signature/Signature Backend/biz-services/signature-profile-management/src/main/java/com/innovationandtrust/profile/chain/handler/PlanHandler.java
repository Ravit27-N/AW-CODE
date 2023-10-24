package com.innovationandtrust.profile.chain.handler;

import com.innovationandtrust.profile.constant.GraviteeConstant;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.chain.ExecutionState;
import com.innovationandtrust.utils.chain.handler.AbstractExecutionHandler;
import com.innovationandtrust.utils.gravitee.GraviteeFeignClient;
import com.innovationandtrust.utils.gravitee.GraviteeProperty;
import com.innovationandtrust.utils.gravitee.model.PlanResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PlanHandler extends AbstractExecutionHandler {
  private static final String SECURITY = "OAUTH2";
  private final GraviteeFeignClient graviteeFeignClient;

  @Override
  public ExecutionState execute(ExecutionContext context) {
    log.info("Get plan start...");
    List<PlanResponse> planResponses =
        this.getPlans(
            context.get(GraviteeConstant.API_ID, String.class),
            context.get(GraviteeConstant.GRAVITEE_PROPERTY_KEY, GraviteeProperty.class));
    context.put(GraviteeConstant.PLAN_ID, planResponses.get(0).getId());
    if (log.isDebugEnabled()) {
      log.info("Get plan successfully...[{}]", planResponses);
    }
    var planIds = planResponses.stream().map(PlanResponse::getId).toList();
    log.info("Get plan successfully...[{}]", planIds);
    return ExecutionState.NEXT;
  }

  private List<PlanResponse> getPlans(String apiId, GraviteeProperty graviteeProperty) {
    try {
      var plans =
          this.graviteeFeignClient.getPlans(
              graviteeProperty.getOrganizations(),
              graviteeProperty.getEnvironment(),
              apiId,
              "published");
      return plans.stream()
          .filter(planResponse -> SECURITY.equals(planResponse.getSecurity()))
          .toList();
    } catch (Exception e) {
      log.error("Failed to get plans...[{}]", apiId, e);
      throw new IllegalArgumentException("Failed to get plans..." + e);
    }
  }
}
