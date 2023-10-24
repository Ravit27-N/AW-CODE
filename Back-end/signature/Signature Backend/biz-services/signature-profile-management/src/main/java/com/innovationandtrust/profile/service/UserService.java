package com.innovationandtrust.profile.service;

import com.innovationandtrust.profile.exception.UserNotFoundException;
import com.innovationandtrust.profile.model.dto.AbstractUser;
import com.innovationandtrust.profile.model.dto.RoleDto;
import com.innovationandtrust.profile.model.dto.UserDto;
import com.innovationandtrust.profile.model.dto.UserEmployee;
import com.innovationandtrust.profile.model.entity.AbstractEntity_;
import com.innovationandtrust.profile.model.entity.Role;
import com.innovationandtrust.profile.model.entity.User;
import com.innovationandtrust.profile.model.entity.UserActivity;
import com.innovationandtrust.profile.model.entity.User_;
import com.innovationandtrust.profile.repository.CompanyRepository;
import com.innovationandtrust.profile.repository.UserActivityRepository;
import com.innovationandtrust.profile.repository.UserRepository;
import com.innovationandtrust.profile.service.restclient.EmployeeFeignClient;
import com.innovationandtrust.profile.service.spefication.CompanySpec;
import com.innovationandtrust.profile.service.spefication.UserSpec;
import com.innovationandtrust.share.constant.RoleConstant;
import com.innovationandtrust.share.model.corporateprofile.EmployeeResponseDto;
import com.innovationandtrust.utils.commons.AdvancedFilter;
import com.innovationandtrust.utils.commons.Filter;
import com.innovationandtrust.utils.commons.QueryOperator;
import com.innovationandtrust.utils.exception.exceptions.EntityNotFoundException;
import com.innovationandtrust.utils.exception.exceptions.InvalidRequestException;
import com.innovationandtrust.utils.exception.exceptions.KeycloakException;
import com.innovationandtrust.utils.exception.exceptions.MissingParamException;
import com.innovationandtrust.utils.keycloak.model.Company;
import com.innovationandtrust.utils.keycloak.model.KeycloakUserRequest;
import com.innovationandtrust.utils.keycloak.model.UserInfo;
import com.innovationandtrust.utils.keycloak.provider.IKeycloakProvider;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@Transactional
public class UserService extends CommonCrudService<UserDto, User, Long> {
  private final UserRepository userRepository;
  private final UserActivityRepository activityRepository;
  private final RoleService roleService;
  private final CompanyRepository companyRepository;
  private static final String MISSING_PARAM_EXCEPTION = "User Id ...";
  public static final String PHONE_EXIST = "Phone number has already taken.";
  public static final String MAIL_EXIST = "Email has already taken.";

  @Autowired
  public UserService(
      UserRepository userRepository,
      ModelMapper modelMapper,
      UserActivityRepository activityRepository,
      RoleService roleService,
      IKeycloakProvider keycloakProvider,
      CompanyRepository companyRepository) {
    super(modelMapper, keycloakProvider);
    this.userRepository = userRepository;
    this.activityRepository = activityRepository;
    this.roleService = roleService;
    this.companyRepository = companyRepository;
  }

  /**
   * To create a user and add to keycloak.
   *
   * @param dto refer to {@link UserDto}
   * @return object of {@link UserDto}.
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public UserDto save(UserDto dto) {
    var userEntity = this.mapEntity(dto);
    var keycloakUser =
        KeycloakUserRequest.builder()
            .email(dto.getEmail())
            .firstName(dto.getFirstName())
            .lastName(dto.getLastName())
            .build();
    if (StringUtils.hasText(dto.getPassword())) {
      keycloakUser.setPassword(dto.getPassword());
    }
    // find roles by name
    if (!dto.getRoles().isEmpty()) {
      var roles = this.roleService.findByNames(dto.getRoles());
      keycloakUser.setRoles(roles.stream().map(RoleDto::getName).toList());
      userEntity.setRoles(new HashSet<>(this.mapAll(roles, Role.class)));
    }

    // Create a user in keycloak
    var keycloakResponse = this.keycloakProvider.createUser(keycloakUser);
    userEntity.setUserEntityId(keycloakResponse.getId());
    if (!dto.getRoles().contains(RoleConstant.SUPER_ADMIN)) {
      userEntity.setCreatedBy(dto.getCreatedBy());
    }

    userEntity.setActive(false);
    var response = this.userRepository.save(userEntity);

    var activity =
        UserActivity.builder()
            .user(response)
            .token(UUID.randomUUID().toString())
            .expireTime(DateUtils.addDays(new Date(), 2))
            .build();
    activity.setCreatedBy(this.getUserId());
    this.activityRepository.save(activity);

    this.addUserAttributesToKeycloak(
        keycloakResponse.getId(),
        response.getId(),
        dto.getCompanyId(),
        dto.getCorporateUuid(),
        true);
    return this.mapData(response);
  }

  /**
   * to update user login status.
   *
   * @param user refers to user object
   */
  @Transactional(rollbackFor = Exception.class)
  public UserDto updateStatus(UserDto user) {
    this.setUserActive(String.valueOf(user.getUserEntityId()), user.getActive());
    this.userRepository.updateStatus(user.getId(), user.getActive());
    return user;
  }

