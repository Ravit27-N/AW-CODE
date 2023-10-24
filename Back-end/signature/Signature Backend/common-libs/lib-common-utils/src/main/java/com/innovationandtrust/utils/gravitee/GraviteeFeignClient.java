package com.innovationandtrust.utils.gravitee;

import com.innovationandtrust.utils.feignclient.GraviteeFeignClientConfiguration;
import com.innovationandtrust.utils.gravitee.model.ApplicationResponse;
import com.innovationandtrust.utils.gravitee.model.DeploymentRequest;
import com.innovationandtrust.utils.gravitee.model.DuplicateRequestDto;
import com.innovationandtrust.utils.gravitee.model.GraviteeResponse;
import com.innovationandtrust.utils.gravitee.model.PlanResponse;
import com.innovationandtrust.utils.gravitee.model.PublishRequest;
import feign.Headers;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "gravitee",
    configuration = GraviteeFeignClientConfiguration.class,
    url = "${signature.gravitee.base-url}")
public interface GraviteeFeignClient {
  @PostMapping(
      value =
          "/management/organizations/{organization}/environments/{environment}/apis/{apiContextPath}/duplicate",
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @Headers({"Content-Type", "application/json"})
  GraviteeResponse duplicate(
      @PathVariable("organization") String organization,
      @PathVariable("environment") String environment,
      @PathVariable("apiContextPath") String apiContextPath,
      @RequestBody DuplicateRequestDto duplicateRequestDto);

  @PostMapping(
      value =
          "/management/organizations/{organization}/environments/{environment}/apis/{apiId}/deploy",
      consumes = MediaType.APPLICATION_JSON_VALUE)
  void deploy(
      @PathVariable("organization") String organization,
      @PathVariable("environment") String environment,
      @PathVariable("apiId") String apiId,
      @RequestBody DeploymentRequest deploymentRequest);

  @PostMapping("/management/organizations/{organization}/environments/{environment}/apis/{apiId}")
  GraviteeResponse start(
      @PathVariable("organization") String organization,
      @PathVariable("environment") String environment,
      @PathVariable("apiId") String apiId,
      @RequestParam("action") String action);

  @PutMapping(
      value = "/management/organizations/{organization}/environments/{environment}/apis/{apiId}",
      consumes = MediaType.APPLICATION_JSON_VALUE)
  void publishOrUnPublish(
      @PathVariable("organization") String organization,
      @PathVariable("environment") String environment,
      @PathVariable("apiId") String apiId,
      @RequestBody PublishRequest publishRequest);

  @GetMapping(
      "/management/organizations/{organization}/environments/{environment}/apis/{apiId}/plans")
  List<PlanResponse> getPlans(
      @PathVariable("organization") String organization,
      @PathVariable("environment") String environment,
      @PathVariable("apiId") String apiId,
      @RequestParam("status") String status);

  @GetMapping(
      "/management/organizations/{organization}/environments/{environment}/applications/_paged")
  ApplicationResponse getApplications(
      @PathVariable("organization") String organization,
      @PathVariable("environment") String environment,
      @RequestParam("status") String status,
      @RequestParam("page") int page,
      @RequestParam("size") int size,
      @RequestParam("exclude") String exclude);

  @PostMapping(
      value =
          "/management/organizations/{organization}/environments/{environment}/apis/{apiId}/subscriptions",
      consumes = MediaType.APPLICATION_JSON_VALUE)
  void subscribe(
      @PathVariable("organization") String organization,
      @PathVariable("environment") String environment,
      @PathVariable("apiId") String apiId,
      @RequestParam("application") String application,
      @RequestParam("plan") String plan);
}
