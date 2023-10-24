package com.innovationandtrust.profile.service;

import static com.innovationandtrust.profile.service.spefication.UserSpec.findByRole;

import com.innovationandtrust.profile.exception.InvalidedPasswordException;
import com.innovationandtrust.profile.exception.PasswordNotMatchException;
import com.innovationandtrust.profile.model.dto.AbstractUser;
import com.innovationandtrust.profile.model.dto.BusinessUnitInfo;
import com.innovationandtrust.profile.model.dto.CompanyInfo;
import com.innovationandtrust.profile.model.dto.NormalUserDto;
import com.innovationandtrust.profile.model.dto.UserDto;
import com.innovationandtrust.profile.model.entity.User;
import com.innovationandtrust.profile.model.entity.UserActivity;
import com.innovationandtrust.profile.model.entity.UserActivity_;
import com.innovationandtrust.profile.model.entity.User_;
import com.innovationandtrust.profile.repository.UserActivityRepository;
import com.innovationandtrust.profile.repository.UserRepository;
import com.innovationandtrust.profile.service.restclient.BusinessUnitsFeignClient;
import com.innovationandtrust.profile.service.restclient.EmployeeFeignClient;
import com.innovationandtrust.profile.service.restclient.ProjectFeignClient;
import com.innovationandtrust.profile.service.spefication.UserActivitySpec;
import com.innovationandtrust.share.constant.RoleConstant;
import com.innovationandtrust.share.model.corporateprofile.EmployeeDTO;
import com.innovationandtrust.share.model.corporateprofile.EmployeeResponseDto;
import com.innovationandtrust.share.model.profile.UserCompany;
import com.innovationandtrust.utils.authenticationUtils.AuthenticationUtils;
import com.innovationandtrust.utils.commons.AdvancedFilter;
import com.innovationandtrust.utils.commons.Filter;
import com.innovationandtrust.utils.commons.QueryOperator;
import com.innovationandtrust.utils.corporateprofile.feignclient.CorporateProfileFeignClient;
import com.innovationandtrust.utils.exception.exceptions.InvalidRequestException;
import com.innovationandtrust.utils.exception.exceptions.InvalidRoleException;
import com.innovationandtrust.utils.exception.exceptions.KeycloakException;
import com.innovationandtrust.utils.keycloak.model.ResetPasswordRequest;
import com.innovationandtrust.utils.keycloak.provider.IKeycloakProvider;
import jakarta.persistence.EntityNotFoundException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** End -user business logic. */
@Service
@Slf4j
public class NormalUserService {
  private final UserService userService;
  private final ModelMapper modelMapper;
  private final EmployeeFeignClient employeeFeignClient;
  private final CorporateProfileFeignClient corporateProfileFeignClient;
  private final BusinessUnitsFeignClient businessUnitsFeignClient;
  private final CompanyService companyService;
  private final MailService mailService;
  private final IKeycloakProvider keycloakProvider;
  private final ProjectFeignClient projectFeignClient;
  private final UserRepository userRepository;
  private final UserActivityRepository activityRepository;
  private final TemplateService templateService;

  /** End-User service dependencies injection. */
  public NormalUserService(
      UserService userService,
      ModelMapper modelMapper,
      EmployeeFeignClient employeeFeignClient,
      CorporateProfileFeignClient corporateProfileFeignClient,
      BusinessUnitsFeignClient businessUnitsFeignClient,
      CompanyService companyService,
      MailService mailService,
      ProjectFeignClient projectFeignClient,
      IKeycloakProvider keycloakProvider,
      UserRepository userRepository,
      UserActivityRepository activityRepository,
      TemplateService templateService) {
    this.userService = userService;
    this.modelMapper = modelMapper;
    this.employeeFeignClient = employeeFeignClient;
    this.corporateProfileFeignClient = corporateProfileFeignClient;
    this.businessUnitsFeignClient = businessUnitsFeignClient;
    this.companyService = companyService;
    this.mailService = mailService;
    this.projectFeignClient = projectFeignClient;
    this.keycloakProvider = keycloakProvider;
    this.userRepository = userRepository;
    this.activityRepository = activityRepository;
    this.templateService = templateService;
  }

