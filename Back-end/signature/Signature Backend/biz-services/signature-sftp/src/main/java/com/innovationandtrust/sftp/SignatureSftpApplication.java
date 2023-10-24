package com.innovationandtrust.sftp;

import com.innovationandtrust.sftp.restclient.ProfileFeignClient;
import com.innovationandtrust.utils.feignclient.FeignClientProperty;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.quartz.QuartzAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableConfigurationProperties(value = {FeignClientProperty.class})
@EnableFeignClients(clients = {ProfileFeignClient.class})
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, QuartzAutoConfiguration.class})
public class SignatureSftpApplication {

  public static void main(String[] args) {
    SpringApplication.run(SignatureSftpApplication.class, args);
  }
}
