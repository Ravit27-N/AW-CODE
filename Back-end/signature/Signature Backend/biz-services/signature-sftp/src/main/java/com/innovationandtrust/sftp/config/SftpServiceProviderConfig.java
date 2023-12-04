package com.innovationandtrust.sftp.config;

import com.innovationandtrust.utils.sftpgo.SFTPGoProperty;
import com.innovationandtrust.utils.sftpgo.provider.SFTPGoServiceProvider;
import com.innovationandtrust.utils.sftpgo.restclient.SFTPGoFeignClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(value = {SFTPGoProperty.class})
@EnableFeignClients(clients = SFTPGoFeignClient.class)
public class SftpServiceProviderConfig extends SFTPGoServiceProvider {

  public SftpServiceProviderConfig(
      SFTPGoFeignClient sftpGoFeignClient, SFTPGoProperty sftpGoProperty) {
    super(sftpGoFeignClient, sftpGoProperty);
  }
}
