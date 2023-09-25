package com.allweb.rms.core.mail;

import com.allweb.rms.entity.jpa.SystemConfiguration;
import com.allweb.rms.repository.jpa.SystemConfigurationRepository;
import com.allweb.rms.service.MailServiceProvider;
import com.allweb.rms.utils.SystemConfigurationConstants;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ExtendWith({MockitoExtension.class, SpringExtension.class})
@TestPropertySource("classpath:mail-account.properties")
class MailServiceIT {

    @Value("${mail.gmail.host}")
    private String mailServer;
    @Value("${mail.gmail.protocol}")
    private String mailProtocol;
    @Value("${mail.gmail.port}")
    private String mailServerPort;
    @Value("${mail.gmail.username}")
    private String username;
    @Value("${mail.gmail.password}")
    private String password;
    @Value("${mail.sendgrid.api.key}")
    private String sendGridApiKey;
    @Value("${mail.to}")
    private String receiver;

    @Mock
    private SystemConfigurationRepository systemConfigurationRepository;

    @Test
    void testSendGmail() {
        MailServiceProvider stubbedMailServiceProvider = this.getStubMailServiceProvider("GMAIL");
        GenericMailMessage message = this.getDefaultMailMessage();
        MailHandler gmailHandler = stubbedMailServiceProvider.getConfiguredMailHandler();
        MailException mailException = null;
        try {
            gmailHandler.send(message);
        } catch (MailException e) {
            mailException = e;
        }
        // assertions
        Assertions.assertThat(mailException).isNull();
    }

    @Test
    void testSendEmailBySendGrid() {
        MailServiceProvider stubbedMailServiceProvider = this.getStubMailServiceProvider("SENDGRID");
        GenericMailMessage message = this.getDefaultMailMessage();
        message.setReplyTo(this.username);
        MailHandler gmailHandler = stubbedMailServiceProvider.getConfiguredMailHandler();
        MailException mailException = null;
        try {
            gmailHandler.send(message);
        } catch (MailException e) {
            mailException = e;
        }
        // assertions
        Assertions.assertThat(mailException).isNull();
    }

    MailServiceProvider getStubMailServiceProvider(String mailProvider) {
        List<SystemConfiguration> mailConfigurations = this.getDefaultMailConfigurations();
        mailConfigurations.get(6).setConfigValue(mailProvider);
        // stub
        Mockito.when(systemConfigurationRepository.findAll()).thenReturn(mailConfigurations);
        return new MailServiceProvider(this.systemConfigurationRepository);
    }

    private GenericMailMessage getDefaultMailMessage() {
        GenericMailMessage message = new GenericMailMessage();
        message.setTo(this.receiver);
        message.setFrom(this.username);
        message.setBody("Test message");
        message.setSubject("Test");
        message.setSendDate(Date.from(new Date().toInstant().plus(1, ChronoUnit.MINUTES)));
        return message;
    }

    private List<SystemConfiguration> getDefaultMailConfigurations() {
        List<SystemConfiguration> configurationList = new ArrayList<>();
        SystemConfiguration configuration;
        // Mail server
        configuration = new SystemConfiguration();
        configuration.setConfigKey(SystemConfigurationConstants.MAIL_SERVER.getValue());
        configuration.setConfigValue(this.mailServer);
        configurationList.add(configuration);
        // Mail server protocol
        configuration = new SystemConfiguration();
        configuration.setConfigKey(SystemConfigurationConstants.MAIL_PROTOCOL.getValue());
        configuration.setConfigValue(this.mailProtocol);
        configurationList.add(configuration);
        // Mail server port
        configuration = new SystemConfiguration();
        configuration.setConfigKey(SystemConfigurationConstants.PORT.getValue());
        configuration.setConfigValue(this.mailServerPort);
        configurationList.add(configuration);
        // Mail username
        configuration = new SystemConfiguration();
        configuration.setConfigKey(SystemConfigurationConstants.USERNAME.getValue());
        configuration.setConfigValue(this.username);
        configurationList.add(configuration);
        // Mail password
        configuration = new SystemConfiguration();
        configuration.setConfigKey(SystemConfigurationConstants.PASSWORD.getValue());
        configuration.setConfigValue(this.password);
        configurationList.add(configuration);
        // SendGrid Api Key
        configuration = new SystemConfiguration();
        configuration.setConfigKey(SystemConfigurationConstants.API_KEY.getValue());
        configuration.setConfigValue(this.sendGridApiKey);
        configurationList.add(configuration);
        //Empty mail provider
        configuration = new SystemConfiguration();
        configuration.setConfigKey(SystemConfigurationConstants.MAIL_PROVIDER.getValue());
        configurationList.add(configuration);
        return configurationList;
    }
}
