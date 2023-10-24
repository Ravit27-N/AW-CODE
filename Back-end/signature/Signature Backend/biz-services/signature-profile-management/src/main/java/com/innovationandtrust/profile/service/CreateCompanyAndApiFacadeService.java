package com.innovationandtrust.profile.service;

import com.innovationandtrust.profile.model.dto.CompanyDto;
import com.innovationandtrust.profile.model.entity.Company;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateCompanyAndApiFacadeService {
    private final GraviteeService graviteeService;
    private final CompanyService companyService;

    public CompanyDto create(CompanyDto dto) {
        CompanyDto companyDto = this.companyService.save(dto);
        Company company = new Company();
        company.setName(companyDto.getName());
        company.setUuid(companyDto.getUuid());
        try {
            this.graviteeService.createAPIFacade(company);
            return companyDto;
        } catch (Exception e) {
            this.companyService.delete(companyDto.getId());
            log.error("Unable to create company because : ", e);
            throw new InternalError("Unable to create company because :" + e.getMessage());
        }
    }
}
