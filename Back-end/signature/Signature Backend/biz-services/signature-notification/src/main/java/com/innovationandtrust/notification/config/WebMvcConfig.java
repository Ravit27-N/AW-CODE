package com.innovationandtrust.notification.config;

import com.innovationandtrust.configuration.webmvc.CommonWebMvcConfigurer;
import com.innovationandtrust.utils.exception.config.FeignErrorDecoder;
import com.innovationandtrust.utils.feignclient.FacadeUrlConfig;
import com.innovationandtrust.utils.feignclient.FeignClientProperty;
import feign.codec.ErrorDecoder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.RequestScope;

@Configuration
@EnableConfigurationProperties(value = {FeignClientProperty.class})
public class WebMvcConfig extends CommonWebMvcConfigurer {
  @Bean
  @RequestScope
  public FacadeUrlConfig facadeUriConfig() {
    return new FacadeUrlConfig();
  }

  @Bean
  public ErrorDecoder errorDecoder() {
    return new FeignErrorDecoder();
  }
}
