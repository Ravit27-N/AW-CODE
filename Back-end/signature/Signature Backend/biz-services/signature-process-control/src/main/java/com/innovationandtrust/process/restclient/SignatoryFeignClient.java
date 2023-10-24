package com.innovationandtrust.process.restclient;

import com.innovationandtrust.share.model.project.SignatoryRequest;
import com.innovationandtrust.utils.feignclient.FeignClientFacadeConfiguration;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    name = "signatory",
    url = "${signature.feign-client.clients.project-url}",
    path = "${signature.feign-client.contexts.project-context-path}",
    configuration = FeignClientFacadeConfiguration.class)
public interface SignatoryFeignClient {

  @PutMapping("/v1/signatories/{projectId}/status")
  void updateStatus(
      @PathVariable("projectId") Long projectId, @RequestBody List<SignatoryRequest> requests);
}
