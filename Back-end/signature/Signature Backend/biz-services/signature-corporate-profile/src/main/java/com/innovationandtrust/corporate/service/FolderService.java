package com.innovationandtrust.corporate.service;

import com.innovationandtrust.corporate.exception.InvalidBusinessUnitException;
import com.innovationandtrust.corporate.model.dto.BusinessUnitDto;
import com.innovationandtrust.corporate.model.dto.BusinessUnitDtoListRes;
import com.innovationandtrust.corporate.model.dto.FolderDto;
import com.innovationandtrust.corporate.model.entity.Folder;
import com.innovationandtrust.corporate.repository.BusinessUnitRepository;
import com.innovationandtrust.corporate.repository.FolderRepository;
import com.innovationandtrust.corporate.service.restclient.ProfileFeignClient;
import com.innovationandtrust.corporate.service.specification.BusinessSpecification;
import com.innovationandtrust.corporate.service.specification.FolderSpecification;
import com.innovationandtrust.utils.chain.handler.BearerAuthentication;
import com.innovationandtrust.utils.exception.exceptions.EntityNotFoundException;
import com.innovationandtrust.utils.exception.exceptions.FolderNotFoundException;
import com.innovationandtrust.utils.keycloak.provider.IKeycloakProvider;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/** Folder services logic. */
@Slf4j
@Service
@Transactional
public class FolderService extends CommonCrudService<FolderDto, Folder, Long> {

  private final FolderRepository folderRepository;
  private final BusinessUnitRepository businessUnitRepository;
  private final ProfileFeignClient profileFeignClient;
  private final BusinessUnitService businessUnitService;

  protected FolderService(
      ModelMapper modelMapper,
      IKeycloakProvider keycloakProvider,
      FolderRepository folderRepository,
      BusinessUnitRepository businessUnitRepository,
      ProfileFeignClient profileFeignClient,
      BusinessUnitService businessUnitService) {
    super(modelMapper, keycloakProvider);
    this.folderRepository = folderRepository;
    this.businessUnitRepository = businessUnitRepository;
    this.profileFeignClient = profileFeignClient;
    this.businessUnitService = businessUnitService;
  }

  /**
   * Insert a folder record service.
   *
   * @param folderDto refers to the folderDTO object that use to insert
   * @return the inserted folderDTO
   */
  @Transactional(rollbackFor = Exception.class)
  public FolderDto save(FolderDto folderDto, HttpHeaders headers) {
    validate(folderDto, headers);
    folderDto.setCreatedBy(getUserId());
    folderDto.setCreatedAt(new Date());
    var folder = this.folderRepository.save(mapEntity(folderDto));
    return this.setBusinessUnits(this.mapData(folder));
  }

  /**
   * Update the folderDTO record.
   *
   * @param folderDto refers to folderDTO object that use to update
   * @return the updated FolderDto
   */
  @Transactional
  public FolderDto update(FolderDto folderDto, HttpHeaders headers) {
    validate(folderDto, headers);
    folderDto.setModifiedBy(getUserId());
    folderDto.setModifiedAt(new Date());
    var folder = this.folderRepository.save(mapEntity(folderDto));
    return this.setBusinessUnits(this.mapData(folder));
  }

  private void validate(FolderDto folderDto, HttpHeaders headers) {
    var businessUnit = this.businessUnitService.findEntityById(folderDto.getBusinessUnitId());
    if (!Objects.nonNull(businessUnit)) {
      throw new InvalidBusinessUnitException("Business unit not found...");
    }

    Long userId = this.getUserId();
    var token = BearerAuthentication.getTokenWithPrefix(headers);
    var user =
        this.profileFeignClient
            .findUserById(userId, token)
            .orElseThrow(() -> new EntityNotFoundException("User not found ..."));

    if (!Objects.equals(
        businessUnit.getCompanyDetail().getCompanyId(), user.getCompany().getId())) {
      throw new InvalidBusinessUnitException("Invalid business unit...");
    }
  }

