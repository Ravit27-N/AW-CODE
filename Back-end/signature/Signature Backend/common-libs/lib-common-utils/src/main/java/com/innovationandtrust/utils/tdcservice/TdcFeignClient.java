package com.innovationandtrust.utils.tdcservice;

import com.innovationandtrust.utils.feignclient.TdcFeignClientConfiguration;
import com.innovationandtrust.utils.tdcservice.model.TdcResponse;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(
    name = "tdc-service",
    configuration = TdcFeignClientConfiguration.class,
    url = "${signature.tdc-service.url}"
)
public interface TdcFeignClient {

  @PostMapping(value = "/ws/rest/store", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Headers({"Content-Type","multipart/form-data"})
  TdcResponse uploadDocument(
      @RequestPart("inputStream") MultipartFile inputStream,
      @RequestPart("document") MultipartFile jsonFile);
  
}