  public void updateUserKeycloakEmail(User user) {

    var keycloakUser =
        KeycloakUserRequest.builder()
            .id(String.valueOf(user.getUserEntityId()))
            .email(user.getEmail())
            .build();

    log.info("Updating user username or email on keycloak userId: {}", user.getUserEntityId());
    this.keycloakProvider.updateUserEmail(keycloakUser);
  }

  /**
   * To enable/disable user login.
   *
   * @param userUuid refers to user uuid
   * @param active status to set user
   */
  public void setUserActive(String userUuid, boolean active) {
    try {
      // Update keycloak: disable/enable user login
      var keycloakUser = KeycloakUserRequest.builder().id(userUuid).active(active).build();
      this.keycloakProvider.isActive(keycloakUser);
    } catch (Exception e) {
      log.error("Error when update user status on Keycloak: ", e);
      throw new KeycloakException(e.getMessage());
    }
  }

  /**
   * Add user attribute to keycloak method.
   *
   * @param keycloakUserId refers the id of that user in keycloak
   * @param userId refers to user's id
   * @param companyId refers to the user's companyId
   */
  private void addUserAttributesToKeycloak(
      String keycloakUserId,
      Long userId,
      Long companyId,
      String corporateUuid,
      boolean firstLogin) {
    var userInfo = new UserInfo();
    if (Objects.nonNull(companyId) && companyId > 0) {
      this.companyRepository
          .findCompanyById(companyId, com.innovationandtrust.profile.model.projection.Company.class)
          .ifPresent(
              value ->
                  userInfo.setCompany(
                      new Company(value.getId(), value.getName(), value.getUuid())));
    }
    userInfo.setUserId(userId);
    userInfo.setFirstLogin(firstLogin);
    userInfo.setCorporateId(corporateUuid);
    // Set user attributes to keycloak
    Executors.newSingleThreadExecutor()
        .execute(() -> this.keycloakProvider.setUserAttributes(keycloakUserId, userInfo));
  }

  /**
   * Update user entity method./
   *
   * @param dto refers to the userDTO that used to update
   * @return updated UserDto
   */
  @Override
  public UserDto update(UserDto dto) {
    var userEntity =
        this.userRepository
            .findById(dto.getId())
            .orElseThrow(() -> new EntityNotFoundException("User with this id is not found!"));

    var keycloakUser =
        KeycloakUserRequest.builder()
            .id(String.valueOf(userEntity.getUserEntityId()))
            .firstName(dto.getFirstName())
            .lastName(dto.getLastName())
            .build();

    // find roles by name
    if (!dto.getRoles().isEmpty()) {
      this.mapEntity(dto, userEntity);
      var roles = this.roleService.findByNames(dto.getRoles());
      keycloakUser.setRoles(roles.stream().map(RoleDto::getName).toList());
      userEntity.setRoles(new HashSet<>(this.mapAll(roles, Role.class)));
    } else {
      dto.setRoles(null);
      this.mapEntity(dto, userEntity);
    }

    // Update a user in keycloak
    try {
      this.keycloakProvider.updateUser(keycloakUser);
    } catch (Exception e) {
      log.error("Error when update user on Keycloak: ", e);
      throw new KeycloakException(e.getMessage());
    }
    userEntity.setModifiedBy(this.getUserId());

    var response = this.userRepository.save(userEntity);
    this.addUserAttributesToKeycloak(
        response.getUserEntityId(),
        response.getId(),
        dto.getCompanyId(),
        dto.getCorporateUuid(),
        false);
    return this.mapData(response);
  }

