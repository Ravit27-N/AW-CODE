package com.innovationandtrust.utils.feignclient;

import com.innovationandtrust.utils.authenticationUtils.AuthenticationUtils;
import com.innovationandtrust.utils.keycloak.provider.IKeycloakTokenExchange;
import feign.Logger;
import feign.Logger.Level;
import feign.RequestInterceptor;
import feign.Retryer;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;

@Slf4j
public class FeignClientConfiguration extends AuthFeignConfig {
  public FeignClientConfiguration(
      IKeycloakTokenExchange iKeycloakTokenExchange) {
    super(iKeycloakTokenExchange);
  }

  @Bean
  public Logger.Level feignLoggerLevel() {
    return Level.FULL;
  }

  @Bean
  public CloseableHttpClient feignClient() {
    return HttpClients.createDefault();
  }

  @Bean
  public RequestInterceptor requestInterceptor() {
    return requestTemplate -> {
      requestTemplate.header(HttpHeaders.AUTHORIZATION, "Bearer " + this.getAccessToken());
      requestTemplate.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
      requestTemplate.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
      requestTemplate.header(
          AuthenticationUtils.X_GRAVITEE_TRANSACTION, AuthenticationUtils.getGraviteeTransaction());
    };
  }

  @Bean
  public Retryer retryer() {
    return new Retryer.Default(100L, TimeUnit.SECONDS.toMillis(10L), 3);
  }
}
