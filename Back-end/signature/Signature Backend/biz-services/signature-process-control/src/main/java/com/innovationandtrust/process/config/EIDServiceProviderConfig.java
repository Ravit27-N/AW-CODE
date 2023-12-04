package com.innovationandtrust.process.config;

import com.innovationandtrust.utils.eid.EIDProperty;
import com.innovationandtrust.utils.eid.provider.EIDServiceProvider;
import com.innovationandtrust.utils.eid.restclient.EIDFeignClient;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(value = {EIDProperty.class})
@EnableFeignClients(clients = EIDFeignClient.class)
public class EIDServiceProviderConfig extends EIDServiceProvider {

  public EIDServiceProviderConfig(EIDFeignClient eidFeignClient, @NotNull EIDProperty eIDProperty) {
    super(eidFeignClient, eIDProperty);
  }
}
