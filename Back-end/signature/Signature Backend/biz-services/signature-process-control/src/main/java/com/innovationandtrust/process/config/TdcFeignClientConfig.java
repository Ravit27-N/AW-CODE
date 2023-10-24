package com.innovationandtrust.process.config;

import com.innovationandtrust.utils.tdcservice.TdcFeignClient;
import com.innovationandtrust.utils.tdcservice.TdcProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(value = {TdcProperty.class})
@EnableFeignClients(clients = {TdcFeignClient.class})
public class TdcFeignClientConfig {

}