  /**
   * To validate user.
   *
   * @param uuid refers the user entity id {@link NormalUserDto}
   * @return String of status code
   */
  public ResponseEntity<String> validate(String uuid) {
    var user = this.findByUuid(uuid);
    if (!Objects.equals(user.getUserEntityId(), this.userService.getUserUuid())) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized!");
    }
    return ResponseEntity.status(HttpStatus.OK).body("OK!");
  }

  /**
   * To find user by uuid.
   *
   * @param uuid refers the user entity id {@link NormalUserDto}
   * @return an Object of user.
   */
  private User findByUuid(String uuid) {
    return this.userRepository
        .findByUserEntityIdAndDeleted(uuid, false)
        .orElseThrow(() -> new EntityNotFoundException("User with this uuid is not found!"));
  }

  /**
   * To create a user with role as normal user.
   *
   * @param dto refers to an object of {@link NormalUserDto}
   * @return object of {@link NormalUserDto} after created
   */
  @Transactional(rollbackFor = Exception.class)
  public NormalUserDto save(NormalUserDto dto) {
    this.userService.validateUserPhoneAndMail(dto.getEmail(), dto.getPhone());

    try {
      var user = this.modelMapper.map(dto, UserDto.class);
      if (dto.getCreatedBy() != null) {
        var corporate = this.userService.findById(dto.getCreatedBy());
        user.setCompanyId(corporate.getCompanyId());
        user.setCorporateUuid(corporate.getUserEntityId().toString());
      } else {
        var userInfo =
            this.userService
                .getUserInfo()
                .orElseThrow(
                    () -> new IllegalArgumentException("Unable retrieve the company of the user"));
        user.setCompanyId(userInfo.getSystemUser().getCompany().getId());
        user.setCorporateUuid(
            AuthenticationUtils.getAuthenticatedUser(
                    SecurityContextHolder.getContext().getAuthentication())
                .uuid());
        user.setCreatedBy(this.userService.getUserId());
      }
      user.setRoles(Collections.singleton(RoleConstant.NORMAL_USER));

      var response = this.userService.save(user);

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
                var theme = this.corporateProfileFeignClient.getLogo(response.getCreatedBy());
                this.mailService.endUserInvitation(
                    response.getFullName(),
                    response.getEmail(),
                    dto.getPassword(),
                    theme,
                    userActivity.getToken());
              });

      // Save user record to employee on Corporate-profile service
      var emp = this.employeeFeignClient.saveEmployee(
              EmployeeDTO.builder()
                      .firstName(response.getFirstName())
                      .lastName(response.getLastName())
                      .userId(response.getId())
                      .businessUnitId(dto.getBusinessId())
                      .userAccessId(dto.getUserAccessId())
                      .functional(dto.getFunctional())
                      .percentage(0)
                      .build());

      return this.mapFieldInNormalUser(response, emp);
    } catch (Exception e) {
      log.error("Create end-user failed", e);
      throw new KeycloakException(
          "Create end-user failed. Root causes: Duplicate entry, Services unavailable"
              + e.getMessage());
    }
  }

  /**
   * All roles can update Normal User (END-USER).
   *
   * @param dto refers to the object of {@link NormalUserDto}
   * @return object of {@link NormalUserDto} after updating
   */
  @Transactional(rollbackFor = Exception.class)
  public NormalUserDto update(NormalUserDto dto) {
    if (Objects.nonNull(dto.getPhone()) && userService.isPhoneExist(dto.getId(), dto.getPhone())) {
      throw new InvalidRequestException(UserService.PHONE_EXIST);
    }

    var user = this.userService.findById(userService.getUserId());

    boolean isPrivilegeUser = RoleConstant.isPrivilegeUser(user.getRoles());
    if (isPrivilegeUser) {
      log.info("Updating End-User by {} roles:{}", user.getFullName(), user.getRoles());
      user = this.userService.findById(dto.getId());

      var email = dto.getEmail();
      if (Objects.nonNull(dto.getEmail()) && this.userService.isEmailExist(user.getId(), email)) {
        throw new InvalidRequestException(UserService.MAIL_EXIST);
      }

      if (!Objects.equals(user.getEmail(), email)) {
        log.info("Sending mail for mail changing confirmation...");
        var theme = this.corporateProfileFeignClient.getLogo(user.getCreatedBy());
        UserActivity activity =
            UserActivity.builder()
                .user(this.modelMapper.map(user, User.class))
                .pendingMail(email)
                .currentMail(user.getEmail())
                .token(UUID.randomUUID().toString())
                .expireTime(DateUtils.addDays(new Date(), 2))
                .build();
        activity.setCreatedBy(this.userService.getUserId());

        this.disableActivePending(user.getId());

        user.setActive(false);
        user.setEmail(email);
        this.activityRepository.save(activity);
        this.mailService.confirmChangeMail(user.getFullName(), email, theme, activity.getToken());
        this.userService.updateStatus(user);
        this.userService.updateUserKeycloakEmail(this.modelMapper.map(user, User.class));
      }
    } else {
      // Prevent normal user update current email
      dto.setEmail(user.getEmail());
    }

    log.info("Updating End-User {} with id:{}", user.getFullName(), user.getId());

    // Prevent user company null [old data]
    if (Objects.nonNull(user.getCreatedBy())
        && (!Objects.nonNull(user.getCompanyId()) || user.getCompanyId() == 0)) {
      var cp = this.userService.findById(user.getCreatedBy());
      user.setCompanyId(cp.getCompanyId());
      user.setCorporateUuid(cp.getUserEntityId().toString());
    }

    this.modelMapper.map(dto, user);
    user.setCorporateUuid(
        AuthenticationUtils.getAuthenticatedUser(
                SecurityContextHolder.getContext().getAuthentication())
            .uuid());

    var updatedEmployee =
        this.employeeFeignClient.updateEmployee(
            EmployeeDTO.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .userId(user.getId())
                .functional(user.getFunctional())
                .userAccessId(dto.getUserAccessId())
                .businessUnitId(isPrivilegeUser ? dto.getBusinessId() : null)
                .percentage(0)
                .build());

    if (isPrivilegeUser) {
      this.templateService.updateBusinessId(user.getId(), dto.getBusinessId());
    }

    var updatedUser = this.userService.update(user);

    return this.mapFieldInNormalUser(updatedUser, updatedEmployee);
  }

  /** To prevent corporate admin change multiple end_user mail */
  private void disableActivePending(Long userId) {
    Specification<UserActivity> spec =
        AdvancedFilter.searchByFields(
            Arrays.asList(
                Filter.builder()
                    .referenceField(Arrays.asList(UserActivity_.USER, User_.ID))
                    .operator(QueryOperator.EQUALS)
                    .value(String.valueOf(userId))
                    .build(),
                Filter.builder()
                    .field(UserActivity_.ACTIONED)
                    .operator(QueryOperator.IS_FALSE)
                    .build()));

    var foundActivities =
        this.activityRepository.findAll(spec.and(UserActivitySpec.greaterThanDate(new Date())));

    if (!foundActivities.isEmpty()) {
      foundActivities.forEach(
          activity -> {
            activity.setActioned(true);
            activity.setModifiedBy(userService.getUserId());
          });
      this.activityRepository.saveAll(foundActivities);
    }
  }

  /**
   * To find all users with pagination.
   *
   * @param search use for search records of user.
   * @param pageable use for pagination.
   * @return Page of {@link NormalUserDto}
   */
  public Page<NormalUserDto> findAll(Pageable pageable, String search) {
    var normalUsers =
        this.userService
            .findAll(pageable, search)
            .map(data -> this.modelMapper.map(data, NormalUserDto.class));

    var userIds = normalUsers.stream().map(AbstractUser::getId).toList();
    List<EmployeeResponseDto> employeeResponseDtoList =
        this.employeeFeignClient.listEmployeesByUserIds(userIds);

    normalUsers.forEach(
        normalUserDto ->
            employeeResponseDtoList.stream()
                .filter(emp -> Objects.equals(normalUserDto.getId(), emp.getUserId()))
                .findFirst()
                .ifPresent(
                    emp -> {
                      normalUserDto.setUserAccess(emp.getUserAccess());
                      normalUserDto.setBusinessUnit(emp.getBusinessUnit());
                    }));

    return normalUsers;
  }

  public List<NormalUserDto> findAll() {
    var normalUsers =
        this.userService.findByRole(RoleConstant.NORMAL_USER).stream()
            .map(user -> this.modelMapper.map(user, NormalUserDto.class))
            .toList();
    return this.userService.userEmployee(normalUsers, this.employeeFeignClient);
  }

  /**
   * To find a user by its id.
   *
   * @param id refer to user id.
   * @return a record of {@link NormalUserDto} that has that id.
   */
  public NormalUserDto findByIdDeletedFalse(Long id) {
    var user = this.userService.findById(id);
    return this.mapFieldInNormalUser(user, this.findEmployeeByUserId(user.getId()));
  }

  public Optional<NormalUserDto> findByIdOptional(Long id) {
    return Optional.ofNullable(
        this.modelMapper.map(this.userService.findByIdOptional(id), NormalUserDto.class));
  }

  public UserCompany findUserCompanyById(Long id) {
    return this.modelMapper.map(this.userService.findById(id), UserCompany.class);
  }

  /**
   * To find a user by its id.
   *
   * @return a record of {@link NormalUserDto} that has that id.
   */
  public NormalUserDto findById() {
    var user = this.userService.findById(userService.getUserId());
    var normalUser = this.mapFieldInNormalUser(user, this.findEmployeeByUserId(user.getId()));
    var businessUnit = this.businessUnitsFeignClient.findByUserId(user.getId());
    if (Objects.nonNull(businessUnit)) {
      normalUser.setBusinessUnitInfo(this.modelMapper.map(businessUnit, BusinessUnitInfo.class));
    }
    return normalUser;
  }

  /**
   * To find a user with company by id.
   *
   * @param id refer to user id.
   * @return a record of {@link NormalUserDto} that has that id with the company info.
   */
  @Transactional(readOnly = true)
  public NormalUserDto findWithCompanyById(Long id) {
    var user = this.userService.findById(id);
    var company =
        this.modelMapper.map(this.companyService.findById(user.getCompanyId()), CompanyInfo.class);
    var userDto = this.modelMapper.map(user, NormalUserDto.class);
    userDto.setCompany(company);
    return userDto;
  }

  public List<Long> findUserInTheSameCompany(Long id) {
    return this.userService.findAllInCompany(id).stream().map(UserDto::getId).toList();
  }

  /**
   * To update end-user's status.
   *
   * @param id refer to user id
   * @param active refer to user's status could be true or false
   * @return a record of {@link NormalUserDto} that has the given id
   */
  public NormalUserDto isActive(Long id, Boolean active) {
    var user = this.findByIdDeletedFalse(id);
    user.setActive(active);
    var updatedUser = this.userService.updateStatus(this.modelMapper.map(user, UserDto.class));
    return this.mapFieldInNormalUser(updatedUser, this.findEmployeeByUserId(updatedUser.getId()));
  }

  public NormalUserDto findByUserEntityId(String id) {
    var user =
        this.userService
            .findByUserEntityId(id)
            .orElseThrow(() -> new EntityNotFoundException("User with uuid is not found!"));
    return this.mapFieldInNormalUser(user, this.findEmployeeByUserId(user.getId()));
  }

  public boolean isEmailExist(String email) {
    return this.userService.isUserExist(email);
  }

  public boolean isPhoneExist(String phone) {
    return this.userService.isUserPhoneExist(phone);
  }

  public boolean isValidUser(String email) {
    return this.userService.isValidUser(email);
  }

  /**
   * To get users with list of user id.
   *
   * @param ids refers to list of user id
   * @return list of {@link NormalUserDto}
   */
  public List<NormalUserDto> findUsers(List<String> ids) {
    return this.userService.findUsers(ids).stream()
        .map(data -> this.modelMapper.map(data, NormalUserDto.class))
        .toList();
  }

  /**
   * To update user password. Update by him/her self.
   *
   * @param request refers to object of password to reset
   */
  public void resetPassword(ResetPasswordRequest request) {
    var user = this.findByIdDeletedFalse(this.userService.getUserId());
    if (Objects.nonNull(user)) {
      if (this.keycloakProvider.isValidPassword(user.getEmail(), request.getCurrentPassword())) {
        if (Objects.equals(request.getNewPassword(), request.getConfirmPassword())) {
          request.setId(user.getUserEntityId().toString());
          this.keycloakProvider.resetPassword(request);
          this.mailService.sendResetPasswordSuccessfullyTemplate(
              user.getFirstName(), user.getEmail());
        } else {
          throw new PasswordNotMatchException("Confirm password does not match.");
        }
      } else {
        throw new InvalidedPasswordException("Current password is invalid.");
      }
    }
  }

  /**
   * To delete end-user (Soft-Delete).
   *
   * @param id of user to delete
   * @param assignTo is user id of an end user. Him/Her will see projects assigned to him/her
   */
  @Transactional(rollbackFor = Exception.class)
  public void deleteUser(Long id, Long assignTo) {
    var userId = this.userService.getUserId();
    var user = this.userService.findById(userId);
    if (user.getRoles().contains(RoleConstant.CORPORATE_ADMIN)) {
      if (Objects.nonNull(assignTo)) {
        this.userService.findById(assignTo);
        userId = assignTo;
      }

      log.info("Calling project service to assign projects to user {} ...", userId);
      this.projectFeignClient.assignProjects(id, userId);

      user = this.userService.findById(id);
      user.setDeleted(true);
      user.setActive(false);

      log.info("Updating user...");
      this.userService.updateDeleted(user);

      // Update employee: deleted
      log.info("Updating employee...");
      this.employeeFeignClient.deleteUser(id);

      // Update keycloak: disable user login
      log.info("Updating user login...");
      this.userService.setUserActive(user.getUserEntityId().toString(), user.getActive());
    } else {
      log.info(
          "This user: {} with roles: {} has no privilege to do this action.",
          user.getId(),
          user.getRoles());
      throw new InvalidRoleException("Invalid privilege.");
    }
  }

  /**
   * To get employee by role.
   *
   * @param userIds refers to employee ids
   */
  public List<Long> getUserIds(List<Long> userIds, String role) {
    return this.userRepository.getUserByRole(role, userIds);
  }

  private NormalUserDto mapFieldInNormalUser(UserDto userDto, EmployeeDTO employeeDTO) {
    NormalUserDto normalUserDto = this.modelMapper.map(userDto, NormalUserDto.class);
    normalUserDto.setUserAccess(employeeDTO.getUserAccess());
    normalUserDto.setBusinessUnit(employeeDTO.getDepartment());

    return normalUserDto;
  }

  private EmployeeDTO findEmployeeByUserId(Long id) {
    return this.employeeFeignClient.findByUserId(id);
  }

  public Optional<User> getAnActiveUserByRole(Long companyId, String role) {
    var user = this.userRepository.findAll(findByRole(companyId, role), PageRequest.of(0, 1));
    return user.get().findFirst();
  }

  public NormalUserDto findById(Long id) {
    return this.modelMapper.map(this.userService.findByIdNoFiltering(id), NormalUserDto.class);
  }
}
