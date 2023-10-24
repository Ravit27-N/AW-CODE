package com.innovationandtrust.profile.service;

import com.innovationandtrust.profile.model.dto.CorporateUserDto;
import com.innovationandtrust.profile.model.dto.UserDto;
import com.innovationandtrust.profile.model.entity.UserActivity_;
import com.innovationandtrust.profile.model.entity.User_;
import com.innovationandtrust.profile.repository.CompanyRepository;
import com.innovationandtrust.profile.repository.UserActivityRepository;
import com.innovationandtrust.profile.service.restclient.EmployeeFeignClient;
import com.innovationandtrust.profile.service.restclient.ProjectFeignClient;
import com.innovationandtrust.profile.service.restclient.SftpFeignClient;
import com.innovationandtrust.share.constant.RoleConstant;
import com.innovationandtrust.share.model.corporateprofile.EmployeeDTO;
import com.innovationandtrust.share.model.profile.CorporateUser;
import com.innovationandtrust.share.model.profile.NormalUser;
import com.innovationandtrust.utils.commons.AdvancedFilter;
import com.innovationandtrust.utils.commons.Filter;
import com.innovationandtrust.utils.commons.QueryOperator;
import com.innovationandtrust.utils.corporateprofile.feignclient.CorporateProfileFeignClient;
import com.innovationandtrust.utils.exception.exceptions.EntityNotFoundException;
import com.innovationandtrust.utils.exception.exceptions.InvalidRequestException;
import com.innovationandtrust.utils.exception.exceptions.KeycloakException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Slf4j
public class CorporateUserService {
  private final UserService userService;
  private final ModelMapper modelMapper;
  private final EmployeeFeignClient employeeFeignClient;
  private final SftpFeignClient sftpFeignClient;
  private final CompanyRepository companyRepository;
  private final MailService mailService;
  private final CorporateProfileFeignClient corporateProfileFeignClient;
  private final ProjectFeignClient projectFeignClient;
  private final UserActivityRepository activityRepository;

  public CorporateUserService(
      UserService userService,
      ModelMapper modelMapper,
      EmployeeFeignClient employeeFeignClient,
      SftpFeignClient sftpFeignClient,
      CompanyRepository companyRepository,
      MailService mailService,
      CorporateProfileFeignClient corporateProfileFeignClient,
      ProjectFeignClient projectFeignClient,
      UserActivityRepository activityRepository) {
    this.userService = userService;
    this.modelMapper = modelMapper;
    this.employeeFeignClient = employeeFeignClient;
    this.sftpFeignClient = sftpFeignClient;
    this.companyRepository = companyRepository;
    this.mailService = mailService;
    this.corporateProfileFeignClient = corporateProfileFeignClient;
    this.projectFeignClient = projectFeignClient;
    this.activityRepository = activityRepository;
  }

  /**
   * To create a new user with a role as corporate admin.
   *
   * @param dto refers to the object of {@link CorporateUserDto}
   * @return object of {@link CorporateUserDto} after creation
   */
  @Transactional(rollbackFor = Exception.class)
  public CorporateUserDto save(CorporateUserDto dto) {
    this.userService.validateUserPhoneAndMail(dto.getEmail(), dto.getPhone());

    try {
      var user = this.modelMapper.map(dto, UserDto.class);
      var company = this.companyRepository.findById(user.getCompanyId());
      if (company.isEmpty()) {
        throw new EntityNotFoundException("Company", "id");
      }
      user.setRoles(Collections.singleton(RoleConstant.CORPORATE_ADMIN));
      var response = this.userService.save(user);

      // Save user record to employee on Corporate-profile service
      var savedEmployee =
          this.employeeFeignClient.saveEmployee(
              EmployeeDTO.builder()
                  .firstName(response.getFirstName())
                  .lastName(response.getLastName())
                  .userId(response.getId())
                  .businessUnitId(dto.getBusinessId())
                  .userAccessId(dto.getUserAccessId())
                  .build());

      // Create corporate folder IN
      this.sftpFeignClient.createCorporateFolder(String.valueOf(response.getUserEntityId()));
      // send invitation email
      this.activityRepository
          .findOne(
              AdvancedFilter.searchByField(
                  Filter.builder()
                      .referenceField(Arrays.asList(UserActivity_.USER, User_.ID))
                      .operator(QueryOperator.EQUALS)
                      .value(String.valueOf(response.getId()))
                      .build()))
          .ifPresent(
              userActivity -> {
                log.info("Corporate invitation mail sending...");
                var theme = this.corporateProfileFeignClient.getLogo(response.getCreatedBy());
                this.mailService.endUserInvitation(
                    response.getFullName(),
                    response.getEmail(),
                    dto.getPassword(),
                    theme,
                    userActivity.getToken());
                log.info("Send invitation mail successfully...");
              });

      return this.mapFieldInCorporate(response, savedEmployee);
    } catch (Exception e) {
      log.error("Failed to create corporate user", e);
      throw new KeycloakException("Failed to create corporate user...");
    }
  }

