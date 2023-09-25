package com.tessi.cxm.pfl.ms5.config;

import com.tessi.cxm.pfl.ms5.dto.CreateUserResponseDTO;
import com.tessi.cxm.pfl.ms5.dto.ProfileDto;
import com.tessi.cxm.pfl.ms5.dto.QueryUserResponseDTO;
import com.tessi.cxm.pfl.ms5.entity.Profile;
import com.tessi.cxm.pfl.ms5.entity.UserEntity;
import com.tessi.cxm.pfl.shared.model.User;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

  @Bean
  public ModelMapper modelMapper() {
    var modelMapper = new ModelMapper();
    modelMapper.addMappings(
        new PropertyMap<UserEntity, QueryUserResponseDTO>() {
          @Override
          protected void configure() {
            map().setServiceByDepartment(source.getDepartment());
          }
        });

    modelMapper.addMappings(new PropertyMap<ProfileDto, Profile>() {
      @Override
      protected void configure() {
        map().setDisplayName(source.getDisplayName());
        map().setName(source.getName());
      }
    });
    modelMapper.addMappings(new PropertyMap<UserEntity, QueryUserResponseDTO>() {
      @Override
      protected void configure() {
          map().setReturnAddressLevel(source.getReturnAddressLevelString());
      }
    });
    modelMapper.addMappings(new PropertyMap<User, QueryUserResponseDTO>() {
      @Override
      protected void configure() {
        map().setId(0);
      }
    });
    modelMapper.addMappings(
        new PropertyMap<User, CreateUserResponseDTO>() {
          /** Called by ModelMapper to configure mappings as defined in the PropertyMap. */
          @Override
          protected void configure() {
            skip(destination.getId());
          }
        });
    return modelMapper;
  }
}
