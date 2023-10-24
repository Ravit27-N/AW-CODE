package com.innovationandtrust.utils.feignclient;

import com.innovationandtrust.utils.tinyurl.TinyUrlProperty;
import feign.Logger;
import feign.RequestInterceptor;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import lombok.RequiredArgsConstructor;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
public class TinyUrlFeignClientConfiguration {
  private final TinyUrlProperty tinyUrlProperty;

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
    return requestTemplate -> {
      requestTemplate.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
      requestTemplate.header(HttpHeaders.AUTHORIZATION, "Bearer " + tinyUrlProperty.getToken());
      requestTemplate.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
    };
  }
}
