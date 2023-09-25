package com.allweb.rms.service.mail.config;

import com.allweb.rms.repository.jpa.SystemConfigurationRepository;
import com.allweb.rms.service.SystemConfigurationService;
import org.modelmapper.ModelMapper;

public abstract class AbstractConfig extends SystemConfigurationService {

  protected AbstractConfig(
      SystemConfigurationRepository systemMailConfigurationRepository, ModelMapper modelMapper) {
    super(systemMailConfigurationRepository, modelMapper);
  }
}