  /**
   * To update a corporate user.
   *
   * @param dto refers to the object of {@link CorporateUserDto}
   * @return object of {@link CorporateUserDto} after updating
   */
  @Transactional(rollbackFor = Exception.class)
  public CorporateUserDto update(CorporateUserDto dto) {
    if (Objects.nonNull(dto.getPhone()) && userService.isPhoneExist(dto.getId(), dto.getPhone())) {
      throw new InvalidRequestException(UserService.PHONE_EXIST);
    } else if (Objects.nonNull(dto.getEmail())
        && userService.isEmailExist(dto.getId(), dto.getEmail())) {
      throw new InvalidRequestException(UserService.MAIL_EXIST);
    }

    var user = this.userService.findById(userService.getUserId());

    if (RoleConstant.isCorporateUser(user.getRoles())) {
      log.info("Validate updating corporate user request is in the same company... ");
      var userInfo =
          this.userService
              .getUserInfo()
              .orElseThrow(
                  () -> new KeycloakException("Unable to get user info from keycloak ..."));
      this.userService.checkUserCompany(userInfo.getSystemUser().getCompany().getId(), dto.getId());
    }

    var updatedUser = this.userService.update(this.modelMapper.map(dto, UserDto.class));
    var updatedEmployee =
        this.employeeFeignClient.updateEmployee(
            EmployeeDTO.builder()
                .firstName(updatedUser.getFirstName())
                .lastName(updatedUser.getLastName())
                .userId(updatedUser.getId())
                .businessUnitId(dto.getBusinessId())
                .userAccessId(dto.getUserAccessId())
                .build());
    return this.mapFieldInCorporate(updatedUser, updatedEmployee);
  }

  /**
   * To find a corporate user by id.
   *
   * @param id refers to corporate user's id
   * @return object of {@link CorporateUserDto}
   */
  public CorporateUserDto findById(Long id) {
    var user = this.userService.findById(id);
    var employee = this.employeeFeignClient.findByUserId(id);
    return this.mapFieldInCorporate(user, employee);
  }

  /**
   * To find corporate user info.
   *
   * @return object of {@link CorporateUserDto}
   */
  public CorporateUserDto findAuthor() {
    var userId = this.userService.getUserId();
    return this.mapFieldInCorporate(
        this.userService.findById(userId), this.employeeFeignClient.findByUserId(userId));
  }

  /**
   * To find corporate user by keycloak id (internal).
   *
   * @param uuid refers to corporate user entity id.
   * @param userId refers to normal user entity id.
   * @return object of {@link CorporateUserDto}
   */
  public CorporateUser findByUserEntityId(String uuid, String userId) {
    var corporateUser =
        this.modelMapper.map(this.userService.findByUserEntityId(uuid), CorporateUser.class);
    if (StringUtils.hasText(userId) && Objects.nonNull(corporateUser)) {
      corporateUser.setNormalUser(
          this.modelMapper.map(this.userService.findByUserEntityId(userId), NormalUser.class));
    }
    return corporateUser;
  }

  /**
   * To find all corporate users by company id.
   *
   * @param uuid refers to corporate user's id
   * @param pageable refers to pagination
   * @param search refers to search string
   * @return object of {@link CorporateUserDto}
   */
  public Page<CorporateUserDto> findByCompany(Pageable pageable, String search, String uuid) {
    var corporateUsers =
        this.userService
            .findByCompany(pageable, search, uuid, RoleConstant.CORPORATE_ADMIN)
            .map(data -> this.modelMapper.map(data, CorporateUserDto.class));

    this.userService.userEmployee(corporateUsers.getContent(), this.employeeFeignClient);
    return corporateUsers;
  }

  public List<CorporateUserDto> findAll() {
    var corporateUsers =
        this.userService.findByRole(RoleConstant.CORPORATE_ADMIN).stream()
            .map(data -> this.modelMapper.map(data, CorporateUserDto.class))
            .toList();
    return this.userService.userEmployee(corporateUsers, this.employeeFeignClient);
  }

  public void deleteUser(Long id, Long assignTo) {
    var userId = this.userService.getUserId();
    var user = this.userService.findById(id);

    if (Objects.nonNull(assignTo)) {
      this.userService.findById(assignTo);
      userId = assignTo;
    }

    log.info("Calling project service to assign projects to another corporate user {} ...", userId);
    this.projectFeignClient.assignProjects(id, userId);

    user.setDeleted(true);
    user.setActive(false);

    // Update user: deleted
    this.userService.updateDeleted(user);

    // Update employee: deleted
    this.employeeFeignClient.deleteUser(id);

    // Update keycloak: disable user login
    this.userService.setUserActive(user.getUserEntityId().toString(), user.getActive());
  }

  public CorporateUserDto active(Long id, Boolean active) {
    var user = this.userService.findById(id);
    user.setActive(active);
    var updatedUser = this.userService.updateStatus(this.modelMapper.map(user, UserDto.class));
    var employee = this.employeeFeignClient.findByUserId(updatedUser.getId());

    return this.mapFieldInCorporate(updatedUser, employee);
  }

  /**
   * Map field in corporate user dto.
   *
   * @param userDto refers to userDto that use to map to corporateUserDto
   * @param employeeDTO refers to employee data that belongs to that user
   * @return an object of CorporateUserDto.
   */
  private CorporateUserDto mapFieldInCorporate(UserDto userDto, EmployeeDTO employeeDTO) {
    CorporateUserDto corporateUserDto = this.modelMapper.map(userDto, CorporateUserDto.class);
    corporateUserDto.setUserAccess(employeeDTO.getUserAccess());
    corporateUserDto.setBusinessUnit(employeeDTO.getDepartment());

    return corporateUserDto;
  }
}
