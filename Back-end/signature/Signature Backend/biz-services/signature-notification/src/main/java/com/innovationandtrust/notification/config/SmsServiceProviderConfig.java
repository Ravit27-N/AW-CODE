package com.innovationandtrust.notification.config;

import com.innovationandtrust.utils.sms.SMSProperty;
import com.innovationandtrust.utils.sms.config.SmsServiceProviderConfigurer;
import com.innovationandtrust.utils.sms.restclient.SmsFeignClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(clients = {SmsFeignClient.class})
@EnableConfigurationProperties(value = {SMSProperty.class})
public class SmsServiceProviderConfig extends SmsServiceProviderConfigurer {
  public SmsServiceProviderConfig(final SMSProperty smsProperty) {
    super(smsProperty);
  }
}
