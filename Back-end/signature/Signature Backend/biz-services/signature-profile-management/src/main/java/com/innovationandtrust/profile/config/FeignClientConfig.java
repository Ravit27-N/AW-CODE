package com.innovationandtrust.profile.config;

import com.innovationandtrust.profile.service.restclient.BusinessUnitsFeignClient;
import com.innovationandtrust.profile.service.restclient.CompanyDetailFeignClient;
import com.innovationandtrust.profile.service.restclient.EmployeeFeignClient;
import com.innovationandtrust.profile.service.restclient.ProjectFeignClient;
import com.innovationandtrust.profile.service.restclient.SftpFeignClient;
import com.innovationandtrust.utils.aping.ApiNGProperty;
import com.innovationandtrust.utils.aping.config.ApiNgServiceProviderConfigurer;
import com.innovationandtrust.utils.aping.feignclient.ApiNgFeignClientFacade;
import com.innovationandtrust.utils.corporateprofile.feignclient.CorporateProfileFeignClient;
import com.innovationandtrust.utils.notification.feignclient.NotificationFeignClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(
    clients = {
      ApiNgFeignClientFacade.class,
      BusinessUnitsFeignClient.class,
      CorporateProfileFeignClient.class,
      ProjectFeignClient.class,
      SftpFeignClient.class,
      NotificationFeignClient.class,
      CompanyDetailFeignClient.class,
      EmployeeFeignClient.class,
    })
@EnableConfigurationProperties(value = {ApiNGProperty.class})
public class FeignClientConfig extends ApiNgServiceProviderConfigurer {}
