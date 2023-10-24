package com.innovationandtrust.profile.config;

import com.innovationandtrust.profile.model.dto.CorporateUserDto;
import com.innovationandtrust.profile.model.dto.NormalUserDto;
import com.innovationandtrust.profile.model.dto.UserDto;
import com.innovationandtrust.profile.model.dto.UserEmployee;
import com.innovationandtrust.profile.model.entity.CompanySetting;
import com.innovationandtrust.share.model.profile.CompanySettingDto;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {
  @Bean
  public ModelMapper modelMapper() {
    var mapper = new ModelMapper();
    mapper.addMappings(
        new PropertyMap<CompanySetting, CompanySettingDto>() {
          @Override
          protected void configure() {
            map().setCompanyUuid(source.getCompany().getUuid());
          }
        });
    mapper.addMappings(
        new PropertyMap<CorporateUserDto, UserDto>() {
          @Override
          protected void configure() {
            map().setCompanyId(source.getCompanyId());
          }
        });

    mapper.addMappings(
        new PropertyMap<NormalUserDto, UserEmployee>() {
          @Override
          protected void configure() {
            map().setUserAccessId(source.getUserAccess().getId());
          }
        });

    mapper.getConfiguration().setSkipNullEnabled(true);
    mapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
    return mapper;
  }
}
