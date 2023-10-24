package com.innovationandtrust.profile.service;

import com.innovationandtrust.profile.model.dto.CompanyDto;
import com.innovationandtrust.profile.model.entity.Company;
import com.innovationandtrust.profile.model.entity.CompanySetting;
import com.innovationandtrust.profile.model.entity.CompanySetting_;
import com.innovationandtrust.profile.model.entity.Company_;
import com.innovationandtrust.profile.repository.CompanySettingRepository;
import com.innovationandtrust.share.model.corporateprofile.CorporateSettingDto;
import com.innovationandtrust.share.model.profile.CompanySettingDto;
import com.innovationandtrust.utils.commons.AdvancedFilter;
import com.innovationandtrust.utils.commons.Filter;
import com.innovationandtrust.utils.commons.QueryOperator;
import com.innovationandtrust.utils.companySetting.CompanySettingUtils;
import com.innovationandtrust.utils.corporateprofile.feignclient.CorporateProfileFeignClient;
import com.innovationandtrust.utils.exception.exceptions.EntityNotFoundException;
import com.innovationandtrust.utils.exception.exceptions.InvalidRequestException;
import com.innovationandtrust.utils.exception.exceptions.MissingParamException;
import com.innovationandtrust.utils.keycloak.provider.IKeycloakProvider;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/** Company setting services logic. */
@Slf4j
@Service
public class CompanySettingService
    extends CommonCrudService<CompanySettingDto, CompanySetting, Long> {

  private final CompanySettingRepository companySettingRepository;

  private final CorporateProfileFeignClient corporateProfileFeignClient;

  private final CompanyService companyService;

  /** Company setting initialize dependencies. */
  protected CompanySettingService(
      ModelMapper modelMapper,
      IKeycloakProvider keycloakProvider,
      CompanySettingRepository companySettingRepository,
      CorporateProfileFeignClient corporateProfileFeignClient,
      CompanyService companyService) {
    super(modelMapper, keycloakProvider);
    this.companySettingRepository = companySettingRepository;
    this.corporateProfileFeignClient = corporateProfileFeignClient;
    this.companyService = companyService;
  }

  /**
   * to update a company themes and logo.
   *
   * @param logoFile refers to logo file.
   * @param dto refers to {@link CorporateSettingDto}
   */
  public CorporateSettingDto updateCompanyThemesAndLogo(
      CorporateSettingDto dto, MultipartFile logoFile) {

    if (!Objects.nonNull(dto.getCompanyUuid())) {
      throw new MissingParamException("company uuid");
    }

    CompanyDto company = this.companyService.findByUuid(dto.getCompanyUuid());
    if (Objects.nonNull(company)) {
      String oldFileName = "";

      if (Objects.nonNull(logoFile)) {
        oldFileName = company.getLogo();

        // Upload new logo
        var newFilename = this.corporateProfileFeignClient.uploadLogo(logoFile);

        dto.setLogo(newFilename);
        company.setLogo(newFilename);
        company.setModifiedBy(getUserId());
        this.companyService.update(company);
      }

      dto.setCompanyId(company.getId());
      CorporateSettingDto corporateSettingDto =
          this.modelMapper.map(dto, CorporateSettingDto.class);
      corporateSettingDto.setLogo(company.getLogo());
      return this.corporateProfileFeignClient.updateSetting(corporateSettingDto, oldFileName);
    }

    throw new EntityNotFoundException("Company not found...");
  }

  /**
   * To get all company setting.
   *
   * @param companyUuid unique company uuid
   * @return list of company setting object {@link CompanySettingDto}
   */
  public List<CompanySettingDto> getAll(String companyUuid) {
    var settingList = this.mapAll(getSettings(companyUuid), CompanySettingDto.class);
    settingList.forEach(
        setting -> {
          // Prevent model mapper, wrong mapping
          setting.setCompanyChannel(null);
          setting.setCompanyFileType(null);
        });
    return settingList;
  }

  /**
   * Creating or updating company unique settings.
   *
   * @param settingList refers to list of {@link CompanySettingDto}
   * @return saved company settings.
   */
  public List<CompanySettingDto> save(List<CompanySettingDto> settingList) {
    CompanySettingUtils.validateCompanySettings(settingList);
    CompanySettingUtils.validateOptionBySuperAdmin(settingList);

    var companyUuid = CompanySettingUtils.getCompanyUuid(settingList);
    var existsCompanySettings = getSettings(companyUuid);
    if (!existsCompanySettings.isEmpty()) {
      this.companySettingRepository.deleteAll(existsCompanySettings);
    }

    var company = this.modelMapper.map(this.companyService.findByUuid(companyUuid), Company.class);
    if (Objects.isNull(company)) {
      throw new InvalidRequestException("Company not found with uuid: " + companyUuid);
    }
    var companySettings = this.mapAll(settingList, CompanySetting.class);
    companySettings.forEach(
        setting -> {
          setting.setCreatedBy(getUserId());
          setting.setCompany(company);
        });

    log.info("Saving company settings...");
    var savedCompanySetting = this.companySettingRepository.saveAll(companySettings);

    log.info("Syncing company settings to corporate...");
    this.corporateProfileFeignClient.saveCompanySetting(settingList);

    log.info("Successfully save company settings...");
    var response = this.mapAll(savedCompanySetting, CompanySettingDto.class);
    response.forEach(CompanySettingUtils::setToNull);
    return response;
  }

  public CompanySettingDto getSettingByLevel(String companyUuid, String signatureLevel) {
    CompanySettingUtils.checkEmpty(companyUuid, signatureLevel);

    log.info("Getting company setting from database...");
    var companySetting =
        this.companySettingRepository
            .findOne(
                AdvancedFilter.searchByFields(
                    Arrays.asList(
                        Filter.builder()
                            .referenceField(Arrays.asList(CompanySetting_.COMPANY, Company_.UUID))
                            .operator(QueryOperator.EQUALS)
                            .value(companyUuid)
                            .build(),
                        Filter.builder()
                            .field(CompanySetting_.SIGNATURE_LEVEL)
                            .operator(QueryOperator.EQUALS)
                            .value(signatureLevel)
                            .build())))
            .orElse(null);

    if (Objects.isNull(companySetting)) {
      log.info("No signature level:{} for this company:{}...", signatureLevel, companyUuid);
      throw new InvalidRequestException(
          "Not found signatureLevel:" + signatureLevel + " in company:" + companyUuid);
    }

    log.info("Retrieved signature level:{} for this company:{}...", signatureLevel, companyUuid);
    var response = this.modelMapper.map(companySetting, CompanySettingDto.class);
    CompanySettingUtils.setToNull(response);
    return response;
  }

  private List<CompanySetting> getSettings(String uuid) {
    return this.companySettingRepository.findAll(
        AdvancedFilter.searchByField(
            Filter.builder()
                .referenceField(Arrays.asList(CompanySetting_.COMPANY, Company_.UUID))
                .operator(QueryOperator.EQUALS)
                .value(uuid)
                .build()));
  }
}
