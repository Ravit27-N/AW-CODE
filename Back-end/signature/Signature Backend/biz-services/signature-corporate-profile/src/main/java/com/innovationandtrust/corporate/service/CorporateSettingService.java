package com.innovationandtrust.corporate.service;

import com.innovationandtrust.corporate.controller.CorporateSettingController;
import com.innovationandtrust.corporate.controller.CorporateSettingPublicController;
import com.innovationandtrust.corporate.exception.FileRequestException;
import com.innovationandtrust.corporate.model.DocumentContent;
import com.innovationandtrust.corporate.model.dto.CompanyDto;
import com.innovationandtrust.corporate.model.dto.CompanyIdListDto;
import com.innovationandtrust.corporate.model.dto.CorporateSettingRequest;
import com.innovationandtrust.corporate.model.entity.CompanySetting;
import com.innovationandtrust.corporate.model.entity.CompanySetting_;
import com.innovationandtrust.corporate.model.entity.CorporateSetting;
import com.innovationandtrust.corporate.repository.CompanySettingRepository;
import com.innovationandtrust.corporate.repository.CorporateSettingRepository;
import com.innovationandtrust.corporate.service.restclient.ProfileFeignClient;
import com.innovationandtrust.corporate.service.specification.CorporateSettingSpecification;
import com.innovationandtrust.share.constant.NotificationConstant;
import com.innovationandtrust.share.constant.RoleConstant;
import com.innovationandtrust.share.model.corporateprofile.CorporateSettingDto;
import com.innovationandtrust.share.model.profile.Company;
import com.innovationandtrust.share.model.profile.CompanySettingDto;
import com.innovationandtrust.utils.authenticationUtils.AuthenticationUtils;
import com.innovationandtrust.utils.commons.AdvancedFilter;
import com.innovationandtrust.utils.commons.Filter;
import com.innovationandtrust.utils.commons.QueryOperator;
import com.innovationandtrust.utils.companySetting.CompanySettingUtils;
import com.innovationandtrust.utils.exception.exceptions.InvalidRequestException;
import com.innovationandtrust.utils.file.model.FileResponse;
import com.innovationandtrust.utils.file.provider.FileProvider;
import com.innovationandtrust.utils.keycloak.provider.IKeycloakProvider;
import jakarta.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * Handling business logic of corporate setting.
 *
 * @see CorporateSettingRepository
 * @see CorporateSettingController
 * @see CorporateSettingPublicController
 */
