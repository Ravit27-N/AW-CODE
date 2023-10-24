package com.innovationandtrust.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/** Manages notifications service such as email, sms. */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class SignatureNotificationApplication {

  public static void main(String[] args) {
    SpringApplication.run(SignatureNotificationApplication.class, args);
  }
}
