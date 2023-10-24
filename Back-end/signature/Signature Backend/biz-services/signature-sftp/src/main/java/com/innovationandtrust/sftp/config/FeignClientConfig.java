package com.innovationandtrust.sftp.config;

import com.innovationandtrust.sftp.restclient.NotificationFeignClient;
import com.innovationandtrust.sftp.restclient.ProfileFeignClient;
import com.innovationandtrust.sftp.restclient.ProjectFeignClient;
import com.innovationandtrust.utils.feignclient.FeignClientProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(value = {FeignClientProperty.class})
@EnableFeignClients(clients = {ProfileFeignClient.class, NotificationFeignClient.class, ProjectFeignClient.class})
public class FeignClientConfig {}