@Slf4j
@Service
public class CorporateSettingService
    extends CommonCrudService<CorporateSettingDto, CorporateSetting, Long> {
  private static final String NOT_FOUND = "CorporateSetting Not Found!";
  private final CorporateSettingRepository corporateSettingRepository;
  private final FileProvider fileProvider;
  private final ProfileFeignClient profileFeignClient;
  private final CompanyDetailService companyDetailService;

  private final CompanySettingRepository companySettingRepository;

  protected CorporateSettingService(
      ModelMapper modelMapper,
      IKeycloakProvider keycloakProvider,
      CorporateSettingRepository corporateSettingRepository,
      FileProvider fileProvider,
      ProfileFeignClient profileFeignClient,
      CompanyDetailService companyDetailService,
      CompanySettingRepository companySettingRepository) {
    super(modelMapper, keycloakProvider);
    this.corporateSettingRepository = corporateSettingRepository;
    this.fileProvider = fileProvider;
    this.profileFeignClient = profileFeignClient;
    this.companyDetailService = companyDetailService;
    this.companySettingRepository = companySettingRepository;
  }

  /**
   * Retrieves an entity by its id.
   *
   * @param id must not be {@literal null}.
   * @return the entity with the given id or {@literal Optional#empty()} if none found.
   * @throws IllegalArgumentException if {@literal id} is {@literal null}.
   */
  protected CorporateSetting findEntityById(long id) {
    return corporateSettingRepository
        .findById(id)
        .orElseThrow(() -> new EntityNotFoundException(NOT_FOUND));
  }

  /**
   * Retrieves a corporate setting dto by its id.
   *
   * @param id must not be {@literal null}.
   * @return the dto with the given id.
   */
  @Override
  @Transactional(readOnly = true)
  public CorporateSettingDto findById(Long id) {
    return mapData(this.findEntityById(id));
  }

  /**
   * Retrieves a corporate setting dto by company id.
   *
   * @param id must not be, and it's companyId {@literal null}.
   * @return the dto with the given company id.
   */
  public List<CorporateSettingDto> findByCompanyId(Long id) {
    var corporateSetting =
        this.corporateSettingRepository.findAll(
            Specification.where(
                CorporateSettingSpecification.findByCompanyIdWhereDefault(id, false)));
    if (corporateSetting.isEmpty()) {
      corporateSetting =
          this.corporateSettingRepository.findAll(
              Specification.where(
                  CorporateSettingSpecification.findByCompanyIdWhereDefault(id, true)));
    }
    return this.mapAll(corporateSetting, CorporateSettingDto.class);
  }

  /**
   * Retrieves a Company Entity.
   *
   * @return the Company entity
   * @throws IllegalArgumentException if {@literal id} is {@literal null}
   */
  public Company getCorporateSettingByUser(Long userId) {
    var id = Objects.isNull(userId) ? this.getUserId() : userId;
    var company = this.profileFeignClient.findCompanyByCorporateId(id).orElse(null);

    if (company == null) {
      company =
          this.profileFeignClient
              .findCompanyById(
                  this.companyDetailService.getCompanyDetailByEmployeeId(id).getCompanyId())
              .orElseThrow(() -> new EntityNotFoundException("This user does not in any company"));
    }

    company.setTheme(this.findByCompanyId(company.getId()));
    return company;
  }

  /**
   * Retrieves a corporate setting dto by company name.
   *
   * @param uuid must not be, and it's company's name {@literal null}.
   * @return the dto with the given company id.
   */
  @Transactional(readOnly = true)
  public List<CorporateSettingDto> findByCompanyUuid(String uuid) {
    if (!StringUtils.hasText(uuid)) {
      throw new EntityNotFoundException(
          "The company name is required and cannot be empty or null!");
    }
    var company = this.profileFeignClient.findCompanyByUuid(uuid);
    return company
        .map(value -> findByCompanyId(value.getId()))
        .orElseThrow(
            () -> new EntityNotFoundException("Could not find any themes of this company"));
  }

  /**
   * Reset corporate setting by Company id *** WILL BE REMOVED ***.
   *
   * @return a list of corporateSettingDTO.
   */
  @Transactional(rollbackFor = Exception.class)
  public List<CorporateSettingDto> resetByCompany() {
    var id = this.getCorporateSettingByUser(null).getId();
    this.corporateSettingRepository.delete(
        Specification.where(CorporateSettingSpecification.findByCompanyIdWhereDefault(id, false)));

    var corporateSetting =
        this.corporateSettingRepository.findAll(
            Specification.where(
                CorporateSettingSpecification.findByCompanyIdWhereDefault(id, true)));

    return this.mapAll(corporateSetting, CorporateSettingDto.class);
  }

  /**
   * Retrieves all corporate settings.
   *
   * @return List of Corporate settings.
   */
  @Override
  @Transactional(readOnly = true)
  public List<CorporateSettingDto> findAll() {
    return this.mapAll(this.corporateSettingRepository.findAll(), CorporateSettingDto.class);
  }

  /**
   * Retrieves pagination of corporate settings.
   *
   * @return the pagination of corporate settings.
   */
  @Override
  @Transactional(readOnly = true)
  public Page<CorporateSettingDto> findAll(Pageable pageable) {
    if (pageable.isUnpaged()) {
      return new PageImpl<>(this.findAll());
    }
    return corporateSettingRepository.findAll(pageable).map(super::mapData);
  }

  /**
   * Insert a corporate setting record.
   *
   * @param logo refers to a logo file and must not be {@literal null}.
   * @param corporateSettingRequest refers to a request obj that use to insert into a database.
   * @return the corporate setting dto.
   */
  @Transactional(rollbackFor = Exception.class)
  public CorporateSettingDto save(
      CorporateSettingRequest corporateSettingRequest, MultipartFile logo) {
    CorporateSettingDto csDto = modelMapper.map(corporateSettingRequest, CorporateSettingDto.class);
    csDto.setLogo(this.uploadLogo(logo).getFileName());
    return mapData(this.corporateSettingRepository.save(this.mapEntity(csDto)));
  }

  /**
   * Update or create a corporate setting, only have default and current theme.
   *
   * @param logo refers to a logo file and must not be {@literal null}.
   * @param csDto refers to an obj that uses to request for update into a database.
   * @return the updated corporate setting dto.
   */
  @Transactional(rollbackFor = Exception.class)
  public CorporateSettingDto saveOrUpdate(CorporateSettingDto csDto, MultipartFile logo) {
    var company = this.profileFeignClient.findCompanyByCorporateId(this.getUserId());
    if (company.isPresent()) {
      csDto.setCompanyId(company.get().getId());
      CorporateSetting entity = this.mapEntity(csDto);
      var setting =
          this.corporateSettingRepository.findOne(
              Specification.where(
                  CorporateSettingSpecification.findByCompanyIdWhereDefault(
                      csDto.getCompanyId(), false)));
      if (setting.isPresent()) {
        entity = this.mapEntity(csDto, setting.get());
      }
      this.setTheLastLogo(entity, logo, company);
      entity.setDefault(false);
      return mapData(this.corporateSettingRepository.save(entity));
    }
    throw new EntityNotFoundException(
        "This user does not have permission to update or create a company");
  }

  private void setTheLastLogo(
      CorporateSetting entity, MultipartFile logo, Optional<Company> company) {
    if (Objects.nonNull(logo)) {
      // delete the old logo
      var uploadLogo = this.uploadLogo(logo);
      if (entity.getLogo() != null) {
        this.fileProvider.deleteFile(entity.getLogo());
      }
      if (Objects.nonNull(uploadLogo) && company.isPresent()) {
        entity.setLogo(uploadLogo.getFileName());
        company.get().setLogo(uploadLogo.getFileName());
        this.profileFeignClient.update(this.modelMapper.map(company, CompanyDto.class));
      }
    } else if (Objects.isNull(entity.getLogo()) && Objects.isNull(entity.getId())) {
      this.corporateSettingRepository
          .findOne(
              Specification.where(
                  CorporateSettingSpecification.findByCompanyIdWhereDefault(
                      entity.getCompanyId(), true)))
          .ifPresent(s -> entity.setLogo(s.getLogo()));
    }
  }

  /** Super admin updating corporate setting. */
  @Transactional(rollbackFor = Exception.class)
  public CorporateSettingDto updateSetting(CorporateSettingDto settingDto, String oldFile) {
    CorporateSetting entity = this.mapEntity(settingDto);
    var foundSetting =
        this.corporateSettingRepository.findOne(
            Specification.where(
                CorporateSettingSpecification.findByCompanyIdWhereDefault(
                    entity.getCompanyId(), false)));
    if (foundSetting.isPresent()) {
      entity = this.mapEntity(settingDto, foundSetting.get());
    }

    if (StringUtils.hasText(oldFile)) {
      try {
        this.fileProvider.deleteFile(oldFile);
      } catch (Exception e) {
        log.error("Error " + e.getMessage());
      }
    }

    entity.setDefault(false);
    return this.modelMapper.map(
        this.corporateSettingRepository.save(entity), CorporateSettingDto.class);
  }

  /**
   * Delete a corporate setting.
   *
   * @param id refers an id of corporate setting.
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public void delete(Long id) {
    this.corporateSettingRepository.deleteById(id);
  }

  private FileResponse uploadLogo(MultipartFile logo) {
    if (logo.isEmpty()) {
      throw new FileRequestException("Logo is required!");
    }
    return this.fileProvider.upload(logo, "", false);
  }

  /**
   * Viewing file as base64 string format.
   *
   * @param fileName refers to file name
   * @return string of base64
   */
  public String viewFile(String fileName) {
    try {
      Resource resource = fileProvider.download(fileName, false);
      if (Objects.nonNull(resource)) {
        return encodeFileToBase64(resource);
      }
    } catch (Exception exception) {
      log.error("Error downloading file: ", exception);
      throw new FileRequestException("Unable to view the logo.");
    }
    throw new FileRequestException("Unable to view the logo.");
  }

  /**
   * To retrieve content of file.
   *
   * @param docName refers to the name of the document
   * @return object of {@link DocumentContent}
   */
  public DocumentContent viewFileContent(String docName) {
    try {
      Resource resource = fileProvider.download(docName, false);
      assert resource != null;
      return new DocumentContent(resource, resource.getURL().openConnection().getContentType());
    } catch (Exception exception) {
      log.error("Load file failed", exception);
      return new DocumentContent(null, "");
    }
  }

  private String encodeFileToBase64(Resource resource) {
    try {
      return Base64.getEncoder().encodeToString(resource.getContentAsByteArray());
    } catch (IOException e) {
      log.error("Unable to encode", e);
      throw new FileRequestException("Unable to covert file to base64");
    }
  }

  /**
   * Find corporate setting by company ids.
   *
   * @param ids refers to the list of company's id
   * @return List of CorporateSettingDto
   */
  public List<CorporateSettingDto> findByCompany(CompanyIdListDto ids) {
    var corporateSetting =
        this.corporateSettingRepository.findAll(
            Specification.where(
                CorporateSettingSpecification.findByCompanyIds(ids.getCompanyIds(), false)));

    return this.mapAll(corporateSetting, CorporateSettingDto.class);
  }

  public String uploadFile(MultipartFile file) {
    var fileRes = this.fileProvider.upload(file, "", false);
    return fileRes.getFileName();
  }

  /**
   * Creating or updating company unique settings by super admin.
   *
   * @param settingList refers to list of {@link CompanySettingDto}
   */
  public void saveCompanySetting(List<CompanySettingDto> settingList) {
    CompanySettingUtils.validateCompanySettings(settingList);
    CompanySettingUtils.validateOptionBySuperAdmin(settingList);

    var companyUuid = CompanySettingUtils.getCompanyUuid(settingList);
    var foundCompanySettings = getSettings(companyUuid, false);
    List<CompanySettingDto> existCompanySettings = new ArrayList<>();
    if (!foundCompanySettings.isEmpty()) {
      // Restore company config for their employee
      foundCompanySettings.forEach(
          setting ->
              existCompanySettings.add(
                  CompanySettingDto.builder()
                      .signatureLevel(setting.getSignatureLevel())
                      .companyChannel(setting.getCompanyChannel())
                      .companyFileType(setting.getCompanyFileType())
                      .build()));
      log.info("Delete existing company settings by super-admin...");
      this.companySettingRepository.deleteAll(
          this.mapAll(foundCompanySettings, CompanySetting.class));

      log.info("Applying existing company options...");
      settingList.forEach(
          setting ->
              existCompanySettings.stream()
                  .filter(
                      existSetting ->
                          Objects.equals(
                              existSetting.getSignatureLevel(), setting.getSignatureLevel()))
                  .findFirst()
                  .ifPresent(
                      existSetting -> {
                        log.info(
                            "Applying for signature level:{}...", existSetting.getSignatureLevel());
                        setting.setCompanyChannel(
                            Objects.equals(
                                    setting.getChannelReminder(), NotificationConstant.SMS_EMAIL)
                                ? existSetting.getCompanyChannel()
                                : setting.getChannelReminder());

                        setting.setCompanyFileType(
                            CompanySettingUtils.getCompanyFileTypes(
                                existSetting.getCompanyFileType(), setting.getFileType()));
                      }));
    }

    var companySettings = this.mapAll(settingList, CompanySetting.class);

    // Not yet has user story about corporate admin set/update their options
    // So will set default options selected by super admin
    // Will remove this code when corporate can take action
    companySettings.forEach(
        setting ->
            settingList.stream()
                .filter(s -> Objects.equals(s.getSignatureLevel(), setting.getSignatureLevel()))
                .findFirst()
                .ifPresent(
                    s -> {
                      setting.setCompanyChannel(s.getChannelReminder());
                      setting.setCompanyFileType(s.getFileType());
                    }));

    log.info("Saving company settings by super-admin...");
    this.companySettingRepository.saveAll(companySettings);
    log.info("Saving company settings successfully...");
  }

  /**
   * Updating company settings message. By corporate-admin
   *
   * @param settingList refers to list of {@link CompanySettingDto}
   * @return saved company settings.
   */
  public List<CompanySettingDto> updateCompanySettings(List<CompanySettingDto> settingList) {
    CompanySettingUtils.validateCompanySettings(settingList);
    CompanySettingUtils.validateOptionByCorporateAdmin(settingList);

    var companyUuid = CompanySettingUtils.getCompanyUuid(settingList);

    log.info("Getting company settings from mandatory database...");
    var currentSettings = this.profileFeignClient.findCompanySettings(companyUuid);
    CompanySettingUtils.validateSettings(currentSettings, settingList);

    var existsCompanySettings = getSettings(companyUuid, true);
    if (!existsCompanySettings.isEmpty()) {
      log.info("Found company settings...");

      settingList.forEach(
          setting ->
              existsCompanySettings.stream()
                  .filter(
                      existSetting ->
                          Objects.equals(
                              existSetting.getSignatureLevel(), setting.getSignatureLevel()))
                  .findFirst()
                  .ifPresent(existSetting -> setting.setId(existSetting.getId())));

      var settingsEntity = this.mapAll(settingList, CompanySetting.class);

      settingsEntity.forEach(setting -> setting.setModifiedBy(getUserId()));

      log.info("Saving company settings...");
      this.companySettingRepository.saveAll(settingsEntity);
      this.mergeSettings(currentSettings, settingList);
      return settingList;
    }

    return Collections.emptyList();
  }

  /**
   * To get all company setting.
   *
   * @param companyUuid unique company uuid
   * @return list of company setting object {@link CompanySettingDto}
   */
  public List<CompanySettingDto> getCompanySettings(String companyUuid) {
    return this.mapAll(getSettings(companyUuid, false), CompanySettingDto.class);
  }

  private List<CompanySettingDto> getSettings(String companyUuid, boolean isUpdating) {
    log.info("Getting company settings from database...");
    var settingList =
        this.mapAll(
            this.companySettingRepository.findCompanySettingsByCompanyUuid(companyUuid),
            CompanySettingDto.class);

    if (!isUpdating && !RoleConstant.isSuperAdmin(AuthenticationUtils.getUserRoles())) {
      log.info("Getting company settings from mandatory database...");
      var currentSettings = this.profileFeignClient.findCompanySettings(companyUuid);
      // Validate to prevent any change direct from database
      CompanySettingUtils.validateSettings(currentSettings, settingList);
      // Merge with available setting options
      this.mergeSettings(currentSettings, settingList);
    }

    return settingList;
  }

  public CompanySettingDto getSettingByLevel(String companyUuid, String signatureLevel) {
    CompanySettingUtils.checkEmpty(companyUuid, signatureLevel);
    var foundCompany =
        getSetting(companyUuid, signatureLevel)
            .orElseThrow(
                () ->
                    new InvalidRequestException(
                        "Not found signatureLevel:"
                            + signatureLevel
                            + " in company:"
                            + companyUuid));
    log.info(
        "Getting company setting from database companyUuid:{} and signatureLevel:{}...",
        companyUuid,
        signatureLevel);
    var setting = this.profileFeignClient.findCompanySetting(companyUuid, signatureLevel);

    if (Objects.nonNull(setting)) {
      var companySetting = this.modelMapper.map(foundCompany, CompanySettingDto.class);
      // Validate to prevent any change direct from database
      CompanySettingUtils.validateSetting(setting, companySetting);
      // Merge with available setting options
      companySetting.setFileType(setting.getFileType());
      companySetting.setChannelReminder(setting.getChannelReminder());

      return companySetting;
    }

    throw new InvalidRequestException(
        "Not found signatureLevel:" + signatureLevel + " in company:{}" + companyUuid);
  }

  private Optional<CompanySetting> getSetting(String companyUuid, String signatureLevel) {
    return this.companySettingRepository.findOne(
        AdvancedFilter.searchByFields(
            Arrays.asList(
                Filter.builder()
                    .field(CompanySetting_.COMPANY_UUID)
                    .operator(QueryOperator.EQUALS)
                    .value(companyUuid)
                    .build(),
                Filter.builder()
                    .field(CompanySetting_.SIGNATURE_LEVEL)
                    .operator(QueryOperator.EQUALS)
                    .value(signatureLevel)
                    .build())));
  }

  /**
   * @param currentSettings refers to settings get from mandatory database.
   * @param settingList refers to settings get from corporate database.
   */
  private void mergeSettings(
      List<CompanySettingDto> currentSettings, List<CompanySettingDto> settingList) {
    settingList.forEach(
        setting ->
            currentSettings.stream()
                .filter(
                    currentSetting ->
                        Objects.equals(
                            currentSetting.getSignatureLevel(), setting.getSignatureLevel()))
                .findFirst()
                .ifPresent(
                    currentSetting -> {
                      setting.setFileType(currentSetting.getFileType());
                      setting.setChannelReminder(currentSetting.getChannelReminder());
                    }));
  }
}
