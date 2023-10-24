package com.innovationandtrust.process.config;

import com.innovationandtrust.utils.tinyurl.TinyUrlFeignClient;
import com.innovationandtrust.utils.tinyurl.TinyUrlProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(value = {TinyUrlProperty.class})
@EnableFeignClients(clients = {TinyUrlFeignClient.class})
public class TinyUrlFeignClientConfig {}
