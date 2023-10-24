package com.innovationandtrust.profile.service;

import com.innovationandtrust.profile.exception.DuplicateCompanyNameException;
import com.innovationandtrust.profile.exception.DuplicateCompanySiretException;
import com.innovationandtrust.profile.model.dto.CompanyDto;
import com.innovationandtrust.profile.model.dto.LogoResponse;
import com.innovationandtrust.profile.model.entity.Company;
import com.innovationandtrust.profile.model.entity.CompanySetting;
import com.innovationandtrust.profile.repository.CompanyRepository;
import com.innovationandtrust.profile.repository.CompanySettingRepository;
import com.innovationandtrust.profile.service.restclient.CompanyDetailFeignClient;
import com.innovationandtrust.profile.service.spefication.CompanySpec;
import com.innovationandtrust.share.constant.NotificationConstant;
import com.innovationandtrust.share.constant.RoleConstant;
import com.innovationandtrust.share.enums.SignatureSettingLevel;
import com.innovationandtrust.share.model.corporateprofile.CompanyDetailDTO;
import com.innovationandtrust.share.model.profile.CompanyIdListDTO;
import com.innovationandtrust.share.model.profile.CompanySettingDto;
import com.innovationandtrust.utils.corporateprofile.feignclient.CorporateProfileFeignClient;
import com.innovationandtrust.utils.exception.exceptions.EntityNotFoundException;
import com.innovationandtrust.utils.file.utils.FileUtils;
import com.innovationandtrust.utils.keycloak.provider.IKeycloakProvider;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/** Company service logic. */
@Service
@Transactional
@Slf4j
public class CompanyService extends CommonCrudService<CompanyDto, Company, Long> {
  private final CompanyRepository companyRepository;
  private final CompanyDetailFeignClient detailFeignClient;
  private final CorporateProfileFeignClient corporateProfileFeignClient;
  private final CorporateUserService corporateUserService;
  private final CompanySettingRepository companySettingRepository;
  private final UserService userService;

  protected CompanyService(
      ModelMapper modelMapper,
      IKeycloakProvider keycloakProvider,
      CompanyRepository companyRepository,
      CompanyDetailFeignClient detailFeignClient,
      CorporateProfileFeignClient corporateProfileFeignClient,
      CorporateUserService corporateUserService,
      CompanySettingRepository companySettingRepository,
      UserService userService) {
    super(modelMapper, keycloakProvider);
    this.companyRepository = companyRepository;
    this.detailFeignClient = detailFeignClient;
    this.corporateProfileFeignClient = corporateProfileFeignClient;
    this.corporateUserService = corporateUserService;
    this.companySettingRepository = companySettingRepository;
    this.userService = userService;
  }

  private Boolean findByNameAndNotEqualId(String name, Long id) {
    return this.companyRepository
        .findOne(
            Specification.where(
                Objects.requireNonNull(CompanySpec.findByName(name))
                    .and(CompanySpec.findByNotEqualId(id))))
        .isPresent();
  }

  private Boolean findBySiretAndNotEqualId(String siret, Long id) {
    return this.companyRepository
        .findOne(
            Specification.where(
                Objects.requireNonNull(CompanySpec.findBySiret(siret))
                    .and(CompanySpec.findByNotEqualId(id))))
        .isPresent();
  }

  /**
   * To check company's name is already exist or not.
   *
   * @param name refers to company's name that input by user
   * @return true or false
   */
  public Boolean validateName(String name) {
    return this.findByName(name) != null;
  }

  public Boolean validateSiret(String siret) {
    return this.companyRepository
        .findOne(Specification.where(CompanySpec.findBySiret(siret)))
        .isPresent();
  }

  private void validateNameSiret(String name, String siret) {
    if (Boolean.TRUE.equals(this.validateName(name))) {
      throw new DuplicateCompanyNameException("Company name already exists.");
    }
    if (Boolean.TRUE.equals(this.validateSiret(siret))) {
      throw new DuplicateCompanySiretException("Company siret already exists.");
    }
  }

  /**
   * to save a company.
   *
   * @param dto companyDTO object @Save CompanyDetailDTO in signature-corporate-profile
   * @return the companyDTO Object
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public CompanyDto save(CompanyDto dto) {
    this.validateNameSiret(dto.getName(), dto.getSiret());
    var company = this.mapEntity(dto);
    company.setCreatedBy(this.getUserId());
    company.setLogo(dto.getLogo());
    var response = this.companyRepository.save(company);

    // Create a default setting with SIMPLE signature level
    var companySetting =
        this.modelMapper.map(
            CompanySettingDto.builder()
                .companyUuid(response.getUuid())
                .signatureLevel(SignatureSettingLevel.SIMPLE.name())
                .channelReminder(NotificationConstant.EMAIL)
                .build(),
            CompanySetting.class);
    companySetting.setCompany(response);
    companySetting.setCreatedBy(getUserId());
    this.companySettingRepository.save(companySetting);

    this.corporateProfileFeignClient.saveCompanySetting(
        Collections.singletonList(this.modelMapper.map(companySetting, CompanySettingDto.class)));

    // Save company record to companyDetail on Corporate-profile service
    this.detailFeignClient.saveCompanyDetails(
        CompanyDetailDTO.builder()
            .companyUuid(response.getUuid())
            .companyId(response.getId())
            .name(response.getName())
            .filename(response.getContactFirstName())
            .lastName(response.getContactLastName())
            .address(response.getAddressLine1())
            .userId(this.getUserId())
            .creationDate(new Date())
            .filename(dto.getLogo())
            .build());
    return this.mapData(response);
  }

  /**
   * to update a company.
   *
   * @param dto companyDTO object @EntityNotFoundException if the id is null or 0
   * @return the companyDTO Object
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public CompanyDto update(CompanyDto dto) {
    if (Boolean.TRUE.equals(this.findByNameAndNotEqualId(dto.getName(), dto.getId()))) {
      throw new DuplicateCompanyNameException("Company name already exists.");
    }
    if (Boolean.TRUE.equals(this.findBySiretAndNotEqualId(dto.getSiret(), dto.getId()))) {
      throw new DuplicateCompanySiretException("Company siret already exists.");
    }
    var company = this.findEntityById(dto.getId());
    this.modelMapper.map(dto, company);
    if (!StringUtils.hasText(company.getUuid())) {
      company.setUuid(UUID.randomUUID().toString());
    }
    company.setModifiedBy(super.getUserId());
    this.detailFeignClient.updateCompanyDetails(
        CompanyDetailDTO.builder()
            .companyUuid(company.getUuid())
            .companyId(company.getId())
            .name(company.getName())
            .filename(company.getContactFirstName())
            .lastName(company.getContactLastName())
            .address(company.getAddressLine1())
            .filename(company.getLogo())
            .build());
    return this.mapData(this.companyRepository.save(company));
  }

  /**
   * to find a company by its id.
   *
   * @param id companyDTO object. @EntityNotFoundException when cannot find the company with the
   *     given id.
   * @return the Company Entity.
   */
  private Company findEntityById(Long id) {
    return this.companyRepository
        .findById(id)
        .orElseThrow(() -> new EntityNotFoundException(id, "company"));
  }

