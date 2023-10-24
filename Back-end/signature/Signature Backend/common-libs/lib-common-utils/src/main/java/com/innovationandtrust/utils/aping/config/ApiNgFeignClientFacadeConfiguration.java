package com.innovationandtrust.utils.aping.config;

import com.innovationandtrust.utils.aping.ApiNGProperty;
import com.innovationandtrust.utils.aping.constant.APIHeaderConstant;
import com.innovationandtrust.utils.feignclient.FeignClientFacadeConfiguration;
import com.innovationandtrust.utils.keycloak.provider.IKeycloakTokenExchange;
import feign.RequestInterceptor;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

@Slf4j
public class ApiNgFeignClientFacadeConfiguration extends FeignClientFacadeConfiguration {
  private final ApiNGProperty apiNgProperty;

  public ApiNgFeignClientFacadeConfiguration(
      @Autowired ApiNGProperty apiNGProperty, IKeycloakTokenExchange iKeycloakTokenExchange) {
    super(iKeycloakTokenExchange);
    this.apiNgProperty = apiNGProperty;
  }

  @Override
  @Bean
  @Primary
  public RequestInterceptor requestInterceptor() {
    return requestTemplate -> {
      log.info("Feign client request variables [{}]", requestTemplate.getRequestVariables());
      facadeUrl.facadeRequest(requestTemplate);
      requestTemplate.header(HttpHeaders.AUTHORIZATION, "Bearer " + this.getAccessToken());
      requestTemplate.header(APIHeaderConstant.CERTIGNA_USER, apiNgProperty.getCertignaUser());
      requestTemplate.header(
          APIHeaderConstant.CERTIGNA_ROLE,
          String.valueOf(Integer.parseInt(apiNgProperty.getCertignaRole())));
      requestTemplate.header(APIHeaderConstant.CERTIGNA_HASH, apiNgProperty.getCertignaHash());
      requestTemplate.header(
          APIHeaderConstant.DEFAULT_LANGUAGE, apiNgProperty.getDefaultLanguage());
      log.info("Feint client Request headers [{}]", requestTemplate.headers());
      log.info(
          "Feign client start calling url [{}] date [{}]",
          requestTemplate.feignTarget().url(),
          new Date());
    };
  }

  @Bean
  public Encoder multipartFormEncoder() {
    return new SpringFormEncoder(
        new SpringEncoder(
            () -> new HttpMessageConverters(new RestTemplate().getMessageConverters())));
  }
}
