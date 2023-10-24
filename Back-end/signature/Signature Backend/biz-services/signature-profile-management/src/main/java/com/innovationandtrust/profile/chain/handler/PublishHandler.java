package com.innovationandtrust.profile.chain.handler;

import com.innovationandtrust.profile.constant.GraviteeConstant;
import com.innovationandtrust.utils.chain.ExecutionContext;
import com.innovationandtrust.utils.chain.ExecutionState;
import com.innovationandtrust.utils.chain.handler.AbstractExecutionHandler;
import com.innovationandtrust.utils.gravitee.GraviteeFeignClient;
import com.innovationandtrust.utils.gravitee.GraviteeProperty;
import com.innovationandtrust.utils.gravitee.model.GraviteeResponse;
import com.innovationandtrust.utils.gravitee.model.PublishRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PublishHandler extends AbstractExecutionHandler {
  private static final String GROUP_NAME = "default-group";
  private final GraviteeFeignClient graviteeFeignClient;
  private final ModelMapper modelMapper;

  @Override
  public ExecutionState execute(ExecutionContext context) {
    log.info("Publish API...");
    var gravitee = context.get(GraviteeConstant.GRAVITEE_RESPONSE_KEY, GraviteeResponse.class);
    var graviteeProperties =
        context.get(GraviteeConstant.GRAVITEE_PROPERTY_KEY, GraviteeProperty.class);
    var publishRequest = this.modelMapper.map(gravitee, PublishRequest.class);
    publishRequest.setVersion(graviteeProperties.getVersion());
    publishRequest.setVisibility("PUBLIC");
    publishRequest.setName(context.get(GraviteeConstant.COMPANY_NAME, String.class));
    publishRequest.setDescription(context.get(GraviteeConstant.COMPANY_NAME, String.class));
    publishRequest.setLifeCycleState("PUBLISHED");

    this.publish(
        context.get(GraviteeConstant.API_ID, String.class), publishRequest, graviteeProperties);
    log.info("Publish API facade successfully...");
    return ExecutionState.NEXT;
  }

  private void publish(
      String apiId, PublishRequest publishRequest, GraviteeProperty graviteeProperty) {
    try {
      this.graviteeFeignClient.publishOrUnPublish(
          graviteeProperty.getOrganizations(),
          graviteeProperty.getEnvironment(),
          apiId,
          publishRequest);
    } catch (Exception e) {
      log.error("Failed to publish API...[{}] [{}]", apiId, publishRequest, e);
      throw new IllegalArgumentException("Failed to publish API..." + e);
    }
  }
}
