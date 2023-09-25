package com.allweb.rms.service;

import com.allweb.rms.core.mail.MailHandler;
import com.allweb.rms.core.mail.handler.MimeMailHandler;
import com.allweb.rms.core.mail.handler.SendGridMailHandler;
import com.allweb.rms.entity.jpa.SystemConfiguration;
import com.allweb.rms.repository.jpa.SystemConfigurationRepository;
import com.allweb.rms.utils.SystemConfigurationConstants;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
public class MailServiceProvider {
  private final SystemConfigurationRepository systemConfigurationRepository;
  private final Map<String, String> configuration;

  public MailServiceProvider(SystemConfigurationRepository systemMailConfigurationRepository) {
    this.systemConfigurationRepository = systemMailConfigurationRepository;
    this.configuration = getConfiguration();
  }

  /**
   * @return {@link MailHandler}
   */
  public MailHandler getConfiguredMailHandler() {
    MailProvider mailProvider = this.getConfiguredMailProvider();
    if (mailProvider == MailProvider.GMAIL) {
      return getGMailMailHandler();
    } else if (mailProvider == MailProvider.SENDGRID) {
      return getSendGridMailHandler();
    }
    return null;
  }

  public MailProvider getConfiguredMailProvider() {
    return MailProvider.valueOf(
        this.configuration
            .get(SystemConfigurationConstants.MAIL_PROVIDER.getValue())
            .toUpperCase());
  }

  public String getSystemAdminEmailAddress() {
    return this.configuration.get(SystemConfigurationConstants.MAIL_SENDER.getValue());
  }

  Map<String, String> getConfiguration() {
    return systemConfigurationRepository.findAll().stream()
        .collect(
            Collectors.toMap(
                SystemConfiguration::getConfigKey, SystemConfiguration::getConfigValue));
  }

  MimeMailHandler getGMailMailHandler() {
    MimeMailHandler mailHandler = new MimeMailHandler();
    mailHandler.setHost(
        this.configuration.get(SystemConfigurationConstants.MAIL_SERVER.getValue()));
    mailHandler.setProtocol(
        this.configuration.get(SystemConfigurationConstants.MAIL_PROTOCOL.getValue()));
    mailHandler.setPort(
        Integer.parseInt(this.configuration.get(SystemConfigurationConstants.PORT.getValue())));
    mailHandler.setUserName(
        this.configuration.get(SystemConfigurationConstants.USERNAME.getValue()));
    mailHandler.setPassword(
        this.configuration.get(SystemConfigurationConstants.PASSWORD.getValue()));
    return mailHandler;
  }

  SendGridMailHandler getSendGridMailHandler() {
    String apiKey = this.configuration.get(SystemConfigurationConstants.API_KEY.getValue());
    return new SendGridMailHandler(apiKey);
  }

  @RequiredArgsConstructor
  enum MailProvider {
    GMAIL("GMAIL"),
    SENDGRID("SENDGRID");

    private final String provider;

    public String getProviderName() {
      return provider;
    }
  }
}
