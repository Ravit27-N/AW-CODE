package com.innovationandtrust.utils.aping.provider;

import com.innovationandtrust.utils.aping.feignclient.ApiNgFeignClientFacade;
import com.innovationandtrust.utils.aping.model.ResponseData;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@Slf4j
@RequiredArgsConstructor
public class ApiNgProvider {

  private final ApiNgFeignClientFacade apiNgFeignClient;

  public ResponseData uploadFile(URI uri, String name, String filename, byte[] content) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_PDF);

    ContentDisposition contentDisposition =
        ContentDisposition.builder("form-data").name(name).filename(filename).build();
    headers.setContentDisposition(contentDisposition);

    return this.apiNgFeignClient.uploadDocument(content, headers);
  }
}
