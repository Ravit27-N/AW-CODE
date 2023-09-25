package com.allweb.rms.service.mail.config;

import static com.allweb.rms.utils.SystemConfigurationConstants.API_KEY;

import com.allweb.rms.repository.jpa.SystemConfigurationRepository;
import com.sendgrid.SendGrid;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SendGridConfig extends AbstractConfig {

  /*
   *
   * Gmail Config
   *
   * */

  protected SendGridConfig(
      SystemConfigurationRepository systemMailConfigurationRepository, ModelMapper modelMapper) {
    super(systemMailConfigurationRepository, modelMapper);
  }

  public SendGrid sendGrid() {
    return new SendGrid(config().get(API_KEY.getValue()));
  }
}
