package com.innovationandtrust.profile.config;

import com.innovationandtrust.utils.gravitee.GraviteeFeignClient;
import com.innovationandtrust.utils.gravitee.GraviteeProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(value = {GraviteeProperty.class})
@EnableFeignClients(clients = {GraviteeFeignClient.class})
public class GraviteeFeignClientConfig {}
