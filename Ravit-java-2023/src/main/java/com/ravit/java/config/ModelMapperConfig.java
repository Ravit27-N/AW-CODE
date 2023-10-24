package com.ravit.java.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

  @Bean
  public ModelMapper modelMapper(ObjectMapper objectMapper) {
    ModelMapper modelMapper = new ModelMapper();

    modelMapper
        .getConfiguration()
        .setMatchingStrategy(MatchingStrategies.STANDARD)
        .setSkipNullEnabled(true);

    return modelMapper;
  }
}
