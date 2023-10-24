package com.innovationandtrust.process.config;

import com.innovationandtrust.process.restclient.ProfileFeignClient;
import com.innovationandtrust.process.restclient.ProjectFeignClient;
import com.innovationandtrust.process.restclient.SignatoryFeignClient;
import com.innovationandtrust.utils.aping.ApiNGProperty;
import com.innovationandtrust.utils.aping.config.ApiNgServiceProviderConfigurer;
import com.innovationandtrust.utils.aping.feignclient.ApiNgFeignClientFacade;
import com.innovationandtrust.utils.corporateprofile.feignclient.CorporateProfileFeignClient;
import com.innovationandtrust.utils.notification.feignclient.NotificationFeignClient;
import com.innovationandtrust.utils.signatureidentityverification.feignclient.SignatureIdentityVerificationFeignClient;
import com.innovationandtrust.utils.tinyurl.TinyUrlProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(value = {ApiNGProperty.class, TinyUrlProperty.class})
@EnableFeignClients(
    clients = {
      ApiNgFeignClientFacade.class,
      ProjectFeignClient.class,
      ProfileFeignClient.class,
      SignatoryFeignClient.class,
      CorporateProfileFeignClient.class,
      NotificationFeignClient.class,
      SignatureIdentityVerificationFeignClient.class
    })
public class FeignClientConfig extends ApiNgServiceProviderConfigurer {}
