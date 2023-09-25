package com.tessi.cxm.pfl.ms5.service;

import com.google.common.base.Strings;
import com.tessi.cxm.pfl.shared.model.AddressDto;
import com.tessi.cxm.pfl.ms5.dto.DepartmentDto;
import com.tessi.cxm.pfl.ms5.dto.DivisionDto;
import com.tessi.cxm.pfl.ms5.dto.LoadOrganizationUserImpl;
import com.tessi.cxm.pfl.ms5.entity.Department;
import com.tessi.cxm.pfl.ms5.entity.Division;
import com.tessi.cxm.pfl.ms5.entity.projection.LoadUserPrivilegeDetails;
import com.tessi.cxm.pfl.ms5.exception.DepartmentConflictNameException;
import com.tessi.cxm.pfl.ms5.exception.DepartmentNotFoundException;
import com.tessi.cxm.pfl.ms5.exception.UserNotFoundException;
import com.tessi.cxm.pfl.ms5.repository.DepartmentRepository;
import com.tessi.cxm.pfl.ms5.repository.DivisionRepository;
import com.tessi.cxm.pfl.ms5.repository.UserRepository;
import com.tessi.cxm.pfl.ms5.service.specification.DepartmentSpecification;
import com.tessi.cxm.pfl.ms5.service.specification.DivisionSpecification;
import com.tessi.cxm.pfl.shared.model.DepartmentProjection;
import com.tessi.cxm.pfl.shared.service.AbstractCrudService;
import com.tessi.cxm.pfl.shared.service.keycloak.KeycloakService;
import com.tessi.cxm.pfl.shared.service.restclient.ModificationLevel;
import com.tessi.cxm.pfl.shared.service.restclient.VisibilityLevel;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DepartmentService extends AbstractCrudService<DepartmentDto, Department, Long>
    implements AdminService {
  private final DepartmentRepository departmentRepository;
  private final DivisionRepository divisionRepository;
  private final DivisionService divisionService;
  private final UserRepository userRepository;
  private final ProfileService profileService;

  @Value("${cxm.user.admin-id}")
  private String adminUserId;

  public DepartmentService(
      DepartmentRepository departmentRepository, DivisionRepository divisionRepository,
      ModelMapper modelMapper,
      DivisionService divisionService,
      UserRepository userRepository,
      ProfileService profileService) {
    this.departmentRepository = departmentRepository;
    this.divisionRepository = divisionRepository;
    this.divisionService = divisionService;
    this.userRepository = userRepository;
    this.profileService = profileService;
    this.modelMapper = modelMapper;
  }

  @Override
  public UserRepository getUserRepository() {
    return this.userRepository;
  }

  @Transactional(readOnly = true)
  public Department findEntity(long id) {
    return this.departmentRepository
        .findById(id)
        .orElseThrow(() -> new DepartmentNotFoundException(id));
  }

  @Override
  public DepartmentDto findById(Long id) {
    return mapData(this.findEntity(id), new DepartmentDto());
  }

  @Transactional(readOnly = true)
  @Override
  public List<DepartmentDto> findAll() {
    return this.departmentRepository.findAll().stream()
        .map(e -> this.mapData(e, new DepartmentDto()))
        .collect(Collectors.toList());
  }

  /**
   * Method used to get division by id.
   *
   * @param divisionId the identity of {@link Division}
   * @return object of {@link Division}
   */
  private Division getDivision(Long divisionId) {
    return this.divisionService.findEntity(divisionId);
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public DepartmentDto save(DepartmentDto dto) {
    if (isDuplicateNameInDivision(dto.getName(), dto.getId(), dto.getDivisionId())) {
      throw new DepartmentConflictNameException();
    }
    var service = mapEntity(dto, new Department());
    service.setDivision(this.getDivision(dto.getDivisionId()));
    return mapData(this.departmentRepository.save(service), new DepartmentDto());
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public DepartmentDto update(DepartmentDto dto) {
    this.getDivision(dto.getDivisionId());

    if (isDuplicateNameInDivision(dto.getName(), dto.getId(), dto.getDivisionId())) {
      throw new DepartmentConflictNameException();
    }

    return mapData(this.departmentRepository.save(mapEntity(dto)));
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public void delete(Long id) {
    this.departmentRepository.delete(this.findEntity(id));
  }

  /**
   * Get all user in this service.
   *
   * @param id is identity of service
   * @return values of user found
   */
  @Transactional(readOnly = true)
  public List<String> getUsersInService(long id) {
    if (!this.departmentRepository.existsById(id)) {
      throw new DepartmentNotFoundException(id);
    }
    return this.userRepository.getAllUsersInService(id).stream().map(LoadUserPrivilegeDetails::getUsername)
        .collect(Collectors.toList());
  }

  @Autowired
  @Override
  public void setKeycloakService(KeycloakService keycloakService) {
    super.setKeycloakService(keycloakService);
  }

  @Transactional(readOnly = true)
  public List<DepartmentProjection> getServicesByUser(
      boolean isVisibilityLevel, String functionalKey, String privilegeKey) {
    final var level = this.profileService.findTopPrivilegeLevelOfUser(
        isVisibilityLevel, functionalKey, privilegeKey);
    var organization = this.userRepository
        .loadOrganizationUser(keycloakService.getUserInfo().getId())
        .orElse(new LoadOrganizationUserImpl());
    if (Strings.isNullOrEmpty(level)
        || level.equals(ModificationLevel.OWNER.getKey())
        || level.equals(VisibilityLevel.USER.getKey())) {
      return departmentRepository.findAllById(organization.getServiceId());
    }
    switch (VisibilityLevel.valueByKey(level)) {
      case CLIENT:
        return departmentRepository.findAllByDivisionInClientId(organization.getClientId());
      case DIVISION:
        return departmentRepository.findAllByDivisionId(organization.getDivisionId());
      case SERVICE:
        return departmentRepository.findAllById(organization.getServiceId());
      default:
        return new ArrayList<>();
    }
  }

  /**
   * To validate duplicate service's name in division.
   *
   * @param name       refer to name of {@link Department}
   * @param serviceId  identify of {@link Department}
   * @param divisionId identify of {@link Division}
   * @return true if duplicate
   */
  public boolean isDuplicateNameInDivision(String name, long serviceId, long divisionId) {
    if (serviceId != 0) {
      var entity = this.findEntity(serviceId);
      if (entity.getName().equalsIgnoreCase(name)
          && entity.getId() == serviceId
          && entity.getDivision().getId() == divisionId) {
        return false;
      }
    }
    return departmentRepository.existsAllByNameIgnoreCaseAndDivisionId(name, divisionId);
  }

  @Override
  public String getConfiguredUserAdminId() {
    return this.adminUserId;
  }

  public List<Long> getAllServicesInClient() {
    final String userId = this.keycloakService.getUserInfo().getId();
    final var user = this.userRepository.findByTechnicalRefAndIsActiveTrue(this.keycloakService.getUserInfo().getId())
        .orElseThrow(() -> new UserNotFoundException(userId));
    final var specification = DepartmentSpecification.clientEqual(
        user.getDepartment().getDivision().getClient().getId());
    return this.departmentRepository.findAll(specification).stream().map(Department::getId).collect(
        Collectors.toList());
  }

  // add new
  public List<DepartmentDto> getAllServicesInClientList() {
    final String userId = this.keycloakService.getUserInfo().getId();
    final var user = this.userRepository.findByTechnicalRefAndIsActiveTrue(userId)
        .orElseThrow(() -> new UserNotFoundException(userId));
    final var specification = DepartmentSpecification.clientEqual(
        user.getDepartment().getDivision().getClient().getId());
    return this.departmentRepository.findAll(specification).stream()
        .map(
            department -> new DepartmentDto(department.getId(), department.getName(), department.getDivision().getId(),new AddressDto()))
        .collect(Collectors.toList());
  }

  // add for division list
  public List<DivisionDto> getAllDivisionInClientList() {
    final String userId = this.keycloakService.getUserInfo().getId();
    final var user = this.userRepository.findByTechnicalRefAndIsActiveTrue(userId)
        .orElseThrow(() -> new UserNotFoundException(userId));
    final var specification = DivisionSpecification.equalClientId(
        user.getDepartment().getDivision().getClient().getId());
    return this.divisionRepository.findAll(specification).stream()
        .map(
            division -> new DivisionDto(division.getId(), division.getName()))
        .collect(Collectors.toList());
  }
}
