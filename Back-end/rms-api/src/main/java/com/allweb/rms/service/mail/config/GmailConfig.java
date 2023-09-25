package com.allweb.rms.service.mail.config;

import static com.allweb.rms.utils.SystemConfigurationConstants.MAIL_PROTOCOL;
import static com.allweb.rms.utils.SystemConfigurationConstants.MAIL_SERVER;
import static com.allweb.rms.utils.SystemConfigurationConstants.PASSWORD;
import static com.allweb.rms.utils.SystemConfigurationConstants.PORT;
import static com.allweb.rms.utils.SystemConfigurationConstants.USERNAME;

import com.allweb.rms.repository.jpa.SystemConfigurationRepository;
import java.util.Properties;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class GmailConfig extends AbstractConfig {

  private final JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

  /*
   *
   * Gmail Config
   *
   * */
  protected GmailConfig(
      SystemConfigurationRepository systemMailConfigurationRepository, ModelMapper modelMapper) {
    super(systemMailConfigurationRepository, modelMapper);
  }

  /** Method use to Config JavaMailSender */
  public JavaMailSenderImpl javaMailSender() {
    int port = 587;
    if ((config().get(PORT.getValue()) != null))
      port = Integer.parseInt(config().get(PORT.getValue()));
    mailSender.setHost(config().get(MAIL_SERVER.getValue()));
    mailSender.setUsername(config().get(USERNAME.getValue()));
    mailSender.setPassword(config().get(PASSWORD.getValue()));
    mailSender.setPort(port);
    mailSender.setProtocol(config().get(MAIL_PROTOCOL.getValue()));

    Properties props = mailSender.getJavaMailProperties();
    props.put("mail.transport.protocol", "smtp");
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.debug", "true");
    mailSender.setJavaMailProperties(props);
    return mailSender;
  }
}
