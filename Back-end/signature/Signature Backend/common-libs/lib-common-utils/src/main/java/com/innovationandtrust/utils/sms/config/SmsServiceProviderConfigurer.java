package com.innovationandtrust.utils.sms.config;

import com.innovationandtrust.utils.sms.SMSProperty;
import com.innovationandtrust.utils.sms.SmsServiceProvider;
import com.innovationandtrust.utils.sms.restclient.SmsFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

@Slf4j
public class SmsServiceProviderConfigurer {

  private final SMSProperty smsProperty;

  public SmsServiceProviderConfigurer(@Autowired SMSProperty smsProperty) {
    this.smsProperty = smsProperty;
    if (smsProperty.isEnable()) {
      log.info("SMS service provider is enable.");
    } else {
      log.warn(
          "SMS service provider is disable. "
              + "If you want to enable, please value of property"
              + " 'signature.sms-service.is-enable' to true");
    }
  }

  @Bean
  @ConditionalOnProperty(prefix = "signature.sms-service", name = "is-enable", havingValue = "true")
  public SmsServiceProvider smsServiceProvider(final SmsFeignClient smsFeignClient) {
    return new SmsServiceProvider(smsFeignClient, smsProperty);
  }
}
