package com.innovationandtrust.signature.identityverification.config;

import com.innovationandtrust.utils.exception.config.FeignErrorDecoder;
import com.innovationandtrust.utils.feignclient.FacadeUrlConfig;
import feign.codec.ErrorDecoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

/** Configuration class for WebMvc. */
@Configuration
public class WebMvcConfigurerExtender implements WebMvcConfigurer {

  @Value("${signature.file.base-path}")
  private String basePath;

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(new LocaleChangeInterceptor());
  }

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/dossier/**").allowedMethods("*");
    registry.addMapping("/shareid/**").allowedMethods("*");
  }

  @Override
  public void addResourceHandlers(final ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/v1/**").addResourceLocations("file:" + basePath + "/");
  }

  @Bean
  public ErrorDecoder errorDecoder() {
    return new FeignErrorDecoder();
  }

  @Bean
  @RequestScope
  public FacadeUrlConfig facadeUriConfig() {
    return new FacadeUrlConfig();
  }
}
