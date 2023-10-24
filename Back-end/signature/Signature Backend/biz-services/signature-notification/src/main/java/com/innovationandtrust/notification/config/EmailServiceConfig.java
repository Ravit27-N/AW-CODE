package com.innovationandtrust.notification.config;

import com.innovationandtrust.utils.mail.config.MailSmtpConfigurer;
import com.innovationandtrust.utils.mail.config.MailSmtpProperty;
import java.nio.charset.StandardCharsets;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;

@Configuration
@EnableConfigurationProperties(value = {MailSmtpProperty.class})
public class EmailServiceConfig extends MailSmtpConfigurer {

  public EmailServiceConfig(MailSmtpProperty smtpProperty) {
    super(smtpProperty);
  }

  @Bean
  public SpringTemplateEngine springTemplateEngine() {
    SpringTemplateEngine templateEngine = new SpringTemplateEngine();
    templateEngine.addTemplateResolver(htmlTemplateResolver());
    return templateEngine;
  }

  @Bean
  public SpringResourceTemplateResolver htmlTemplateResolver() {
    SpringResourceTemplateResolver emailTemplateResolver = new SpringResourceTemplateResolver();
    emailTemplateResolver.setPrefix("classpath:/templates/");
    emailTemplateResolver.setSuffix(".html");
    emailTemplateResolver.setTemplateMode(TemplateMode.HTML);
    emailTemplateResolver.setCharacterEncoding(StandardCharsets.UTF_8.name());
    return emailTemplateResolver;
  }
}