  /**
   * Update user entity method./
   *
   * @param dto refers to the userDTO that used to update
   */
  public void updateDeleted(UserDto dto) {
    this.userRepository.updateDeleted(dto.getId(), dto.getDeleted());
  }

  /**
   * To find all users.
   *
   * @return a list of UserDto
   */
  @Override
  @Transactional(readOnly = true)
  public List<UserDto> findAll() {
    return userRepository.findAll().stream().map(this::mapData).toList();
  }

  /**
   * To find all users with pagination.
   *
   * @param pageable to have pagination in response.
   * @param search to filter the records of users.
   * @return a page of UserDto objects.
   */
  @Override
  @Transactional(readOnly = true)
  public Page<UserDto> findAll(Pageable pageable, String search) {
    Specification<User> spec =
        AdvancedFilter.searchByFields(
            Arrays.asList(
                Filter.builder()
                    .field(AbstractEntity_.CREATED_BY)
                    .operator(QueryOperator.EQUALS)
                    .value(super.getUserId().toString())
                    .build(),
                Filter.builder().field(User_.DELETED).operator(QueryOperator.IS_FALSE).build()));
    if (StringUtils.hasText(search)) {
      spec = spec.and(UserSpec.search(search));
    }
    return this.userRepository.findAll(spec, pageable).map(this::mapData);
  }

  /**
   * To find all users with pagination in the same company.
   *
   * @param userId the user id to fetch other.
   * @return a page of UserDto objects.
   */
  @Transactional(readOnly = true)
  public List<UserDto> findAllInCompany(Long userId) {
    var user = this.findById(userId);
    var users =
        this.userRepository.findAll(
            Specification.where(UserSpec.findUserByCompanyId(user.getCompanyId())));
    return this.mapAll(users, UserDto.class);
  }

  /**
   * To find user by its id.
   *
   * @param id refer to user's id.
   * @return UserDto object.
   */
  @Override
  @Transactional(readOnly = true)
  public UserDto findById(Long id) {
    if (!Objects.nonNull(id)) {
      throw new MissingParamException(MISSING_PARAM_EXCEPTION);
    }
    return this.userRepository
        .findByIdAndDeleted(id, false)
        .map(this::mapData)
        .orElseThrow(() -> new UserNotFoundException(id));
  }

  @Transactional(readOnly = true)
  public Optional<UserDto> findByIdOptional(Long id) {
    if (!Objects.nonNull(id)) {
      throw new MissingParamException(MISSING_PARAM_EXCEPTION);
    }
    return this.userRepository.findByIdAndDeleted(id, false).map(this::mapData);
  }

  @Transactional(readOnly = true)
  public UserDto findByIdNoFiltering(Long id) {
    if (!Objects.nonNull(id)) {
      throw new MissingParamException(MISSING_PARAM_EXCEPTION);
    }
    return this.userRepository
        .findById(id)
        .map(this::mapData)
        .orElseThrow(() -> new UserNotFoundException(id));
  }

  /**
   * To find a user by its keycloak id.
   *
   * @param uuid refers to userEntityId.
   * @return UserDto object.
   */
  @Transactional(readOnly = true)
  public Optional<UserDto> findByUserEntityId(String uuid) {
    return Optional.ofNullable(
        this.userRepository
            .findByUserEntityIdAndDeleted(uuid, false)
            .map(this::mapData)
            .orElseThrow(
                () ->
                    new EntityNotFoundException(
                        "User with this uuid: " + uuid + " is not found!")));
  }

  /**
   * To check user if existed.
   *
   * @param email refer to user's email.
   * @return a boolean depends on user is existed or not.
   */
  @Transactional(readOnly = true)
  public boolean isUserExist(String email) {
    return this.userRepository
        .findOne(Specification.where(UserSpec.findByEmail(email)))
        .isPresent();
  }

  /**
   * To check user if existed and not current user.
   *
   * @param email refer to user's email.
   * @return a boolean depends on user is existed or not.
   */
  @Transactional(readOnly = true)
  public boolean isEmailExist(Long userId, String email) {
    return this.userRepository
        .findOne(Specification.where(UserSpec.findByExistMail(userId, email)))
        .isPresent();
  }

