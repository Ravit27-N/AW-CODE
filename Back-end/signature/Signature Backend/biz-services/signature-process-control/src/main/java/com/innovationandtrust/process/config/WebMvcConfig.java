package com.innovationandtrust.process.config;

import com.innovationandtrust.configuration.webmvc.CommonWebMvcConfigurer;
import com.innovationandtrust.share.model.SettingProperties;
import com.innovationandtrust.utils.exception.config.FeignErrorDecoder;
import com.innovationandtrust.utils.feignclient.FacadeUrlConfig;
import com.innovationandtrust.utils.feignclient.FeignClientProperty;
import feign.codec.ErrorDecoder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

@Configuration
@EnableConfigurationProperties(
    value = {FeignClientProperty.class, ProcessControlProperty.class, SettingProperties.class})
public class WebMvcConfig extends CommonWebMvcConfigurer {
  private final String[] methods = {"HEAD", "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"};

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/sign-info/**");
    registry.addMapping("/documents/**");
    registry.addMapping("/otp/**");
    registry.addMapping("/approval/**");
    registry.addMapping("/recipients/**");
    registry.addMapping("/project/**");
    registry.addMapping("/sign/**").allowedMethods(methods);
  }

  @Bean
  public FacadeUrlConfig facadeUriConfig() {
    return new FacadeUrlConfig();
  }

  @Bean
  public ErrorDecoder errorDecoder() {
    return new FeignErrorDecoder();
  }
}
