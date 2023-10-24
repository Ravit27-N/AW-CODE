package com.innovationandtrust.process.restclient;

import com.innovationandtrust.process.model.DocumentInfo;
import com.innovationandtrust.process.model.ProjectDto;
import com.innovationandtrust.share.model.project.ProjectAfterSignRequest;
import com.innovationandtrust.share.model.project.ProjectUpdateRequest;
import com.innovationandtrust.utils.feignclient.FeignClientFacadeConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
    name = "project",
    url = "${signature.feign-client.clients.project-url}",
    path = "${signature.feign-client.contexts.project-context-path}",
    configuration = FeignClientFacadeConfiguration.class)
public interface ProjectFeignClient {

  @GetMapping("/v1/documents/download")
  Resource downloadDocument(@RequestParam("path") String path);

  @PutMapping("/v1/projects/signed")
  void updateProjectAfterSigned(@RequestBody ProjectAfterSignRequest request);

  @GetMapping("/documents/{id}")
  DocumentInfo getDocument(@PathVariable("id") Long id);

  @PutMapping("/v1/projects/complete/{id}")
  void completeProjectWithStatus(
      @PathVariable("id") Long id, @RequestParam("status") String status);

  @PutMapping("/v1/projects/refused")
  void updateProjectAfterRefused(@RequestBody ProjectUpdateRequest request);

  @PutMapping("/v1/projects/expired")
  void updateProjectStatusExpired(@RequestParam("id") Long id);

  @PutMapping("/v1/projects/read")
  void updateProjectStatusRead(@RequestBody ProjectUpdateRequest request);

  @PutMapping("/v1/projects/uuid/{id}/{flowId}")
  void insertProjectUuid(@PathVariable("id") Long id, @PathVariable("flowId") String flowId);

  @PutMapping("/v1/projects/urgent")
  void updateProjectUrgent(@RequestParam("id") Long id);

  @GetMapping("/v1/projects/{id}")
  ProjectDto findById(@PathVariable("id") Long id);
}
