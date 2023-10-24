package com.innovationandtrust.utils.mail.config;

import com.innovationandtrust.utils.mail.provider.MailServiceProvider;
import jakarta.validation.Valid;
import java.util.Objects;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

public class MailSmtpConfigurer extends JavaMailSenderImpl {

  private static final Integer SSL_PORT = 465;

  public MailSmtpConfigurer(@Valid final MailSmtpProperty smtpProperty) {
    this.setHost(smtpProperty.getHost());
    this.setPort(smtpProperty.getPort());
    this.setUsername(smtpProperty.getUsername());
    this.setPassword(smtpProperty.getPassword());
    this.setMailProperties(this.getJavaMailProperties(), smtpProperty);
  }

  private void setMailProperties(final Properties props, MailSmtpProperty smtpProperty) {
    props.put("mail.transport.protocol", "smtp");
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", smtpProperty.isEnableTls());
    props.put("mail.debug", "true");
    props.put("mail.smtp.connectiontimeout", 1500);
    props.put("mail.mime.charset", "UTF-8");
    if (Objects.equals(SSL_PORT, smtpProperty.getPort())) {
      props.put("mail.smtp.ssl.enable", "true");
      props.put("mail.smtp.socketFactory.port", smtpProperty.getPort()); // SSL Port
      props.put("mail.smtp.ssl.checkserveridentity", "false");
      props.put(
          "mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory"); // SSL Factory Class
    }
  }

  @Bean
  public MailServiceProvider mailServiceProvider(
      final JavaMailSender mailSender, final MailSmtpProperty smtpProperty) {
    return smtpProperty.isValidSender()
        ? new MailServiceProvider(mailSender, smtpProperty.getSender())
        : new MailServiceProvider(mailSender, "");
  }
}
