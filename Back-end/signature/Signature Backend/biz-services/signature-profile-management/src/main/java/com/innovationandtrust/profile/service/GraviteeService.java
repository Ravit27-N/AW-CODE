package com.innovationandtrust.profile.service;

import com.innovationandtrust.profile.chain.execution.CreateAPIFacadeExecutionManager;
import com.innovationandtrust.profile.chain.execution.SubscriptionExecutionManager;
import com.innovationandtrust.profile.constant.GraviteeConstant;
import com.innovationandtrust.profile.model.entity.Company;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.gravitee.GraviteeProperty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class GraviteeService {
  private final CreateAPIFacadeExecutionManager createAPIFacadeExecutionManager;
  private final SubscriptionExecutionManager subscriptionExecutionManager;
  private final GraviteeProperty graviteeProperty;

  /**
   * Create API Facade.
   *
   * @param company refers to company that API facade belongs to.
   */
  public void createAPIFacade(Company company) {
    log.info("Start creating API Facade...");
    var context = new ExecutionContext();
    context.put(GraviteeConstant.COMPANY_UUID, company.getUuid());
    context.put(GraviteeConstant.COMPANY_NAME, company.getName());
    context.put(GraviteeConstant.GRAVITEE_PROPERTY_KEY, graviteeProperty);
    try {
      this.createAPIFacadeExecutionManager.execute(context);
      log.info("Creating API Facade successfully...");

      this.subscription(context.get(GraviteeConstant.API_ID, String.class));
    } catch (Exception e) {
      log.error("Failed to create API Facade...", e);
      throw new IllegalArgumentException("Failed to create API Facade...", e);
    }
  }

  private void subscription(String apiId) {
    log.info("Start subscription...");
    var context = new ExecutionContext();
    context.put(GraviteeConstant.API_ID, apiId);
    context.put(GraviteeConstant.GRAVITEE_PROPERTY_KEY, graviteeProperty);
    this.subscriptionExecutionManager.execute(context);
    log.info("Subscription successfully...");
  }
}