  /**
   * To check user if existed.
   *
   * @param phone refer to user's phone.
   * @return a boolean depends on user is existed or not.
   */
  @Transactional(readOnly = true)
  public boolean isUserPhoneExist(String phone) {
    return this.userRepository.findAll(Specification.where(UserSpec.findByPhone(phone))).stream()
        .findFirst()
        .isPresent();
  }

  /**
   * To check user if existed and not current user.
   *
   * @param phone refer to user's phone.
   * @return a boolean depends on user is existed or not.
   */
  @Transactional(readOnly = true)
  public boolean isPhoneExist(Long userId, String phone) {
    return this.userRepository
        .findAll(Specification.where(UserSpec.findByExistPhone(userId, phone)))
        .stream()
        .findFirst()
        .isPresent();
  }

  public void validateUserPhoneAndMail(String email, String phone) {
    if (isUserExist(email)) {
      throw new InvalidRequestException(MAIL_EXIST);
    } else if (isUserPhoneExist(phone)) {
      throw new InvalidRequestException(PHONE_EXIST);
    }
  }

  /**
   * To check user if exist and not deleted.
   *
   * @param email refer to user's email.
   * @return a boolean depends on user is existed or not.
   */
  @Transactional(readOnly = true)
  public boolean isValidUser(String email) {
    return this.userRepository
        .findOne(
            Specification.where(
                UserSpec.findByEmail(email)
                    .and(
                        AdvancedFilter.searchByField(
                            Filter.builder()
                                .field(User_.DELETED)
                                .operator(QueryOperator.IS_FALSE)
                                .build()))))
        .isPresent();
  }

  /**
   * To get users with list of user id.
   *
   * @param ids refers to list of user id
   * @return list of {@link UserDto}
   */
  public List<UserDto> findUsers(List<String> ids) {
    var users =
        this.userRepository.findAll(
            AdvancedFilter.searchByField(
                Filter.builder().field(User_.ID).operator(QueryOperator.IN).values(ids).build()));
    return this.mapAll(users, UserDto.class);
  }

  public Page<UserDto> findByCompany(Pageable pageable, String search, String uuid, String role) {
    var company = this.companyRepository.findOne(Specification.where(CompanySpec.findByUuid(uuid)));
    return company
        .map(
            value ->
                this.userRepository
                    .findAll(
                        Specification.where(
                            UserSpec.findUserByCompanyId(value.getId())
                                .and(UserSpec.findByRole(role))
                                .and(UserSpec.search(search))
                                .and(UserSpec.findUserByDeleted(false))),
                        pageable)
                    .map(this::mapData))
        .orElse(Page.empty());
  }

  public List<User> findByRole(String role) {
    return this.userRepository.findAll(
        Specification.where(UserSpec.findByRole(role).and(UserSpec.findUserByDeleted(false))));
  }

  protected <T extends UserEmployee> List<T> userEmployee(
      List<T> users, EmployeeFeignClient employeeFeignClient) {
    var userIds = users.stream().map(AbstractUser::getId).toList();

    List<EmployeeResponseDto> employeeResponseDtoList =
        employeeFeignClient.listEmployeesByUserIds(userIds);

    users.forEach(
        corporateUser ->
            employeeResponseDtoList.stream()
                .filter(emp -> Objects.equals(corporateUser.getId(), emp.getUserId()))
                .findFirst()
                .ifPresent(
                    emp -> {
                      corporateUser.setUserAccess(emp.getUserAccess());
                      corporateUser.setBusinessUnit(emp.getBusinessUnit());
                    }));
    return users;
  }

  /**
   * To check user by company id and user id.
   *
   * @param companyId refers to company unique id
   * @param userId refers to user id
   */
  public void checkUserCompany(Long companyId, Long userId) {
    var isExists =
        this.userRepository.exists(
            AdvancedFilter.searchByFields(
                Arrays.asList(
                    Filter.builder()
                        .field(User_.ID)
                        .operator(QueryOperator.EQUALS)
                        .value(userId.toString())
                        .build(),
                    Filter.builder()
                        .field(User_.COMPANY_ID)
                        .operator(QueryOperator.EQUALS)
                        .value(companyId.toString())
                        .build())));
    if (!isExists) {
      throw new UserNotFoundException(userId);
    }
  }

  public List<UserDto> listAllByCompany(List<Long> ids) {
    return this.userRepository
        .findAll(Specification.where(UserSpec.findUserByListOfCompanyIds(ids)))
        .stream()
        .map(this::mapData)
        .toList();
  }
}