  /**
   * Retrieves a folderDTO by its id.
   *
   * @param id must not be {@literal null}.
   * @return the folderDTO with the given id or {@literal Optional#empty()} if none found
   * @throws IllegalArgumentException if {@literal id} is {@literal null}
   */
  @Transactional(rollbackFor = Exception.class)
  public FolderDto findById(long id) {
    var folder =
        this.folderRepository
            .findById(id)
            .map(this::mapData)
            .orElseThrow(FolderNotFoundException::new);
    return setBusinessUnits(folder);
  }

  private FolderDto setBusinessUnits(FolderDto folder) {
    var businessUnit = this.businessUnitService.findById(folder.getBusinessUnitId());
    folder.setBusinessUnits(this.modelMapper.map(businessUnit, BusinessUnitDtoListRes.class));
    return folder;
  }

  /**
   * Retrieves a folderDTO by business unit id.
   *
   * @param id refers to the id of businessUnit
   * @return the list of FolderDto
   */
  @Transactional(rollbackFor = Exception.class)
  public List<FolderDto> findByBusinessId(long id) {
    return this.folderRepository.findByBusinessUnitId(id).stream().map(this::mapData).toList();
  }

  /**
   * Find FolderDto by company id.
   *
   * @param companyId refers to the id of the company
   * @return a list of folderDTO
   */
  public List<FolderDto> findByCompanyId(long companyId) {
    var businessUnits =
        this.businessUnitRepository.findAll(
            Specification.where(BusinessSpecification.findByCompanyId(companyId)));

    List<FolderDto> foldersOfCompany = new ArrayList<>();

    businessUnits.forEach(
        (businessUnit -> {
          foldersOfCompany.addAll(businessUnit.getFolders().stream().map(this::mapData).toList());

          foldersOfCompany.stream()
              .filter(
                  folderDto -> Objects.equals(folderDto.getBusinessUnitId(), businessUnit.getId()))
              .forEach(
                  folderDto ->
                      folderDto.setBusinessUnits(
                          modelMapper.map(businessUnit, BusinessUnitDtoListRes.class)));
        }));

    return foldersOfCompany;
  }

  /**
   * Find FolderDto by corporate id or createdBy id.
   *
   * @param userIds refers to the createdBy id
   * @return a list of folderDTO
   */
  public List<FolderDto> findByCorporateId(List<Long> userIds) {

    return this.folderRepository
        .findAll(Specification.where(FolderSpecification.findByCreatedBy(userIds)))
        .stream()
        .map(super::mapData)
        .toList();
  }

  /**
   * Find FolderDto for request user.
   *
   * @return a list of folderDTO
   */
  public Page<FolderDto> findByOwner(Pageable pageable, String search, HttpHeaders headers) {
    Long userId = this.getUserId();
    var token = BearerAuthentication.getTokenWithPrefix(headers);
    var user =
        this.profileFeignClient
            .findUserById(userId, token)
            .orElseThrow(() -> new EntityNotFoundException("User not found ..."));
    var spec =
        Specification.where(
            FolderSpecification.findByCreatedBy(List.of(userId, user.getCreatedBy())));
    if (StringUtils.hasText(search)) {
      spec = spec.and(FolderSpecification.search(search));
    }
    var folders = this.folderRepository.findAll(spec, pageable);
    var unitIds = folders.map(folder -> folder.getBusinessUnit().getId()).stream().toList();

    var businessUnits = this.businessUnitService.findByIds(unitIds);

    return folders.map(
        folder -> {
          var folderDto = this.mapData(folder);
          var currentBusinessUnit =
              businessUnits.stream()
                  .filter(b -> Objects.equals(b.getId(), folder.getBusinessUnit().getId()))
                  .findFirst()
                  .orElse(new BusinessUnitDto());
          folderDto.setBusinessUnits(
              this.modelMapper.map(currentBusinessUnit, BusinessUnitDtoListRes.class));
          return folderDto;
        });
  }

  public void updateBusinessId(Long userId, Long businessId) {
    this.folderRepository.updateBusinessId(userId, businessId);
  }
}
