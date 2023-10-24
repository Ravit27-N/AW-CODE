package com.innovationandtrust.corporate.config;

import com.innovationandtrust.configuration.webmvc.CommonWebMvcConfigurer;
import com.innovationandtrust.corporate.service.restclient.ProfileFeignClient;
import com.innovationandtrust.corporate.service.restclient.ProjectFeignClient;
import com.innovationandtrust.utils.exception.config.FeignErrorDecoder;
import com.innovationandtrust.utils.feignclient.FacadeUrlConfig;
import com.innovationandtrust.utils.feignclient.FeignClientProperty;
import feign.codec.ErrorDecoder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

@Configuration
@EnableFeignClients(clients = {ProfileFeignClient.class, ProjectFeignClient.class})
@EnableConfigurationProperties(value = {FeignClientProperty.class})
public class WebMvcConfig extends CommonWebMvcConfigurer {
  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/corporate-settings/**");
  }

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
