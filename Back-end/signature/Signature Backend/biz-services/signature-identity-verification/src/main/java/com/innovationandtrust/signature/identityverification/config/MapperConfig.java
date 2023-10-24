package com.innovationandtrust.signature.identityverification.config;

import com.innovationandtrust.signature.identityverification.model.model.dossier.Dossier;
import com.innovationandtrust.utils.signatureidentityverification.dto.DossierDto;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Configuration class for ModelMapper. */
@Configuration
public class MapperConfig {
  @Bean
  ModelMapper mapper() {
    var mapper = new ModelMapper();

    mapper.addMappings(
        new PropertyMap<DossierDto, Dossier>() {
          @Override
          protected void configure() {
            map().setParticipantUuid(source.getParticipantUuid());
          }
        });
    return mapper;
  }
}
