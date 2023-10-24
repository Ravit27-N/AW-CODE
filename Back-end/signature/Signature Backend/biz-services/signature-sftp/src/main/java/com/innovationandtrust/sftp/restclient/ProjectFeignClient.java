package com.innovationandtrust.sftp.restclient;

import com.innovationandtrust.share.model.sftp.ProjectModel;
import com.innovationandtrust.utils.feignclient.InternalFeignClientConfiguration;
import com.innovationandtrust.utils.file.model.FileResponse;
import feign.Headers;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(
    value = "signature-profile",
    url = "${signature.feign-client.clients.project-url}",
    path = "${signature.feign-client.contexts.project-context-path}",
    configuration = InternalFeignClientConfiguration.class)
public interface ProjectFeignClient {
  @PostMapping(value = "/v1/project-xml")
  Boolean createProjectXML(
      @RequestBody @Valid ProjectModel projectModel,
      @RequestHeader(HttpHeaders.AUTHORIZATION) String token);

  @PostMapping(
      value = "/v1/project-xml/upload-document",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Headers({"Content-Type", "multipart/form-data"})
  List<FileResponse> uploadDocument(
      @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
      @RequestPart("files") List<MultipartFile> files,
      @RequestParam(value = "dirs", defaultValue = "") String... dirs);
}
