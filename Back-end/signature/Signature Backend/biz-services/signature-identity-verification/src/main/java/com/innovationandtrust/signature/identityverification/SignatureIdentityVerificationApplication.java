package com.innovationandtrust.signature.identityverification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.quartz.QuartzAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/** Main class for the application. */
@SpringBootApplication(exclude = {QuartzAutoConfiguration.class})
@EnableJpaAuditing
public class SignatureIdentityVerificationApplication {

  public static void main(String[] args) {
    SpringApplication.run(SignatureIdentityVerificationApplication.class, args);
  }
}
