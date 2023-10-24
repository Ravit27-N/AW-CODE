package com.innovationandtrust.notification.config;

import com.innovationandtrust.utils.corporateprofile.feignclient.CorporateProfileFeignClient;
import com.innovationandtrust.utils.tinyurl.TinyUrlProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(value = {TinyUrlProperty.class})
@EnableFeignClients(clients = {CorporateProfileFeignClient.class})
public class FeignClientConfig {}
