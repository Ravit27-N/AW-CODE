package com.innovationandtrust.profile.chain.execution;

import com.innovationandtrust.profile.chain.handler.DeployHandler;
import com.innovationandtrust.profile.chain.handler.GetApplicationsHandler;
import com.innovationandtrust.profile.chain.handler.PlanHandler;
import com.innovationandtrust.profile.chain.handler.SubscribeHandler;
import com.innovationandtrust.utils.chain.ExecutionManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubscriptionExecutionManager extends ExecutionManager {
  private final PlanHandler planHandler;
  private final SubscribeHandler subscribeHandler;
  private final GetApplicationsHandler getApplicationsHandler;
  private final DeployHandler deployHandler;

  @Override
  public void afterPropertiesSet() {
    super.addHandlers(List.of(planHandler, getApplicationsHandler, subscribeHandler, deployHandler));
  }
}
