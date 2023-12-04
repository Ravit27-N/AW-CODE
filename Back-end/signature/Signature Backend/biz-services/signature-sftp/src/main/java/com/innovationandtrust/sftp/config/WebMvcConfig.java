package com.innovationandtrust.sftp.config;

import com.innovationandtrust.configuration.webmvc.CommonWebMvcConfigurer;
import com.innovationandtrust.utils.exception.config.FeignErrorDecoder;
import com.innovationandtrust.utils.feignclient.FacadeUrlConfig;
import com.innovationandtrust.utils.pdf.provider.PdfProvider;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebMvcConfig extends CommonWebMvcConfigurer {
  
  @Bean
  public FacadeUrlConfig facadeUriConfig() {
    return new FacadeUrlConfig();
  }

  @Bean
  public ErrorDecoder errorDecoder() {
    return new FeignErrorDecoder();
  }

  @Bean
  public PdfProvider pdfProvider() {
    return new PdfProvider();
  }
}
