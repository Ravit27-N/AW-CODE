package com.innovationandtrust.process.restclient;

import com.innovationandtrust.utils.feignclient.FeignClientMultipartConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

/** FeignClient to request to Signature-sftp. */
@FeignClient(
    value = "signature-sftp",
    url = "${signature.feign-client.clients.sftp-url}",
    path = "${signature.feign-client.contexts.sftp-context-path}",
    configuration = FeignClientMultipartConfiguration.class)
public interface SftpFeignClient {

  @PostMapping(
      value = "/v1/sign/insert/signed-files",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  Void insertSignedDocument(
      @RequestParam("corporateUuid") String corporateUuid,
      @RequestParam("zipFile") String zipFile,
      @RequestPart("signedFiles") MultipartFile[] signedFiles,
      @RequestPart("manifestFile") MultipartFile manifestFile);
}
