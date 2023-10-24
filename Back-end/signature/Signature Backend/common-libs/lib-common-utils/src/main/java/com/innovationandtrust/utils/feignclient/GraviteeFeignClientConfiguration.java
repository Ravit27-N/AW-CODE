package com.innovationandtrust.utils.feignclient;

import com.innovationandtrust.utils.gravitee.GraviteeProperty;
import feign.Logger;
import feign.RequestInterceptor;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
public class GraviteeFeignClientConfiguration {
  private final GraviteeProperty graviteeProperty;

  @Bean
  public Encoder feignFormEncoder() {
    return new SpringFormEncoder(
        new SpringEncoder(
            () -> new HttpMessageConverters(new RestTemplate().getMessageConverters())));
  }

  @Bean
  public Logger.Level feignLoggerLevel() {
    return Logger.Level.FULL;
  }

  @Bean
  public CloseableHttpClient feignClient() {
    return HttpClients.createDefault();
  }

  @Bean
  public RequestInterceptor requestInterceptor() {
    String credential =
        String.format("%s:%s", graviteeProperty.getUsername(), graviteeProperty.getPassword());
    byte[] base64credentialBytes = Base64.encodeBase64(credential.getBytes());
    String base64credential = new String(base64credentialBytes);
    return requestTemplate -> {
      requestTemplate.header(HttpHeaders.AUTHORIZATION, "Basic " + base64credential);
      requestTemplate.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
      requestTemplate.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    };
  }
}
