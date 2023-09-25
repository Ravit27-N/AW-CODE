package com.tessi.cxm.pfl.ms3.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@TestConfiguration
public class InternalConfig {
  @Bean
  public ModelMapper modelMapper() {
    return new ModelMapper();
  }

  @Bean
  public ObjectMapper getModelMapper() {
    return new ObjectMapper();
  }
}
