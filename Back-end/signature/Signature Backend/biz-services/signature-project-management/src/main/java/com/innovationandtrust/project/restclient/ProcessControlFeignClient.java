package com.innovationandtrust.project.restclient;

import com.innovationandtrust.share.model.project.Project;
import com.innovationandtrust.utils.feignclient.FeignClientFacadeConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/** ProcessControlFeignClient use to communicate with process control service. */
@FeignClient(
    value = "process-control",
    url = "${signature.feign-client.clients.process-control-url}",
    path = "${signature.feign-client.contexts.process-control-context-path}",
    configuration = FeignClientFacadeConfiguration.class)
public interface ProcessControlFeignClient {
  @PostMapping("/v1/process-controls/project/send")
  void requestSign(@Validated @RequestBody Project project);

  @PostMapping("/v1/process-controls/project/update/{flowId}")
  void updateExpireDate(@PathVariable("flowId") String flowId, @RequestParam String expireDate);

  @PutMapping("/v1/project/cancel/{flowId}")
  void cancelProject(@PathVariable("flowId") String flowId);

  @GetMapping("/v1/process-controls/is-finished/{flowId}")
  Boolean isProjectFinished(@PathVariable("flowId") String flowId);
}
