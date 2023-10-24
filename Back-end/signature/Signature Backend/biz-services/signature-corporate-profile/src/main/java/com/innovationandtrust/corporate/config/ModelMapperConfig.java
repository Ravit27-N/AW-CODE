package com.innovationandtrust.corporate.config;

import com.innovationandtrust.corporate.model.dto.BusinessUnitDto;
import com.innovationandtrust.corporate.model.entity.BusinessUnit;
import com.innovationandtrust.corporate.model.entity.CompanyDetail;
import com.innovationandtrust.corporate.model.entity.Employee;
import com.innovationandtrust.share.model.corporateprofile.EmployeeDTO;
import com.innovationandtrust.share.model.project.CorporateInfo;
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
        new PropertyMap<BusinessUnitDto, BusinessUnit>() {
          @Override
          protected void configure() {
            map().getParent().setId(source.getParentId());
          }
        });
    mapper.addMappings(
        new PropertyMap<EmployeeDTO, Employee>() {
          @Override
          protected void configure() {
            map().getUserAccess().setId(source.getUserAccessId());
            map().getBusinessUnit().setId(source.getBusinessUnitId());
          }
        });
    mapper.addMappings(
        new PropertyMap<Employee, EmployeeDTO>() {
          @Override
          protected void configure() {
            map().getUserAccess().setId(source.getUserAccess().getId());
            map().getDepartment().setId(source.getBusinessUnit().getId());
            map().getDepartment().setUnitName(source.getBusinessUnit().getUnitName());
            map().getDepartment().setSortOrder(source.getBusinessUnit().getSortOrder());
            map().getDepartment().setCompanyDetailId(source.getBusinessUnit().getCompanyDetail().getId());
          }
        });
    mapper.addMappings(
        new PropertyMap<CompanyDetail, CorporateInfo>() {
          @Override
          protected void configure() {
            map().setCompanyName(source.getName());
          }
        });
    mapper.getConfiguration().setSkipNullEnabled(true);
    mapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
    return mapper;
  }
}
