package com.innovationandtrust.signature.identityverification.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Configuration class for Jackson. */
@Configuration
public class JacksonConfiguration {
  /**
   * Configures the ObjectMapper.
   *
   * @return the ObjectMapper.
   */
  @Bean
  public ObjectMapper objectMapper() {
    var mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS, false);
    mapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
    mapper.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);
    mapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE, true);
    return mapper;
  }
}
