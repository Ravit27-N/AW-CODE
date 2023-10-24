package com.innovationandtrust.process;

import com.innovationandtrust.utils.encryption.ImpersonateTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;

@Slf4j
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class ProcessControlApplication {

  public static void main(String[] args) {
    SpringApplication.run(ProcessControlApplication.class, args);
  }

  @Bean
  public ImpersonateTokenService impersonateToken(
      @Value(value = "${signature.impersonate-token.secrete-key}") String secretKey) {
    return new ImpersonateTokenService(secretKey);
  }
}
