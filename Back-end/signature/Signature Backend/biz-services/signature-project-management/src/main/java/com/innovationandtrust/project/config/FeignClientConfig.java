package com.innovationandtrust.project.config;

import com.innovationandtrust.project.restclient.ProcessControlFeignClient;
import com.innovationandtrust.project.restclient.ProfileFeignClient;
import com.innovationandtrust.utils.aping.ApiNGProperty;
import com.innovationandtrust.utils.aping.config.ApiNgServiceProviderConfigurer;
import com.innovationandtrust.utils.aping.feignclient.ApiNgFeignClientFacade;
import com.innovationandtrust.utils.corporateprofile.feignclient.CorporateProfileFeignClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(
    clients = {
      ApiNgFeignClientFacade.class,
      ProcessControlFeignClient.class,
      CorporateProfileFeignClient.class,
      ProfileFeignClient.class
    })
@EnableConfigurationProperties(value = {ApiNGProperty.class})
public class FeignClientConfig extends ApiNgServiceProviderConfigurer {}
