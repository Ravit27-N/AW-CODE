package com.tessi.cxm.pfl.ms11.config;

import com.cxm.tessi.pfl.shared.flowtreatment.model.response.DepositValidation;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tessi.cxm.pfl.ms11.entity.projection.DepositValidationProjection;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class InternalConfig {

  @Bean
  public ModelMapper modelMapper() {
    var mapper = new ModelMapper();
    mapper.addMappings(
        new PropertyMap<DepositValidationProjection, DepositValidation>() {
          @Override
          protected void configure() {
            map().getContent().setFlowType(source.getFlowType());
            map().getContent().setIdCreator(source.getIdCreator());
            map().setScanActivation(source.isScanActivation());
          }
        });

    return mapper;
  }

  @Bean
  public ObjectMapper getObjectMapper() {
    return new ObjectMapper();
  }
}
