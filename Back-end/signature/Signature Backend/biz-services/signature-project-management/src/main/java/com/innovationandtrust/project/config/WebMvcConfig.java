package com.innovationandtrust.project.config;

import com.innovationandtrust.configuration.webmvc.CommonWebMvcConfigurer;
import com.innovationandtrust.share.model.SettingProperties;
import com.innovationandtrust.utils.exception.config.FeignErrorDecoder;
import com.innovationandtrust.utils.feignclient.FacadeUrlConfig;
import com.innovationandtrust.utils.feignclient.FeignClientProperty;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@EnableConfigurationProperties(value = {FeignClientProperty.class, SettingProperties.class})
public class WebMvcConfig extends CommonWebMvcConfigurer {
  @Bean
  public FacadeUrlConfig facadeUriConfig() {
    return new FacadeUrlConfig();
  }

  @Bean
  public ErrorDecoder errorDecoder() {
    return new FeignErrorDecoder();
  }
}
