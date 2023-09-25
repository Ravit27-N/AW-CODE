package com.tessi.cxm.pfl.ms15.config;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class InternalConfig {

  @Bean
  public ObjectMapper objectMapper() {
    var mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
    mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
    mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    mapper
        .configOverride(String.class)
        .setSetterInfo(JsonSetter.Value.forValueNulls(Nulls.AS_EMPTY));

    SimpleFilterProvider simpleFilterProvider =
        new SimpleFilterProvider()
            .setFailOnUnknownId(false)
            .setDefaultFilter(SimpleBeanPropertyFilter.serializeAll());
    mapper.setFilterProvider(simpleFilterProvider);

    return mapper;
  }

  @Bean
  public ModelMapper modelMapper() {
    return new ModelMapper();
  }
}