  /**
   * to receive CompanyDto by its id.
   *
   * @param id to search for the record that has that id.
   * @return CompanyDto object
   */
  @Override
  @Transactional(readOnly = true)
  public CompanyDto findById(Long id) {
    return this.mapData(this.findEntityById(id));
  }

  /**
   * to receive CompanyDto by its name.
   *
   * @param name to search for the record that has that name.
   * @return CompanyDto object
   */
  @Transactional(readOnly = true)
  public CompanyDto findByName(String name) {
    var company = this.companyRepository.findOne(Specification.where(CompanySpec.findByName(name)));
    return company.map(this::mapData).orElse(null);
  }

  /**
   * Get company with its uuid.
   *
   * @param uuid company unique uuid
   * @return {@link CompanyDto}
   */
  @Transactional(readOnly = true)
  public CompanyDto findByUuid(String uuid) {
    return this.companyRepository
        .findOne(Specification.where(CompanySpec.findByUuid(uuid)))
        .map(super::mapData)
        .orElse(new CompanyDto());
  }

  /**
   * Get company by corporate id.
   *
   * @param corporateId refers to corporate id
   * @return {@link CompanyDto}
   */
  public CompanyDto findByCorporateId(Long corporateId) {
    var corporate = this.corporateUserService.findById(corporateId);
    var company = this.companyRepository.findById(corporate.getCompanyId());
    return company.map(this::mapData).orElse(null);
  }

  /**
   * to receive List of CompanyDTOs.
   *
   * @return a list of CompanyDTOs
   */
  @Override
  @Transactional(readOnly = true)
  public List<CompanyDto> findAll() {
    return this.companyRepository.findAll().stream().map(this::mapData).toList();
  }

  /**
   * to list all companies with pagination.
   *
   * @param pageable for pagination
   * @param search use for a search company
   * @return list of companies with pagination.
   */
  @Override
  @Transactional(readOnly = true)
  public Page<CompanyDto> findAll(Pageable pageable, String search) {
    if (pageable.isUnpaged()) {
      return new PageImpl<>(this.findAll());
    }
    var companies =
        this.companyRepository
            .findAll(Specification.where(CompanySpec.search(search)), pageable)
            .map(this::mapData);
    var companyIds = new CompanyIdListDTO();
    companyIds.setCompanyIds(companies.stream().map(CompanyDto::getId).toList());
    this.loopAndSetData(companyIds, companies.getContent());
    return companies;
  }

  /**
   * to list all companies.
   *
   * @return list of companies.
   */
  public List<CompanyDto> listAll() {
    List<CompanyDto> companyDtoList =
        this.mapAll(this.companyRepository.findAll(), CompanyDto.class);
    var companyIds = new CompanyIdListDTO();
    companyIds.setCompanyIds(companyDtoList.stream().map(CompanyDto::getId).toList());
    this.loopAndSetData(companyIds, companyDtoList);
    return companyDtoList;
  }

  /**
   * to upload a logo for company.
   *
   * @param logoFile refers to a file of the company's logo
   * @return the LogoResponse object
   */
  public LogoResponse uploadLogo(MultipartFile logoFile) {
    FileUtils.validateFileContentType(logoFile.getContentType(), "image/png");
    FileUtils.validateWidthHeights(logoFile);

    var logo = this.corporateProfileFeignClient.uploadLogo(logoFile);
    return new LogoResponse(logo);
  }

  /**
   * To loop and set data to companyDTO list.
   *
   * @param companyIds refers to company id list
   * @param companyDtoList refers to companyDTO list
   */
  public void loopAndSetData(CompanyIdListDTO companyIds, List<CompanyDto> companyDtoList) {
    var totalUsers = this.userService.listAllByCompany(companyIds.getCompanyIds());

    companyDtoList.forEach(
        companyDTO -> {
          var users =
              totalUsers.stream()
                  .filter(
                      userDto ->
                          Objects.equals(userDto.getCompanyId(), companyDTO.getId())
                              && RoleConstant.isCorporateUser(userDto.getRoles()))
                  .toList();
          companyDTO.setTotalEmployees(users.size());
        });
  }
}
