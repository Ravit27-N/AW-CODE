package com.innovationandtrust.utils.aping.config;

import com.innovationandtrust.utils.aping.feignclient.ApiNgFeignClientFacade;
import com.innovationandtrust.utils.aping.provider.ApiNgProvider;
import org.springframework.context.annotation.Bean;

public class ApiNgServiceProviderConfigurer {

  @Bean
  public ApiNgProvider apiNgProvider(final ApiNgFeignClientFacade apiNgFeignClient) {
    return new ApiNgProvider(apiNgFeignClient);
  }
}
