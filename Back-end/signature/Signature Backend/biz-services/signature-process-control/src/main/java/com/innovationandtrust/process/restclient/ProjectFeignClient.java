package com.innovationandtrust.process.restclient;

import com.innovationandtrust.process.model.DocumentInfo;
import com.innovationandtrust.process.model.ProjectDto;
import com.innovationandtrust.share.model.project.ProjectAfterSignRequest;
import com.innovationandtrust.share.model.project.ProjectUpdateRequest;
import com.innovationandtrust.share.model.project.SignatoryRequest;
import com.innovationandtrust.share.model.project.SignatoryResponse;
import com.innovationandtrust.utils.feignclient.FeignClientFacadeConfiguration;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

  @GetMapping("/v1/projects/{id}/external")
  ProjectDto findExternalById(@PathVariable("id") Long id);

  @PostMapping("/v1/projects/get-multiple")
  List<ProjectDto> findByIds(@RequestBody List<Long> ids);

  @GetMapping("/v1/signatories/{id}/video-id")
  SignatoryResponse findSignatoryByVideoId(@PathVariable("id") String id);

  @PutMapping("/v1/signatories/{id}/video-id")
  void updateVideoId(@PathVariable("id") Long projectId, @RequestParam("videoId") String videoId);

  @PutMapping("/v1/signatories/{projectId}/status")
  void updateStatus(
      @PathVariable("projectId") Long projectId, @RequestBody List<SignatoryRequest> requests);

  /**
   * @param id refers to participant id
   * @param status refers to status of document to update
   */
  @PutMapping("/v1/signatories/{id}/doc-status")
  void updateDocumentStatus(@PathVariable("id") Long id, @RequestParam("status") String status);

  /**
   * @param ids refers to list of participant id
   * @param status refers to status of document to update
   */
  @PutMapping("/v1/signatories/doc-status")
  void updateDocumentsStatus(@RequestBody List<Long> ids, @RequestParam("status") String status);

  @PostMapping("/v1/signatories/in-signing")
  void updateInSigningStatus(@RequestBody List<String> uuids);
}
