package com.innovationandtrust.utils.feignclient;

import com.innovationandtrust.utils.keycloak.provider.IKeycloakTokenExchange;
import feign.RequestInterceptor;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;

@Slf4j
public class FeignClientMultipartConfiguration extends FeignClientFacadeConfiguration {
  public FeignClientMultipartConfiguration(IKeycloakTokenExchange iKeycloakTokenExchange) {
    super(iKeycloakTokenExchange);
  }

  @Override
  @Bean
  @Primary
  public RequestInterceptor requestInterceptor() {
    return requestTemplate -> {
      log.info("Feign client request variables [{}]", requestTemplate.getRequestVariables());
      requestTemplate.header(HttpHeaders.AUTHORIZATION, "Bearer " + this.getAccessToken());
      requestTemplate.header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
      facadeUrl.facadeRequest(requestTemplate);
      log.info("Feint client Request headers [{}]", requestTemplate.headers());
      log.info(
          "Feign client start calling url [{}] date [{}]",
          requestTemplate.feignTarget().url(),
          new Date());
    };
  }
}
