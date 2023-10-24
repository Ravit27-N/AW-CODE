package com.innovationandtrust.profile.chain.handler;

import com.innovationandtrust.profile.constant.GraviteeConstant;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.chain.ExecutionState;
import com.innovationandtrust.utils.chain.handler.AbstractExecutionHandler;
import com.innovationandtrust.utils.gravitee.GraviteeFeignClient;
import com.innovationandtrust.utils.gravitee.GraviteeProperty;
import com.innovationandtrust.utils.gravitee.model.ApplicationResponse;
import com.innovationandtrust.utils.gravitee.model.ApplicationResponseList;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class GetApplicationsHandler extends AbstractExecutionHandler {
  private final GraviteeFeignClient graviteeFeignClient;

  @Override
  public ExecutionState execute(ExecutionContext context) {
    log.info("Get applications start...");
    List<ApplicationResponseList> applicationResponses =
        this.getApplications(
            context.get(GraviteeConstant.GRAVITEE_PROPERTY_KEY, GraviteeProperty.class));
    context.put(GraviteeConstant.LIST_APPLICATION_RESPONSE, applicationResponses);
    if (log.isDebugEnabled()) {
      log.debug("Get applications successfully...[{}]", applicationResponses);
    }
    List<String> applicationIds = applicationResponses.stream().map(ApplicationResponseList::getId).toList();
    log.info("Get applications successfully...[{}]", applicationIds);
    return ExecutionState.NEXT;
  }

  private List<ApplicationResponseList> getApplications(GraviteeProperty graviteeProperty) {
    try {
      ApplicationResponse applicationResponses =
          this.graviteeFeignClient.getApplications(
              graviteeProperty.getOrganizations(),
              graviteeProperty.getEnvironment(),
              graviteeProperty.getStatus(),
              graviteeProperty.getPage(),
              graviteeProperty.getPageSize(),
              graviteeProperty.getExclude());

      List<ApplicationResponseList> applicationResponseList = new ArrayList<>();

      for (ApplicationResponseList applicationResponse : applicationResponses.getData()) {
        if (graviteeProperty.getApplicationIds().contains(applicationResponse.getId())) {
          applicationResponseList.add(applicationResponse);
        }
      }

      return applicationResponseList;
    } catch (Exception e) {
      log.error("Failed to get applications: ", e);
      throw new IllegalArgumentException("Failed to get application: " + e);
    }
  }
}
