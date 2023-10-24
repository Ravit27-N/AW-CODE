package com.innovationandtrust.profile.service.restclient;

import com.innovationandtrust.utils.feignclient.FeignClientFacadeConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    value = "project",
    url = "${signature.feign-client.clients.project-url}",
    path = "${signature.feign-client.contexts.project-context-path}",
    configuration = FeignClientFacadeConfiguration.class)
public interface ProjectFeignClient {

  @PutMapping("/v1/projects/assign/{id}")
  void assignProjects(@PathVariable("id") Long creator, @RequestParam Long assignTo);
}
