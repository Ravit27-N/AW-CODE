package com.innovationandtrust.profile.chain.handler;

import com.innovationandtrust.profile.constant.GraviteeConstant;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.chain.ExecutionState;
import com.innovationandtrust.utils.chain.handler.AbstractExecutionHandler;
import com.innovationandtrust.utils.gravitee.GraviteeFeignClient;
import com.innovationandtrust.utils.gravitee.GraviteeProperty;
import com.innovationandtrust.utils.gravitee.model.DuplicateRequestDto;
import com.innovationandtrust.utils.gravitee.model.GraviteeResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DuplicateHandler extends AbstractExecutionHandler {
  private final GraviteeFeignClient graviteeFeignClient;

  @Override
  public ExecutionState execute(ExecutionContext context) {
    log.info("Duplicate start...");
    GraviteeResponse response =
        this.duplicate(
            context.get(GraviteeConstant.COMPANY_UUID, String.class),
            context.get(GraviteeConstant.GRAVITEE_PROPERTY_KEY, GraviteeProperty.class));
    context.put(GraviteeConstant.API_ID, response.getId());
    context.put(GraviteeConstant.GRAVITEE_RESPONSE_KEY, response);
      if (log.isDebugEnabled()) {
          log.debug("Duplicate api facade successfully...[{}]", response);
      }
    log.info("Duplicate api facade successfully...[{}]",response.getId());
    return ExecutionState.NEXT;
  }

  private GraviteeResponse duplicate(String companyUuid, GraviteeProperty graviteeProperty) {
    try {
      return this.graviteeFeignClient.duplicate(
          graviteeProperty.getOrganizations(),
          graviteeProperty.getEnvironment(),
          graviteeProperty.getApiContextPath(),
          new DuplicateRequestDto("/" + companyUuid, graviteeProperty.getVersion()));
    } catch (Exception e) {
      log.error("Failed to duplicate...[{}]", companyUuid, e);
      throw new IllegalArgumentException("Failed to duplicate..." + e);
    }
  }
}
