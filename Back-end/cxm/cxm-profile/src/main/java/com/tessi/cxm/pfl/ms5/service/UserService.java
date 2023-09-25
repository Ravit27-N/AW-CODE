package com.tessi.cxm.pfl.ms5.service;

import com.cxm.tessi.pfl.shared.flowtreatment.util.EmailValidatorUtils;
import com.opencsv.CSVWriter;
import com.tessi.cxm.pfl.ms5.constant.AddressType;
import com.tessi.cxm.pfl.ms5.constant.UserManagementConstants;
import com.tessi.cxm.pfl.ms5.dto.AssignUsersProfilesRequestDTO;
import com.tessi.cxm.pfl.ms5.dto.ClientAssignedServiceDTO;
import com.tessi.cxm.pfl.ms5.dto.CreateUserRequestDTO;
import com.tessi.cxm.pfl.ms5.dto.CreateUserResponseDTO;
import com.tessi.cxm.pfl.ms5.dto.DepartmentDto;
import com.tessi.cxm.pfl.ms5.dto.LoadOrganization;
import com.tessi.cxm.pfl.ms5.dto.ProfileFilterCriteria;
import com.tessi.cxm.pfl.ms5.dto.QueryUserResponseDTO;
import com.tessi.cxm.pfl.ms5.dto.QueryUserResponsesDTO;
import com.tessi.cxm.pfl.ms5.dto.UpdateUserDto;
import com.tessi.cxm.pfl.ms5.dto.UpdateUserRequestDto;
import com.tessi.cxm.pfl.ms5.dto.UpdateUserRequestDto.UserDto;
import com.tessi.cxm.pfl.ms5.dto.UserAssignedProfileDTO;
import com.tessi.cxm.pfl.ms5.dto.UserAssignedServiceDTO;
import com.tessi.cxm.pfl.ms5.dto.UserCreatedByProjection;
import com.tessi.cxm.pfl.ms5.dto.UserCredentialRequest;
import com.tessi.cxm.pfl.ms5.dto.UserCredentialResponse;
import com.tessi.cxm.pfl.ms5.dto.UserDepartmentDto;
import com.tessi.cxm.pfl.ms5.dto.UserInfoRequestUpdatePasswordDto;
import com.tessi.cxm.pfl.ms5.dto.UserRequestResetPasswordDto;
import com.tessi.cxm.pfl.ms5.dto.UserResetPasswordDto;
import com.tessi.cxm.pfl.ms5.dto.UsersExportDto;
import com.tessi.cxm.pfl.ms5.dto.enumeration.PasswordActionType;
import com.tessi.cxm.pfl.ms5.dto.enumeration.UseActionType;
import com.tessi.cxm.pfl.ms5.entity.AuthenticationAttempts;
import com.tessi.cxm.pfl.ms5.entity.Client;
import com.tessi.cxm.pfl.ms5.entity.Profile;
import com.tessi.cxm.pfl.ms5.entity.ReturnAddress;
import com.tessi.cxm.pfl.ms5.entity.UserEntity;
import com.tessi.cxm.pfl.ms5.entity.UserProfiles;
import com.tessi.cxm.pfl.ms5.entity.UserRequestResetPassword;
import com.tessi.cxm.pfl.ms5.entity.projection.UserInfoProjection;
import com.tessi.cxm.pfl.ms5.exception.DepartmentNotFoundException;
import com.tessi.cxm.pfl.ms5.exception.EmailInvalidPatternException;
import com.tessi.cxm.pfl.ms5.exception.InvalidUserException;
import com.tessi.cxm.pfl.ms5.exception.InvalidUserPasswordException;
import com.tessi.cxm.pfl.ms5.exception.KeycloakUserNotFound;
import com.tessi.cxm.pfl.ms5.exception.NotRegisteredServiceUserException;
import com.tessi.cxm.pfl.ms5.exception.PasswordAlreadyUsedException;
import com.tessi.cxm.pfl.ms5.exception.ProfileNotBelongToServiceException;
import com.tessi.cxm.pfl.ms5.exception.ProfileNotFoundException;
import com.tessi.cxm.pfl.ms5.exception.TokenExpiredDateException;
import com.tessi.cxm.pfl.ms5.exception.UserKeycloakServiceExceptionHandler;
import com.tessi.cxm.pfl.ms5.exception.UserNotFoundException;
import com.tessi.cxm.pfl.ms5.exception.UserPasswordNotMatchException;
import com.tessi.cxm.pfl.ms5.exception.UserRepresentationNotFoundException;
import com.tessi.cxm.pfl.ms5.exception.UserResetPasswordException;
import com.tessi.cxm.pfl.ms5.repository.AuthenticationAttemptsRepository;
import com.tessi.cxm.pfl.ms5.repository.ClientRepository;
import com.tessi.cxm.pfl.ms5.repository.DepartmentRepository;
import com.tessi.cxm.pfl.ms5.repository.ProfileRepository;
import com.tessi.cxm.pfl.ms5.repository.ReturnAddressRepository;
import com.tessi.cxm.pfl.ms5.repository.UserProfileRepository;
import com.tessi.cxm.pfl.ms5.repository.UserRepository;
import com.tessi.cxm.pfl.ms5.repository.UserRequestResetPasswordRepository;
import com.tessi.cxm.pfl.ms5.service.specification.PasswordArchiveService;
import com.tessi.cxm.pfl.ms5.service.specification.ProfileSpecification;
import com.tessi.cxm.pfl.ms5.service.specification.UserSpecification;
import com.tessi.cxm.pfl.ms5.util.PasswordUtils;
import com.tessi.cxm.pfl.ms5.util.UserRequestResetPasswordAdapter;
import com.tessi.cxm.pfl.ms5.util.UserResetPasswordValidator;
import com.tessi.cxm.pfl.shared.auth.AuthenticationUtils;
import com.tessi.cxm.pfl.shared.exception.BadRequestException;
import com.tessi.cxm.pfl.shared.exception.KeycloakServiceException;
import com.tessi.cxm.pfl.shared.exception.UserAccessDeniedExceptionHandler;
import com.tessi.cxm.pfl.shared.model.AddressDto;
import com.tessi.cxm.pfl.shared.model.User;
import com.tessi.cxm.pfl.shared.model.UserDetail;
import com.tessi.cxm.pfl.shared.model.UserInfoResponse;
import com.tessi.cxm.pfl.shared.model.UserServiceResponseDto;
import com.tessi.cxm.pfl.shared.model.UsersRelatedToPrivilege;
import com.tessi.cxm.pfl.shared.model.hubdigitalflow.AuthRequest;
import com.tessi.cxm.pfl.shared.model.hubdigitalflow.Email;
import com.tessi.cxm.pfl.shared.model.hubdigitalflow.MailRequest;
import com.tessi.cxm.pfl.shared.service.AbstractCrudService;
import com.tessi.cxm.pfl.shared.service.keycloak.KeycloakService;
import com.tessi.cxm.pfl.shared.service.restclient.HubDigitalFlow;
import com.tessi.cxm.pfl.shared.service.restclient.ModificationLevel;
import com.tessi.cxm.pfl.shared.service.restclient.VisibilityLevel;
import com.tessi.cxm.pfl.shared.utils.AddressValidator;
import com.tessi.cxm.pfl.shared.utils.BearerAuthentication;
import com.tessi.cxm.pfl.shared.utils.HtmlUtils;
import com.tessi.cxm.pfl.shared.utils.ProfileConstants;
import com.tessi.cxm.pfl.shared.utils.SerialExecutor;
import feign.FeignException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.keycloak.adapters.springboot.KeycloakSpringBootProperties;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.FileCopyUtils;

/**
 * Handle business logic of {@link UserService}.
 *
 * @author Piseth Khon
 * @author Pisey CHORN
 * @author Sokhour LACH
 * @author Sakal TUM
 * @author Vichet CHANN
 * @author Chamrong THOR
 * @since 07/12/21
 */
@Slf4j
@Service
@Transactional
public class UserService extends AbstractCrudService<UserDepartmentDto, UserEntity, Long>
    implements AdminService, SharedService {

  private static final String EMAIL_PATTERN = "Email is invalid pattern";
  private static final String USER_REPRESENTATION_NOT_FOUND = "User not found in keycloak server";
  private static final String KEYCLOAK_USER_NOT_FOUND = "Keycloak user not found";
  private static final String TOKEN_EXPIRED_DATE = "Token is expired";
  private static final String USER_RESET_PASSWORD_FAIL = "Reset user password fail";
  private static final String USER_NOT_FOUND = "User is not found: ";

  private static final String USER_BY_ADMIN = "admin";
  private static final String USER_BY_NONADMIN = "nonadmin";

  private static final String MISSING_USER_KEYCLOAK = "Missing Keycloak user for user with this id: ";
  private final UsersResource userResource;
  private final UserRepository userRepository;
  private final DepartmentService departmentService;
  private final ProfileRepository profileRepository;
  private final UserProfileRepository userProfileRepository;
  private final DepartmentRepository departmentRepository;
  private final UserRequestResetPasswordRepository userRequestResetPasswordRepository;
  private ProfileService profileService;

  @Value("${cxm.oauth2.group}")
  private String defaultGroup;

  @Value("${cxm.user.admin-id}")
  private String adminUserId;

  @Value("classpath:/default_reset_password_email_form.html")
  private Resource defaultResetPasswordEmailFormHtmlFile;

  @Value("classpath:/default_user_account_creation_notification_email.html")
  private Resource defaultUserAccountCreationNotificationEmailHtmlFile;

  @Value("classpath:/default_user_account_creation_second_notification_email.html")
  private Resource defaultUserAccountCreationSecondNotificationEmailHtmlFile;

  @Value("${cxm.request-password-base-url}")
  private String requestResetPasswordBaseUrl;

  @Value("${cxm.login-front-base-url}")
  private String loginFrontBaseUrl;


  private String publicFilesFrontBaseUrl="";

  @Value("${cxm.mail.subject}")
  private String subjectMail;

  @Value("${cxm.mail.from}")
  private String senderMail;  


  private String userAccountCreationFirstSubjectMail="";


  private String userAccountCreationSecondSubjectMail="";

  @Value("${cxm.hub-account.username}")
  private String defaultUsername;

  @Value("${cxm.hub-account.password}")
  private String defaultPassword;

  private Duration durationTokenExpired;

  private ClientRepository clientRepository;
  private KeycloakSpringBootProperties keycloakSpringBootProperties;

  private HubDigitalFlow hubDigitalFlowFeignClient;

  private UserHubService userHubService;
  private AuthenticationAttemptsRepository authenticationAttemptsRepository;

  private final PasswordArchiveService passwordArchiveService;
  private ReturnAddressRepository returnAddressRepository;
  private AddressValidator addressValidator;

  private ReturnAddressService returnAddressService;
  /**
   * Initialize bean required by user service.
   *
   * @param departmentRepository               refers to bean of {@link DepartmentRepository}
   * @param keycloakService                    refers to bean of {@link KeycloakService}
   * @param mapper                             refers to bean of {@link ModelMapper}
   * @param userRepository                     refers to the bean of {@link UserRepository}
   * @param departmentService                  refers to the bean of {@link DepartmentService}
   * @param profileRepository                  refers to the bean {@link ProfileRepository}
   * @param userProfileRepository              refers to the bean of {@link UserProfileRepository}
   * @param userRequestResetPasswordRepository refers to the link of
   *                                           {UserRequestResetPasswordRepository}
   * @param passwordArchiveService refers to bean of
   *                                           {PasswordArchiveService}
   */
  public UserService(
      DepartmentRepository departmentRepository,
      KeycloakService keycloakService,
      ModelMapper mapper,
      UserRepository userRepository,
      DepartmentService departmentService,
      ProfileRepository profileRepository,
      UserProfileRepository userProfileRepository,
      UserRequestResetPasswordRepository userRequestResetPasswordRepository,
      PasswordArchiveService passwordArchiveService) {
    this.departmentRepository = departmentRepository;
    this.userResource = keycloakService.getUserResource();
    this.userRepository = userRepository;
    this.keycloakService = keycloakService;
    this.departmentService = departmentService;
    this.profileRepository = profileRepository;
    this.userProfileRepository = userProfileRepository;
    this.modelMapper = mapper;
    this.userRequestResetPasswordRepository = userRequestResetPasswordRepository;
    this.setRepository(this.userRepository);
    this.passwordArchiveService = passwordArchiveService;
  }

  @Autowired
  public void setClientRepository(ClientRepository clientRepository) {
    this.clientRepository = clientRepository;
  }

  @Autowired
  public void setAuthenticationAttempt(AuthenticationAttemptsRepository authenticationAttemptsRepository) {
    this.authenticationAttemptsRepository = authenticationAttemptsRepository;
  }

  @Autowired
  public void setKeycloakSpringBootProperties(
      KeycloakSpringBootProperties keycloakSpringBootProperties) {
    this.keycloakSpringBootProperties = keycloakSpringBootProperties;
  }

  @Override
  public UserRepository getUserRepository() {
    return this.userRepository;
  }

  @Autowired
  public void setHubDigitalFlowFeignClient(HubDigitalFlow hubDigitalFlow) {
    this.hubDigitalFlowFeignClient = hubDigitalFlow;
  }

  @Autowired
  public void setUserHubService(UserHubService userHubService) {
    this.userHubService = userHubService;
  }

  @Autowired
  public void setReturnAddressRepository(ReturnAddressRepository returnAddressRepository) {
    this.returnAddressRepository = returnAddressRepository;
  }

  @Autowired
  public void setAddressValidator(AddressValidator addressValidator) {
    this.addressValidator = addressValidator;
  }

  @Autowired
  public void setReturnAddressService(ReturnAddressService returnAddressService) {
    this.returnAddressService = returnAddressService;
  }

  /**
   * To assign a service to a user.
   *
   * @param userId    identity of a user
   * @param serviceId identity of a service
   * @return object of {@link UserDepartmentDto} after assigned
   */
  @Transactional(rollbackFor = Exception.class)
  public UserDepartmentDto assignServiceToUser(String userId, long serviceId) {
    var user = StringUtils.isEmpty(userId)
        ? this.userResource
        .get(keycloakService.getUserInfo(this.getUsername()).getId())
        .toRepresentation()
        : this.userResource.get(userId).toRepresentation();
    if (user == null) {
      throw new KeycloakServiceException(USER_NOT_FOUND + userId);
    }
    var userDepartment = new UserEntity();
    userDepartment.setTechnicalRef(user.getId());
    userDepartment.setCreatedBy(user.getUsername());
    userDepartment.setUsername(user.getUsername());
    userDepartment.setDepartment(this.departmentService.findEntity(serviceId));
    return this.mapData(this.userRepository.save(userDepartment), new UserDepartmentDto());
  }

  @Transactional(rollbackFor = Exception.class)
  public UserDepartmentDto assignServiceToUser(long serviceId) {
    return this.assignServiceToUser("", serviceId);
  }

  @Transactional(rollbackFor = Exception.class)
  public UserDepartmentDto assignServiceToUserByUsername(String username, long serviceId) {
    return this.assignServiceToUser(this.keycloakService.getUserInfo(username).getId(), serviceId);
  }

  public CreateUserResponseDTO createUser(CreateUserRequestDTO createUserRequestDto) {
    return createUser(createUserRequestDto, null, "");
  }

  /**
   * To create a new user.
   *
   * @param createUserRequestDto refers to the object of {@link CreateUserRequestDTO}
   * @return the object of {@link CreateUserRequestDTO} after created
   */
  @Transactional(rollbackFor = Exception.class)
  public CreateUserResponseDTO createUser(
      CreateUserRequestDTO createUserRequestDto, Long oldUserId, String technicalRef) {
    log.info("UserService - Start user creation service action");
    if (!this.isAdmin()
        && this.profileService.notContainsPrivilege(
            ProfileConstants.CXM_USER_MANAGEMENT,
            ProfileConstants.CXM_USER_MANAGEMENT.concat(
                "_".concat(UserManagementConstants.User.CREATE)))) {
      throw new UserAccessDeniedExceptionHandler();
    }

    if (StringUtils.isNotBlank(createUserRequestDto.getReturnAddressLevel())) {
      AddressType.resourceContain(createUserRequestDto.getReturnAddressLevel());
    }

    if(createUserRequestDto.getUserAction().equals(UseActionType.CREATE)) {
      String randomPassword = PasswordUtils.generateStrongPassword();
      createUserRequestDto.setPassword(randomPassword);
      createUserRequestDto.setConfirmedPassword(randomPassword);
    }
    if (oldUserId == null) {
      this.validateService(createUserRequestDto.getServiceId(), 0L);
      this.validateProfileCanUse(
          createUserRequestDto.getServiceId(), createUserRequestDto.getProfiles());
    }
    User keycloakUser = new User();
    if (oldUserId == null) {
      // validate request object
      if (!createUserRequestDto.getPassword().equals(createUserRequestDto.getConfirmedPassword())) {
        throw new InvalidUserException("User's password is not matched.");
      }
      // create user with keycloak
      keycloakUser = this.createUserInKeycloak(createUserRequestDto);
    } else {
      keycloakUser.setUsername(createUserRequestDto.getEmail());
      keycloakUser.setEmail(createUserRequestDto.getEmail());
      keycloakUser.setId(technicalRef);
      keycloakUser.setFirstName(createUserRequestDto.getFirstName());
      keycloakUser.setLastName(createUserRequestDto.getLastName());
    }

    var userAddress = createUserRequestDto.getUserReturnAddress();
    if (AddressType.USER.getValue().equalsIgnoreCase(createUserRequestDto.getReturnAddressLevel())
        && userAddress != null) {
      this.addressValidator.validate(userAddress);
    }

    var userCreatedResponse = this.modelMapper.map(keycloakUser, CreateUserResponseDTO.class);

    this.createUserInDatabase(createUserRequestDto, keycloakUser, userCreatedResponse, oldUserId);
    // response

    // Send notification mail of creation new user account
    if(createUserRequestDto.getUserAction().equals(UseActionType.CREATE)) {
      String superAdminUserName = AuthenticationUtils.getPrincipal();
      String superAdminId = AuthenticationUtils.getPrincipalIdentifier();
      log.info("Start calling Send notification mail of creation new user account {} - t : {} ",createUserRequestDto.getEmail(), superAdminUserName);
      var executor = new SerialExecutor(Executors.newSingleThreadExecutor());
      executor.execute(() -> this.sendConfirmationOfCreatedAccountByEmailForm(createUserRequestDto, superAdminUserName, superAdminId));
      log.info("End calling Send notification mail of creation new user account {} ",createUserRequestDto.getEmail());
    }
    return userCreatedResponse;
  }

  /**
   * Service must below to the client of invoking user.
   *
   * @param serviceId Service's id.
   * @param userId    refer to id of the user.
   */
  private void validateService(Long serviceId, Long userId) {
    LoadOrganization userOrganization;
    if (this.isAdmin()) {
      if (userId == 0) {
        return;
      }
      userOrganization = this.userRepository
          .loadUserOrganization(userId)
          .orElseThrow(() -> new UserNotFoundException(userId));
    } else {
      // Get Client of current invoking user
      final var currentUserId = this.getUserId();
      userOrganization = this.userRepository
          .loadOrganizationUser(currentUserId)
          .orElseThrow(() -> new NotRegisteredServiceUserException(currentUserId));
    }

    validateServiceInClient(serviceId, userOrganization);
  }

  /**
   * To validate the user service in the client.
   *
   * @param serviceId        refer to service id that passed from payload.
   * @param userOrganization refer to user organization from the database.
   */
  private void validateServiceInClient(Long serviceId, LoadOrganization userOrganization) {
    // Get client by service id
    Client client = this.clientRepository
        .findClientByServiceId(serviceId)
        .orElseThrow(() -> new DepartmentNotFoundException(serviceId));

    if (userOrganization.getClientId() != client.getId()) {
      var msg = String.format(
          "Service with id %d is not belong to the client %d.", serviceId,
          userOrganization.getClientId());
      throw new BadRequestException(msg);
    }
  }

  private void validateProfileCanUse(Long serviceId, List<Long> profileIds) {
    var profiles = this.profileService.getAllProfilesCriteria(serviceId)
        .stream().map(ProfileFilterCriteria::getId).collect(Collectors.toList());
    var invalidProfile = profileIds.stream().filter(p -> !profiles.contains(p))
        .collect(Collectors.toList());

    if (!invalidProfile.isEmpty()) {
      throw new InvalidUserException(
          String.format("The selected profile does not exist!: %s", invalidProfile));
    }
  }

  private User createUserInKeycloak(CreateUserRequestDTO createUserRequestDto) {
    var user = this.modelMapper.map(createUserRequestDto, User.class);
    user.setUsername(createUserRequestDto.getEmail());
    var keycloakUser = this.keycloakService.createUser(
        user, createUserRequestDto.getPassword(), List.of(this.defaultGroup));
    // Delete a user from keycloak if any error
    TransactionSynchronizationManager.registerSynchronization(
        new TransactionSynchronization() {
          @Override
          public void afterCompletion(int status) {
            if (status == TransactionSynchronization.STATUS_ROLLED_BACK) {
              keycloakService.deleteUser(keycloakUser.getId());
            }
          }
        });
    return keycloakUser;
  }

  private void createUserInDatabase(
      CreateUserRequestDTO createUserRequestDto,
      User keycloakUser,
      CreateUserResponseDTO userResponse,
      Long oldUserId) {
    // Get current client of current login user
    var department = this.departmentService.findEntity(createUserRequestDto.getServiceId());
    var addressType =
        (StringUtils.isNotBlank(createUserRequestDto.getReturnAddressLevel()))
            ? AddressType.resourceContain(createUserRequestDto.getReturnAddressLevel())
            : null;
    var userEntity =
        UserEntity.builder()
            .returnAddressLevel(addressType)
            .technicalRef(keycloakUser.getId())
            .username(keycloakUser.getUsername())
            .firstName(keycloakUser.getFirstName())
            .lastName(keycloakUser.getLastName())
            .email(keycloakUser.getUsername())
            .department(department)
            .isActive(true)
            .isAdmin(createUserRequestDto.getAdmin())
            .build();
    var savedUserEntity = this.userRepository.saveAndFlush(userEntity);
    userResponse.setId(savedUserEntity.getId());
    // update client profile if created by super admin.
    this.updateProfileOwner(userEntity, createUserRequestDto.getProfiles());

    // Assign profile(s) to create a user
    var userAssignedProfiles = createUserRequestDto.getProfiles().stream()
        .map(
            profileId -> {
              var profile = this.profileRepository
                  .findById(profileId)
                  .orElseThrow(() -> new ProfileNotFoundException(profileId));
              // Map a user to a profile
              return new UserProfiles(savedUserEntity, profile);
            })
        .collect(Collectors.toList());
    this.userProfileRepository.saveAll(userAssignedProfiles);
    this.passwordArchiveService.addPasswordToArchive(savedUserEntity, createUserRequestDto.getPassword());
    userResponse.setDepartment(this.modelMapper.map(department, DepartmentDto.class));

    // create old user returnAddress if present.
    if (oldUserId != null) {
      var oldReturnAddress =
          this.returnAddressService.findReturnAddress(
              oldUserId, AddressType.USER, department.getDivision().getClient().getId());

      oldReturnAddress.ifPresent(
          returnAddress ->
              this.returnAddressService.saveReturnAddress(
                  savedUserEntity, this.modelMapper.map(returnAddress, AddressDto.class)));
    }
    if (Objects.equals(addressType, AddressType.USER)) {
      if (createUserRequestDto.getUserReturnAddress() != null
          && !createUserRequestDto.getUserReturnAddress().getAddressDto().isEmpty()) {
        // Add returnAddress.
        var returnAddress =
            this.returnAddressService.saveReturnAddress(
                savedUserEntity, createUserRequestDto.getUserReturnAddress());
        var addressDto = this.modelMapper.map(returnAddress, AddressDto.class);
        userResponse.setUserReturnAddress(addressDto);
      }
    }

    var userAssignedProfileDtoList =
        userAssignedProfiles.stream()
            .map(userProfiles -> this.mapToUserAssignedProfileDto(userProfiles.getProfile()))
            .collect(Collectors.toList());
    userResponse.setProfiles(userAssignedProfileDtoList);
  }

  private void updateProfileOwner(UserEntity userEntity, List<Long> profileIds) {
    if (this.isAdmin()) {
      var userAdmins =
          userRepository.findByIsAdminTrueAndIsActiveTrue().stream()
              .map(UserCreatedByProjection::getCreatedBy)
              .collect(Collectors.toList());
      userAdmins.add(this.getUsernameSuperAdmin());

      final var clientId = this.getClientId(userEntity.getUsername());
      Specification<Profile> specification =
          Specification.where(ProfileSpecification.containProfileId(profileIds))
              .and(ProfileSpecification.ownerIdNull())
              .and(ProfileSpecification.containsIn(userAdmins))
              .and(ProfileSpecification.equalClientId(clientId));

      var profiles =
          this.profileRepository.findAll(specification).stream()
              .parallel()
              .map(
                  profile -> {
                    profile.setCreatedBy(userEntity.getUsername());
                    profile.setOwnerId(userEntity.getId());
                    return profile;
                  })
              .collect(Collectors.toList());

      this.profileRepository.saveAll(profiles);
    }
  }

  /**
   * Check if a specific username is available. This will use Keycloak as a single source.
   *
   * @param username User's username.
   * @return True if available, otherwise false.
   */
  public boolean isUsernameAvailable(String username) {
    if (StringUtils.isNotBlank(username)) {
      return this.keycloakService.isUsernameAvailable(username)
          && !this.userRepository.existsByUsernameAndIsActiveTrue(username);
    }
    return false;
  }

  /**
   * To retrieve the identity of user's service.
   *
   * @param username refers to the username of a user.
   * @return the service identity
   */
  @Transactional(readOnly = true)
  public long getServiceIdByUser(Optional<String> username) {
    var userId = this.keycloakService.getUserInfo(username.orElseGet(this::getUsername)).getId();
    return userRepository
        .loadOrganizationUser(userId)
        .orElseThrow(
            () -> new UserAccessDeniedExceptionHandler(
                "Can't process, because you don't have any service"))
        .getServiceId();
  }

  private UserAssignedProfileDTO mapToUserAssignedProfileDto(Profile profile) {
    return UserAssignedProfileDTO.builder().id(profile.getId()).name(profile.getName()).build();
  }

  private void checkUsers(List<User> keycloakUsers, List<UserEntity> dbUsers) {
    Supplier<Stream<UserEntity>> intersectedDbUsersStreamSupplier = () -> dbUsers.stream()
        .filter(
            userEntity -> keycloakUsers.stream()
                .anyMatch(
                    kcUser -> kcUser.getId().equals(userEntity.getTechnicalRef())));

    dbUsers.stream()
        .filter(
            userEntity -> intersectedDbUsersStreamSupplier
                .get()
                .noneMatch(
                    mergedUserEntity -> userEntity.getId().equals(mergedUserEntity.getId())))
        .forEach(userEntity -> log.warn(MISSING_USER_KEYCLOAK + userEntity.getId()));
  }

  private List<QueryUserResponseDTO> mapUsersToResponse(List<UserEntity> dbUsers) {
    return dbUsers.stream()
        .map(
            userEntity -> {
              var userResponseDto = this.modelMapper.map(userEntity, QueryUserResponseDTO.class);
              // Profiles
              List<UserAssignedProfileDTO> userAssignedProfileDtoList = userEntity.getUserProfiles()
                  .stream()
                  .map(
                      userProfiles -> this.mapToUserAssignedProfileDto(userProfiles.getProfile()))
                  .collect(Collectors.toList());
              userResponseDto.setProfiles(userAssignedProfileDtoList);

              return userResponseDto;
            })
        .collect(Collectors.toList());
  }
//map a list of UserEntity objects to a list of UsersExportDto
private List<UsersExportDto> mapUsersExportToResponse(List<UserEntity> dbUsers) {
  return dbUsers.stream()
      .map(userEntity -> {
          var userResponseDto = this.modelMapper.map(userEntity, UsersExportDto.class);

          // Set the client, division, and service fields
          if (userEntity.getDepartment() != null && userEntity.getDepartment().getDivision() != null && userEntity.getDepartment().getDivision().getClient() != null) {
              userResponseDto.setClient(userEntity.getDepartment().getDivision().getClient().getName());
          }
          if (userEntity.getDepartment() != null && userEntity.getDepartment().getDivision() != null) {
              userResponseDto.setDivision(userEntity.getDepartment().getDivision().getName());
          }
          if (userEntity.getDepartment() != null) {
              userResponseDto.setService(userEntity.getDepartment().getName());
          }
          log.info("userEntity.getId().toString() : {}", userEntity.getId().toString());

          // Retrieve the last login date from the repository
          
          Optional<AuthenticationAttempts> lastLoginAttemptsOptional = authenticationAttemptsRepository.findFirstByUserEntityOrderByAttemptDateDesc(userEntity);
          LocalDateTime lastLoginDate = lastLoginAttemptsOptional.map(AuthenticationAttempts::getAttemptDate).orElse(null);

          log.info("lastLoginDate : {}", lastLoginDate);
             
               // Set the last login date directly in the response DTO
              userResponseDto.setLastLoginDateTime(lastLoginDate != null ? lastLoginDate : null);
 

          // Profiles
          List<UserAssignedProfileDTO> userAssignedProfileDtoList = userEntity.getUserProfiles()
              .stream()
              .map(userProfiles -> this.mapToUserAssignedProfileDto(userProfiles.getProfile()))
              .collect(Collectors.toList());
          userResponseDto.setProfiles(userAssignedProfileDtoList);
         
        
          return userResponseDto;
      })
      .collect(Collectors.toList());
}
 

  /**
   * Get all users by reference client id of current invoking user.
   *
   * @param profileIds refers to the identity of {@link Profile} related to the user
   * @param filter     refers to any string relate to first-name, last-name, or email of the user
   * @return List of {@link QueryUserResponseDTO}.
   */
  @Transactional(readOnly = true)
  public List<QueryUserResponseDTO> getAllUsers(List<Long> profileIds, List<String> userType,
      List<Long> clientIds,
      List<Long> divisionIds, List<Long> serviceIds, String filter,
      Sort sort) {
    var specification = Specification.where(UserSpecification.isActive())
        .and(UserSpecification.contains(filter))
        .and(UserSpecification.clientsIn(clientIds))
        .and(UserSpecification.divisionsIn(divisionIds))
        .and(UserSpecification.servicesIn(serviceIds))
        .and(UserSpecification.inProfiles(profileIds));

    if (this.isAdmin()) {
      specification = buildQueryWithUserType(specification, userType);
    }

    if (!this.isAdmin()) {
      specification = this.buildQueryWithListPrivilege(specification);
    }

    List<UserEntity> sortedDbUserList = this.userRepository.findAll(specification, sort);
    var loadOrganization = this.userRepository.loadOrganizationUser(this.getUserId());
    if (loadOrganization.isEmpty()) {
      return new ArrayList<>();
    }

    // Get all users from keycloak and check unmatched user with DB
    var filteredKeycloakUserList = this.keycloakService.getUsers(this.defaultGroup);
    this.checkUsers(filteredKeycloakUserList, sortedDbUserList);
    // Map & merge DB users with Keycloak users

    return this.mapUsersToResponse(sortedDbUserList);
  }

  /**
   * Get all users by reference client id of current invoking user.
   *
   * @return Pages of {@link QueryUserResponseDTO}.
   */
  @Transactional(readOnly = true)
  public Page<QueryUserResponseDTO> getAllUsers(
      Pageable pageableSpecs, List<Long> profileIds, List<String> userType, List<Long> clientIds,
      List<Long> divisionIds, List<Long> serviceIds, String filter) {
    var specification = Specification.where(UserSpecification.isActive())
        .and(UserSpecification.contains(filter))
        .and(UserSpecification.clientsIn(clientIds))
        .and(UserSpecification.divisionsIn(divisionIds))
        .and(UserSpecification.servicesIn(serviceIds))
        .and(UserSpecification.inProfiles(profileIds));

    if (this.isAdmin()) {
      specification = buildQueryWithUserType(specification, userType);
    }

    if (!this.isAdmin()) {
      specification = this.buildQueryWithListPrivilege(specification);
    }
    log.info("specification:  {} ",specification.toString());
    Page<UserEntity> dbUsers = this.userRepository.findAll(specification, pageableSpecs);
    // Get all users from keycloak and check unmatched user with DB
    var filteredKeycloakUserList = this.keycloakService.getUsers(this.defaultGroup);
    this.checkUsers(filteredKeycloakUserList, dbUsers.getContent());

    // Map & merge DB users with Keycloak users
    var userResponseList = this.mapUsersToResponse(dbUsers.getContent());

    return new PageImpl<>(userResponseList, pageableSpecs, dbUsers.getTotalElements());
  }
  

// Exports users to CSV format
public byte[] exportUsersToCSV(
  List<Long> profileIds, List<String> userType,
  List<Long> clientIds, List<Long> divisionIds,
  List<Long> serviceIds, String filter) {

List<UsersExportDto> users = getUsersList(profileIds, userType, clientIds, divisionIds, serviceIds, filter);

// Convert users to CSV format
StringWriter writer = new StringWriter();

try (CSVWriter csvWriter = new CSVWriter(writer)) {
  // Define the CSV headers
  String[] headers = { "Client", "Division", "Service", "Last Name", "First Name", "Email", "Profiles", "Last Login" };
  csvWriter.writeNext(headers);

  // Write each user to the CSV file
  for (UsersExportDto user : users) {
      String[] data = {
              user.getClient(),
              user.getDivision(),
              user.getService(),
              user.getLastName(),
              user.getFirstName(),
              user.getEmail(),
              getProfileNames(user.getProfiles()),
              user.getLastLoginDateTime() != null ? user.getLastLoginDateTime().toString() : ""
             
      };
      csvWriter.writeNext(data);
  }

  csvWriter.flush();
} catch (IOException e) {
  // Handle exception
}

return writer.toString().getBytes();
}

private String getProfileNames(List<UserAssignedProfileDTO> profiles) {
if (profiles == null || profiles.isEmpty()) {
  return "";
}
return profiles.stream()
      .map(UserAssignedProfileDTO::getName)
      .collect(Collectors.joining(", "));
}

//Retrieves the list of users to export
public List<UsersExportDto> getUsersList(
    List<Long> profileIds, List<String> userType,
    List<Long> clientIds, List<Long> divisionIds,
    List<Long> serviceIds, String filter) {

  var pageableSpecs = PageRequest.of(0, Integer.MAX_VALUE); // Fetch all users

  return this.getAllUserstoexport(pageableSpecs, profileIds, userType, clientIds, divisionIds, serviceIds, filter).getContent();
}
 
//Retrieves a page of users based on the provided criteria and pageable specifications
@Transactional(readOnly = true)
public Page<UsersExportDto> getAllUserstoexport(
    Pageable pageableSpecs, List<Long> profileIds, List<String> userType, List<Long> clientIds,
    List<Long> divisionIds, List<Long> serviceIds, String filter) {
  var specification = Specification.where(UserSpecification.isActive())
      .and(UserSpecification.contains(filter))
      .and(UserSpecification.clientsIn(clientIds))
      .and(UserSpecification.divisionsIn(divisionIds))
      .and(UserSpecification.servicesIn(serviceIds))
      .and(UserSpecification.inProfiles(profileIds));

  if (this.isAdmin()) {
    specification = buildQueryWithUserType(specification, userType);
  }

  if (!this.isAdmin()) {
    specification = this.buildQueryWithListPrivilege(specification);
  }
  log.info("specification:  {} ",specification.toString());
  Page<UserEntity> dbUsers = this.userRepository.findAll(specification, pageableSpecs);
  // Get all users from keycloak and check unmatched user with DB
  var filteredKeycloakUserList = this.keycloakService.getUsers(this.defaultGroup);
  this.checkUsers(filteredKeycloakUserList, dbUsers.getContent());

  // Map & merge DB users with Keycloak users
  var userResponseList = this.mapUsersExportToResponse(dbUsers.getContent());

  return new PageImpl<>(userResponseList, pageableSpecs, dbUsers.getTotalElements());
}

  private Specification<UserEntity> buildQueryWithUserType(Specification<UserEntity> specification,
      List<String> userType) {
    // check user type and apply specification
    if (userType.stream()
        .anyMatch(type -> type.equals(USER_BY_ADMIN) || type.equals(USER_BY_NONADMIN))) {

      // if both admin and nonadmin
      if (userType.contains(USER_BY_ADMIN) && userType.contains(USER_BY_NONADMIN)) {
        // use default no need to apply filter
      } else if (userType.contains(USER_BY_ADMIN)) {
        specification = specification.and(UserSpecification.onlyAdminUser());
      } else if (userType.contains(USER_BY_NONADMIN)) {
        specification = specification.and(UserSpecification.onlyNormalUser());
      }
    }

    return specification;
  }

  private Specification<UserEntity> buildQueryWithListPrivilege(
      Specification<UserEntity> specification) {
    UsersRelatedToPrivilege userPrivilegeRelated = profileService.getUserPrivilegeRelated(
        true,
        ProfileConstants.CXM_USER_MANAGEMENT,
        ProfileConstants.CXM_USER_MANAGEMENT.concat(
            "_".concat(UserManagementConstants.User.LIST)));
    if (StringUtils.isBlank(userPrivilegeRelated.getLevel())) {
      throw new UserAccessDeniedExceptionHandler();
    }
    var users = userPrivilegeRelated.getRelatedUsers();
    if (userPrivilegeRelated.getLevel().equalsIgnoreCase(VisibilityLevel.USER.getKey())) {
      specification = specification.and(UserSpecification.createdByIn(users));
    } else {
      specification = specification.and(UserSpecification.usernameIn(users));
    }

    final var currUserServiceIds = this.profileService.loadAllServices(true,
        Optional.of(com.tessi.cxm.pfl.shared.utils.ProfileConstants.CXM_USER_MANAGEMENT),
        com.tessi.cxm.pfl.shared.utils.ProfileConstants.CXM_USER_MANAGEMENT.concat(
            "_".concat(UserManagementConstants.Profile.LIST)),
        "");

    specification = specification.and(UserSpecification.serviceIn(currUserServiceIds));
    return specification;
  }

  /**
   * Get a user information by id.
   *
   * @param userId User's id.
   * @return {@link QueryUserResponseDTO} object represent a user.
   */
  public QueryUserResponseDTO getUserById(String userId) {
    if (!this.isAdmin()) {
      this.checkIfUserCanNotVisibility(userId);
    }

    var userEntity = this.userRepository
        .findByIdAndIsActiveTrue(Long.valueOf(userId))
        .orElseThrow(() -> new UserNotFoundException(userId));
    var technicalRef = userEntity.getTechnicalRef();
    // Check if user is not existed in Keycloak
    if (this.keycloakService.findUserById(technicalRef).isEmpty()) {
      log.error(MISSING_USER_KEYCLOAK + technicalRef);
      throw new UserKeycloakServiceExceptionHandler(USER_NOT_FOUND + userId);
    }

    return this.mapUserEntityToResponseDto(userEntity);
  }

  public QueryUserResponseDTO getUserByUsername(String username) {
    var userEntity = this.userRepository
        .findByUsernameAndIsActiveTrue(username)
        .orElseThrow(() -> new UserNotFoundException(username));

    // Check if user is not existed in Keycloak
    if (this.keycloakService.findUserById(userEntity.getTechnicalRef()).isEmpty()) {
      final String userId = userEntity.getId().toString();
      log.error(MISSING_USER_KEYCLOAK.concat(userId));
      throw new UserKeycloakServiceExceptionHandler(USER_NOT_FOUND.concat(userId));
    }

    return this.mapUserEntityToResponseDto(userEntity);
  }

  public QueryUserResponseDTO getUserInfoByToken() {
    try {
      // All Users
      return this.getUserByUsername(this.getUsername());
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new KeycloakUserNotFound(KEYCLOAK_USER_NOT_FOUND);
    }
  }

  /**
   * Map a {@link UserEntity} to {@link QueryUserResponseDTO} object.
   *
   * @param userEntity {@link UserEntity} object
   * @return {@link QueryUserResponseDTO} object
   */
  public QueryUserResponseDTO mapUserEntityToResponseDto(UserEntity userEntity) {
    var userResponseDto = this.modelMapper.map(userEntity, QueryUserResponseDTO.class);
    // Profiles
    List<UserAssignedProfileDTO> userAssignedProfileDtoList = userEntity.getUserProfiles().stream()
        .map(userProfiles -> this.mapToUserAssignedProfileDto(userProfiles.getProfile()))
        .collect(Collectors.toList());

    UserAssignedServiceDTO userAssignedServiceDto = UserAssignedServiceDTO.builder()
        .id(userEntity.getDepartment().getId())
        .name(userEntity.getDepartment().getName())
        .divisionName(userEntity.getDepartment().getDivision().getName())// add new
        .divisionId(userEntity.getDepartment().getDivision().getId())// add new
        .clientName(userEntity.getDepartment().getDivision().getClient().getName())// add new
        .clientId(userEntity.getDepartment().getDivision().getClient().getId())// add new
        .build();

    Client client = userEntity.getDepartment().getDivision().getClient();
    ClientAssignedServiceDTO clientAssignedServiceDTO = ClientAssignedServiceDTO.builder()
        .id(client.getId())
        .name(client.getName())
        .build();

    userResponseDto.setClient(clientAssignedServiceDTO);
    userResponseDto.setService(userAssignedServiceDto);
    userResponseDto.setProfiles(userAssignedProfileDtoList);
    this.setUserReturnAddress(userResponseDto);
    return userResponseDto;
  }

  /***
   * Set return address and return address level for QueryUserResponseDTO.
   * @param userResponseDto refer to {@link QueryUserResponseDTO}.
   */
  private void setUserReturnAddress(QueryUserResponseDTO userResponseDto) {
    this.returnAddressRepository
        .findByRefIdAndType(userResponseDto.getId(), AddressType.USER)
        .ifPresent(
            address ->
                userResponseDto.setUserReturnAddress(modelMapper.map(address, AddressDto.class)));
  }

  /**
   * Update a user with reassign new multiple profiles or reassign multiple profiles to multiple.
   *
   * @param userDto {@link UpdateUserRequestDto}.
   * @return {@link QueryUserResponsesDTO}.
   */
  public QueryUserResponseDTO update(UpdateUserDto userDto) {
    if (!this.isAdmin()) {
      this.checkIfUserCanNotModify(userDto.getId());
    }
    // Get userDto from DB and Keycloak, otherwise throw exception
    var userEntity = this.userRepository
        .findByIdAndIsActiveTrue(userDto.getId())
        .orElseThrow(() -> new UserNotFoundException(userDto.getId()));

    this.validateService(userDto.getServiceId(), userDto.getId());
    this.validateProfileCanUse(userDto.getServiceId(), userDto.getProfiles());

    return this.updateUser(userDto, userEntity, userDto.getProfiles(), this.isAdmin());
  }

  /**
   * Update a user with reassign new multiple profiles or reassign multiple profiles to multiple
   * users.
   *
   * @param userDto    {@link UserDto}.
   * @param profileIds List of profile's id.
   * @param userEntity refer to object of {@link UserEntity}.
   * @return {@link QueryUserResponseDTO}.
   */
  private QueryUserResponseDTO updateUser(
      UpdateUserDto userDto,
      UserEntity userEntity,
      List<Long> profileIds,
      boolean isUpdateByAdmin) {

    if (userEntity.getDepartment().getId() != userDto.getServiceId()) {
      final CreateUserRequestDTO createUserRequestDTO = new CreateUserRequestDTO();
      BeanUtils.copyProperties(userDto, createUserRequestDTO);
      createUserRequestDTO.setEmail(userEntity.getEmail());
      userEntity.setActive(false);
      userEntity.setAdmin(userDto.getAdmin());
      this.userRepository.save(userEntity);
      createUser(createUserRequestDTO, userEntity.getId(), userEntity.getTechnicalRef());

    } else {
      var addressType =
          (StringUtils.isNotBlank(userDto.getReturnAddressLevel()))
              ? AddressType.resourceContain(userDto.getReturnAddressLevel())
              : null;
      var department =
          this.departmentRepository
              .findById(userDto.getServiceId())
              .orElseThrow(() -> new DepartmentNotFoundException(userDto.getServiceId()));

      // update in DB
      userEntity.setFirstName(userDto.getFirstName());
      userEntity.setLastName(userDto.getLastName());
      userEntity.setAdmin(userDto.getAdmin());
      userEntity.setDepartment(department);
      userEntity.setReturnAddressLevel(addressType);
      this.userRepository.save(userEntity);
      if (Objects.equals(addressType, AddressType.USER)) {
        if (userDto.getUserReturnAddress() != null
            && !userDto.getUserReturnAddress().getAddressDto().isEmpty()) {
          // Add returnAddress.
          var returnAddress =
              this.returnAddressService.saveReturnAddress(
                  userEntity, userDto.getUserReturnAddress());
          var addressDto = this.modelMapper.map(returnAddress, AddressDto.class);
          userDto.setUserReturnAddress(addressDto);
        } else {
          this.returnAddressService.deleteOldReturnAddress(
              userEntity.getId(), AddressType.USER, department.getDivision().getClient().getId());
        }
      }
      // reassign profiles
      this.reassignProfilesToUser(userEntity, profileIds);
      var kcUser = this.keycloakService
          .findUserById(userEntity.getTechnicalRef())
          .orElseThrow(
              () -> new UserKeycloakServiceExceptionHandler(
                  "UserDto is not found: " + userDto.getId()));
      // update in Keycloak
      kcUser.setFirstName(userDto.getFirstName());
      kcUser.setLastName(userDto.getLastName());

      if (isUpdateByAdmin && StringUtils.isNotBlank(userDto.getPassword())) {
        this.keycloakService.updateUserPassword(kcUser, userDto.getPassword());
      } else {
        this.keycloakService.updateUser(kcUser);
      }

      // update client profile if created by super admin.
      this.updateProfileOwner(userEntity, profileIds);
    }
    return this.mapUserEntityToResponseDto(userEntity);
  }

  /**
   * Re-assign profiles to users.
   *
   * @param requestDto refer to the object of {@link AssignUsersProfilesRequestDTO}
   */
  public QueryUserResponsesDTO assignProfiles(AssignUsersProfilesRequestDTO requestDto) {
    if (!this.isAdmin()
        && profileService
        .findModificationLevelOfUser(
            ProfileConstants.CXM_USER_MANAGEMENT,
            ProfileConstants.CXM_USER_MANAGEMENT.concat(
                "_".concat(UserManagementConstants.User.MODIFY)))
        .isEmpty()) {
      throw new UserAccessDeniedExceptionHandler();
    }
    var userResponseDtoList = requestDto.getUserIds().stream()
        .map(
            userId -> {
              UserEntity userEntity = this.userRepository
                  .findById(Long.valueOf(userId))
                  .orElseThrow(() -> new UserNotFoundException(userId));
              return this.reassignProfilesToUser(userEntity, requestDto.getProfiles());
            })
        .collect(Collectors.toList());

    return QueryUserResponsesDTO.builder().users(userResponseDtoList).build();
  }

  /**
   * Re-assign multiple profiles to a single user. This operation will delete all the assigned
   * profiles first.
   *
   * @param userEntity {@link UserEntity} in which the profiles will be assigned to.
   * @param profiles   List of profiles which will be assigned to a user.
   */
  private QueryUserResponseDTO reassignProfilesToUser(UserEntity userEntity, List<Long> profiles) {
    List<Profile> profileEntities = this.profileRepository.findAllById(profiles);
    this.userProfileRepository.deleteAllByUser(userEntity);

    List<UserProfiles> userProfilesList = profileEntities.stream()
        .map(profile -> new UserProfiles(userEntity, profile))
        .collect(Collectors.toList());
    this.userProfileRepository.saveAll(userProfilesList);

    userEntity.setUserProfiles(userProfilesList);

    return this.mapUserEntityToResponseDto(userEntity);
  }

  @Autowired
  public void setProfileService(ProfileService profileService) {
    this.profileService = profileService;
  }

  private void checkIfUserCanNotModify(long id) {
    UsersRelatedToPrivilege userPrivilegeRelated = profileService.getUserPrivilegeRelated(
        false,
        ProfileConstants.CXM_USER_MANAGEMENT,
        ProfileConstants.CXM_USER_MANAGEMENT.concat(
            "_".concat(UserManagementConstants.User.MODIFY)));
    final UserEntity userEntity = this.userRepository
        .findByIdAndIsActiveTrue(id)
        .orElseThrow(UserAccessDeniedExceptionHandler::new);
    if (StringUtils.isBlank(userPrivilegeRelated.getLevel())
        || checkUserProfile(
        null,
        null,
        ModificationLevel.OWNER.getKey().equals(userPrivilegeRelated.getLevel())
            ? this.getCreatedBy(id)
            : userEntity.getUsername(),
        false,
        userPrivilegeRelated)) {
      throw new UserAccessDeniedExceptionHandler();
    }
  }

  private void checkIfUserCanNotVisibility(String userId) {
    UsersRelatedToPrivilege userPrivilegeRelated = profileService.getUserPrivilegeRelated(
        true,
        ProfileConstants.CXM_USER_MANAGEMENT,
        ProfileConstants.CXM_USER_MANAGEMENT
            .concat("_")
            .concat(UserManagementConstants.User.EDIT));
    if (StringUtils.isBlank(userPrivilegeRelated.getLevel())
        || checkUserProfile(
        null,
        null,
        VisibilityLevel.USER.getKey().equals(userPrivilegeRelated.getLevel())
            ? this.getCreatedBy(Long.valueOf(userId))
            : this.userRepository.getById(Long.valueOf(userId)).getUsername(),
        true,
        userPrivilegeRelated)) {
      throw new UserAccessDeniedExceptionHandler();
    }
  }

  /**
   * To delete multiple users from database and keycloak.
   *
   * @param ids refer to the identity of users
   */
  @Transactional(rollbackFor = Exception.class)
  public void deleteUsers(List<Long> ids) {
    UsersRelatedToPrivilege userPrivilegeRelated = null;
    if (!this.isAdmin()) {
      userPrivilegeRelated = profileService.getUserPrivilegeRelated(
          false,
          ProfileConstants.CXM_USER_MANAGEMENT,
          ProfileConstants.CXM_USER_MANAGEMENT.concat(
              "_".concat(UserManagementConstants.User.DELETE)));
      if (StringUtils.isBlank(userPrivilegeRelated.getLevel())) {
        throw new UserAccessDeniedExceptionHandler();
      }
    }

    var deletingUsers = this.userRepository.findAllByIdInAndIsActiveTrue(ids);

    final var unknownDeletingUsers = ids.stream()
        .filter(
            e -> !deletingUsers.stream()
                .map(UserEntity::getId)
                .collect(Collectors.toList())
                .contains(e))
        .collect(Collectors.toList());
    if (!unknownDeletingUsers.isEmpty()) {
      throw new UserNotFoundException(unknownDeletingUsers.toString());
    }

    if (Objects.nonNull(userPrivilegeRelated)) {
      for (var deletingUser : deletingUsers) {
        if (checkUserProfile(
            null,
            null,
            ModificationLevel.OWNER.getKey().equals(userPrivilegeRelated.getLevel())
                ? deletingUser.getCreatedBy()
                : deletingUser.getUsername(),
            false,
            userPrivilegeRelated)) {
          throw new UserAccessDeniedExceptionHandler();
        }
      }
    }
    var userShouldBeDelete = deletingUsers.stream()
        .filter(existingUser -> ids.contains(existingUser.getId()))
        .map(UserEntity::getTechnicalRef)
        .collect(Collectors.toList());
    TransactionSynchronizationManager.registerSynchronization(
        new TransactionSynchronization() {
          @Override
          public void afterCompletion(int status) {
            if (status == TransactionSynchronization.STATUS_COMMITTED) {
              userShouldBeDelete.forEach(keycloakService::deleteUser);
            }
          }
        });
    deletingUsers.forEach(u -> u.setActive(false));
    this.userRepository.saveAll(deletingUsers);
  }

  @Override
  public String getConfiguredUserAdminId() {
    return this.adminUserId;
  }

  /**
   * Get user's organization include service, division and client.
   *
   * @return {@code LoadOrganization}
   */
  @Transactional(readOnly = true)
  public LoadOrganization getCurrentUserOrganization() {
    var userId = this.getUserId();
    return this.getCurrentUserOrganization(userId);
  }

  /**
   * Check if the current invoking user is an admin.
   *
   * @return True if admin, otherwise false.
   */
  public UserInfoResponse checkUserIsAdminOrNormal() {
    return UserInfoResponse.builder()
        .superAdmin(this.isSuperAdmin())
        .platformAdmin(this.isPlatformAdmin())
        .build();
  }

  /**
   * To retrieve data info of user by id into service.
   *
   * @param technicalRef refer to id of {@link UserEntity}
   * @return object of {@link UserInfoProjection}
   */
  public UserInfoProjection getUserInfo(String technicalRef) {
    return this.userRepository
        .findUserInfoByTechnicalRef(technicalRef)
        .orElseThrow(() -> new UserNotFoundException(technicalRef));
  }

  /**
   * Get user information by username.
   *
   * @param username User's username.
   * @return Object of {@link UserInfoProjection}.
   */
  public UserInfoProjection getUserInfoByUsername(String username) {
    return this.userRepository
        .findUserInfoByUsername(username)
        .orElseThrow(() -> new UserNotFoundException(username));
  }

  /**
   * Handling process of requesting to reset a user's password.
   *
   * @param dto refers to object of {@link UserResetPasswordDto}
   * @return the object of {@link UserResetPasswordDto}
   */
  public UserRequestResetPasswordDto requestForResetPassword(UserRequestResetPasswordDto dto) {
    // Validate email address with pattern.
    if (!this.isValidEmail(dto.getEmail())) {
      throw new EmailInvalidPatternException(EMAIL_PATTERN);
    }

    // Get user info from keycloak server and
    // verified with keycloak server.
    Optional<UserRepresentation> userInfo = this.getUserRepresentation(dto.getEmail());
    if (userInfo.isEmpty()) {
      throw new UserRepresentationNotFoundException(USER_REPRESENTATION_NOT_FOUND);
    }
    UserRequestResetPassword keycloakUser = UserRequestResetPasswordAdapter.build(userInfo.get());
    keycloakUser.setExpiredDate(this.getDurationOfTokenExpired());
    // Check user exist.
    boolean isExist = this.userRequestResetPasswordRepository.existsByEmail(dto.getEmail());
    // If true, remove user before create new user.
    if (isExist) {
      this.userRequestResetPasswordRepository.deleteByEmail(dto.getEmail());
    }
    // Save all data to database.
    var user = this.userRequestResetPasswordRepository.save(keycloakUser);

    // Send (reset password email) form to client.
    var executor = new SerialExecutor(Executors.newSingleThreadExecutor());
    executor.execute(() -> this.sendResetPasswordByEmailForm(user));
    return this.modelMapper.map(user, UserRequestResetPasswordDto.class);
  }

  @Retryable(value = FeignException.class, maxAttempts = 5, backoff = @Backoff(delay = 30000))
  private void sendResetPasswordByEmailForm(UserRequestResetPassword user) {
    String htmlContent = this.getDefaultResetPasswordHtmlContentWithReplacement(buildResetPasswordLink(user));
    MailRequest mailRequest = MailRequest.builder().technicalReference(user.getKeycloakUserId())
        .from(this.senderMail)
        .to(List.of(new Email(user.getEmail(), "")))
        .trackOpenEvent(false).trackClickEvent(false)
        .subject(this.subjectMail)
        .body(htmlContent).build();

    //final String hubAccessToken = this.userHubService.getHubToken();
    final var hubUser = this.userHubService.getDefaultUser();
    final var hubToken = this.hubDigitalFlowFeignClient.getAuthToken(
                    AuthRequest.builder().login(hubUser.getUsername()).password(hubUser.getPassword())
                            .build())
            .getToken();
     
    this.hubDigitalFlowFeignClient.sendMail(mailRequest, null, "", "",
          BearerAuthentication.PREFIX_TOKEN.concat(hubToken));
  }

  private String buildResetPasswordLink(UserRequestResetPassword user) {
    return requestResetPasswordBaseUrl.concat("?token=").concat(user.getToken());
  }

  @Retryable(value = FeignException.class, maxAttempts = 5, backoff = @Backoff(delay = 30000))
  public void sendConfirmationOfCreatedAccountByEmailForm(CreateUserRequestDTO user, String superAdminUserName, String superAdminId) {
    Map<String, String> createdAccountConfirmationParams = new HashMap<>();
    Map<String, String> secondConfirmationMailParams = new HashMap<>();
    createdAccountConfirmationParams.put("$CXM_LOGIN_URL$", this.loginFrontBaseUrl);
    createdAccountConfirmationParams.put("$CREATED_USER_LAST_NAME$", user.getLastName());
    createdAccountConfirmationParams.put("$CREATED_USER_USER_NAME$", user.getEmail());
    createdAccountConfirmationParams.put("$CXM_MAIL_HEADER_IMAGE$", publicFilesFrontBaseUrl.concat("/images/cxm-logo/digitalexperience.png"));
    String htmlContent = this.getDefaultUserAccountCreationNotificationContentWithReplacement(createdAccountConfirmationParams);

    secondConfirmationMailParams.put("$CREATED_USER_PASSWORD$", user.getPassword());
    secondConfirmationMailParams.put("$CXM_MAIL_HEADER_IMAGE$", publicFilesFrontBaseUrl.concat("/images/cxm-logo/digitalexperience.png"));
    String secondHtmlContent = this.getDefaultSecondNotificationContentWithReplacement(secondConfirmationMailParams);

    final var hubUser = this.userHubService.getDefaultUser();
    final var hubToken = this.hubDigitalFlowFeignClient.getAuthToken(
                    AuthRequest.builder().login(hubUser.getUsername()).password(hubUser.getPassword())
                            .build())
            .getToken();
    MailRequest mailRequest = MailRequest.builder().technicalReference(superAdminId)
            .from(this.senderMail)
            .to(List.of(new Email(user.getEmail(), "")))
            .trackOpenEvent(false).trackClickEvent(false)
            .subject(this.userAccountCreationFirstSubjectMail)
            .body(htmlContent).build();

    MailRequest secondMail = MailRequest.builder().technicalReference(superAdminId)
            .from(this.senderMail)
            .to(List.of(new Email(user.getEmail(), "")))
            .trackOpenEvent(false).trackClickEvent(false)
            .subject(this.userAccountCreationSecondSubjectMail)
            .body(secondHtmlContent).build();

    this.hubDigitalFlowFeignClient.sendMail(mailRequest, null, "", "",
            BearerAuthentication.PREFIX_TOKEN.concat(hubToken));

    this.hubDigitalFlowFeignClient.sendMail(secondMail, null, "", "",
            BearerAuthentication.PREFIX_TOKEN.concat(hubToken));
  }

  private String getDefaultResetPasswordHtmlContentWithReplacement(String replacement) {
    try {
      String result = HtmlUtils.replaceResetPasswordEmailForm(
          this.getResourceAsString(this.defaultResetPasswordEmailFormHtmlFile), replacement);
      return result;
    } catch (Exception e) {
      log.info("Error: {}", e.getMessage());
      return replacement;
    }
  }

  private String getDefaultUserAccountCreationNotificationContentWithReplacement(Map<String, String> replacements) {
    try {
      String result = HtmlUtils.replaceCreationAccountEmailForm(
              this.getResourceAsString(this.defaultUserAccountCreationNotificationEmailHtmlFile), replacements);
      return result;
    } catch (Exception e) {
      log.info("Error: {}", e.getMessage());
      return replacements.toString();
    }
  }

  private String getDefaultSecondNotificationContentWithReplacement(Map<String, String> replacements) {
    try {
      String result = HtmlUtils.replaceCreationAccountEmailForm(
              this.getResourceAsString(this.defaultUserAccountCreationSecondNotificationEmailHtmlFile), replacements);
      return result;
    } catch (Exception e) {
      log.info("Error: {}", e.getMessage());
      return replacements.toString();
    }
  }

  private String getResourceAsString(Resource resource) {
    try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
      return FileCopyUtils.copyToString(reader);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private Optional<UserRepresentation> getUserRepresentation(String email) {
    return this.keycloakService.getUserRepresentationByEmail(email);
  }

  private boolean isValidEmail(String email) {
    return EmailValidatorUtils.validateEmail(email);
  }

  /**
   * Handling process of update password of a user.
   *
   * @param dto refers to object of {@link UserResetPasswordDto}
   * @return object of {@link UserResetPasswordDto} after reset
   */
  public UserResetPasswordDto resetPassword(UserResetPasswordDto dto) {
    var user = this.getUserRequestResetPassword(dto.getToken())
        .orElseThrow(() -> new KeycloakUserNotFound(KEYCLOAK_USER_NOT_FOUND));

    // Verified request with a database & keycloak server.
    if (!(user != null && isUserPresentInKeycloakServer(user))) {
      throw new UserNotFoundException(KEYCLOAK_USER_NOT_FOUND);
    }
    // Validate with expired date.
    if (UserResetPasswordValidator.isTokenExpired(user.getExpiredDate())) {
      throw new TokenExpiredDateException(TOKEN_EXPIRED_DATE);
    }
    // Update user with keycloak server.
    user.setPassword(dto.getPassword());
    this.resetUserPasswordKeycloakServer(user);
    // Remove user from DB.
    this.userRequestResetPasswordRepository.deleteByEmail(user.getEmail());
    return dto;
  }

  private void resetUserPasswordKeycloakServer(UserRequestResetPassword userRequestResetPassword) {
    User user = this.modelMapper.map(userRequestResetPassword, User.class);
    user.setId(userRequestResetPassword.getKeycloakUserId());
    try {
      this.keycloakService.resetPassword(user);
    } catch (Exception e) {
      log.error("Failed to reset user's password", e);
      throw new UserResetPasswordException(USER_RESET_PASSWORD_FAIL);
    }
  }

  public UserInfoRequestUpdatePasswordDto updateUserPassword(
      UserInfoRequestUpdatePasswordDto userInfo) {
    // Verified new password & confirm password.
    log.info("UserService - Start updating password action");
    if (!Objects.equals(userInfo.getNewPassword(), userInfo.getConfirmPassword())) {
      throw new UserPasswordNotMatchException("Password not match!");
    }

    // Get user from keycloak server by token.
    log.info("UserService - find logged user in Keycloak server");
    User keycloakUser;
    try {
      keycloakUser = this.keycloakService.getUserInfo();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new KeycloakUserNotFound(KEYCLOAK_USER_NOT_FOUND);
    }

    // Validate current password.

    log.info("UserService - Validation of current password for not unblocked account action");
    if (!userInfo.getActionType().equals(PasswordActionType.UNBLOCKED)
        && !this.keycloakService.isUserPasswordCredentialValid(
        this.keycloakSpringBootProperties.getResource(),
        (String) this.keycloakSpringBootProperties.getCredentials().get("secret"),
        keycloakUser.getUsername(),
        userInfo.getCurrentPassword())) {
      throw new InvalidUserPasswordException("Password invalid!");
    }

    // Update password of user admin.
    if (this.isAdmin()) {
      log.info("UserService - Updating password for current user with Admin role");
      keycloakUser.setPassword(userInfo.getNewPassword());
      this.updateUserPassword(keycloakUser);
      log.info("UserService - Calling archive password action");
      UserEntity userEntity = this.userRepository
              .findByTechnicalRefAndIsActiveTrue(keycloakUser.getId())
              .orElseThrow(() -> new UserNotFoundException(keycloakUser.getId()));

      if(this.passwordArchiveService.isNotInPasswordArchive(userEntity, userInfo.getNewPassword()))
        throw new PasswordAlreadyUsedException("Password already used!");

      this.passwordArchiveService.addPasswordToArchive(userEntity, userInfo.getNewPassword());
    } else {
      // Update password of normal user.
      log.info("UserService - Updating password for current user with User role");
      UserEntity userEntity = this.userRepository
          .findByTechnicalRefAndIsActiveTrue(keycloakUser.getId())
          .orElseThrow(() -> new UserNotFoundException(keycloakUser.getId()));

      if(this.passwordArchiveService.isNotInPasswordArchive(userEntity, userInfo.getNewPassword()))
        throw new PasswordAlreadyUsedException("Password already used!");

      User user = new User(
          userEntity.getTechnicalRef(),
          userEntity.getUsername(),
          userEntity.getFirstName(),
          userEntity.getLastName(),
          userEntity.getEmail(),
          new Date(),
          userInfo.getNewPassword());

      log.info("UserService - End of Updating password for logged user");
      this.updateUserPassword(user);

      log.info("UserService - Calling archive password action");
      this.passwordArchiveService.addPasswordToArchive(userEntity, userInfo.getNewPassword());
    }
    return userInfo;
  }

  /**
   * Update user password in keycloak server.
   *
   * @param user - object of {@link User}.
   */
  private void updateUserPassword(User user) {
    try {
      this.keycloakService.resetPassword(user);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  private boolean isUserPresentInKeycloakServer(UserRequestResetPassword user) {
    var userKeycloak = this.keycloakService.getUserInfo(user.getUsername());
    return userKeycloak != null;
  }

  private Optional<UserRequestResetPassword> getUserRequestResetPassword(String token) {
    return this.userRequestResetPasswordRepository.findByToken(token);
  }

  /**
   * To validate the expiration duration of token.
   *
   * @param token refers to the identity to validate
   * @return true if expire or the token is not found nor false
   */
  @Transactional(readOnly = true)
  public boolean isExpire(String token) {

    var user = getUserRequestResetPassword(token);
    return user.map(
            userRequestResetPassword -> UserResetPasswordValidator.isTokenExpired(
                userRequestResetPassword.getExpiredDate()))
        .orElse(true);
  }

  /**
   * Generate date expired of token.
   *
   * @return date of expired
   */
  private Date getDurationOfTokenExpired() {
    return UserResetPasswordValidator.getExpiredDate(
        Math.toIntExact(this.durationTokenExpired.toMinutes()));
  }

  @Autowired
  public void setDurationTokenExpired(
      @Qualifier(UserResetPasswordValidator.DURATION_TOKEN_EXPIRED) Duration durationTokenExpired) {
    this.durationTokenExpired = durationTokenExpired;
  }

  /**
   * To validate the user credential before response.
   *
   * @param userCredentialRequest refer to object of {@link UserCredentialRequest}
   * @return the object of {@link UserCredentialResponse} after validation
   */
  public UserCredentialResponse validateUserCredential(
      UserCredentialRequest userCredentialRequest) {
    return !this.keycloakService.isUsernameAvailable(userCredentialRequest.getUsername())
        ? UserCredentialResponse.builder().usernameValid(true).passwordValid(false).build()
        : UserCredentialResponse.builder().usernameValid(false).passwordValid(true).build();
  }

  /**
   * To get user details both user active and user deleted from keycloak (but in the database the
   * user is soft delete).
   *
   * @param id refer to id {@link User#getId()} of the user.
   * @return object info of the user based on the id.
   * @see #getUserDetailBuilder(UserEntity)
   */
  public UserDetail getUserDetailById(long id) {
    var userEntity =
        this.userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));

    return this.getUserDetailBuilder(userEntity);
  }

  /**
   * check user before return user details.
   *
   * @param userEntity refer to Object {@link UserEntity}.
   * @return {@link UserDetail}.
   */
  public UserDetail checkAndGetUserDetails(UserEntity userEntity) {
    final String technicalRef = userEntity.getTechnicalRef();
    if (this.keycloakService.findUserById(technicalRef).isEmpty()) {
      log.error(MISSING_USER_KEYCLOAK.concat(technicalRef));
      throw new UserKeycloakServiceExceptionHandler(USER_NOT_FOUND.concat(technicalRef));
    }
    return this.getUserDetailBuilder(userEntity);
  }

  /**
   * Get user detail builder.
   *
   * @param entity refer to Object {@link UserEntity}.
   * @return {@link UserDetail}.
   */
  private UserDetail getUserDetailBuilder(UserEntity entity) {
    return UserDetail.builder()
        .firstName(entity.getFirstName())
        .lastName(entity.getLastName())
        .username(entity.getUsername())
        .serviceName(entity.getDepartment().getName())
        .serviceId(entity.getDepartment().getId())
        .divisionName(entity.getDepartment().getDivision().getName())
        .divisionId(entity.getDepartment().getDivision().getId())
        .clientName(entity.getDepartment().getDivision().getClient().getName())
        .clientId(entity.getDepartment().getDivision().getClient().getId())
        .technicalRef(entity.getTechnicalRef())
        .ownerId(entity.getId())
        .build();
  }

  /**
   * Get user detail.
   *
   * @return {@link UserDetail}
   */
  public UserDetail getUserDetail() {
    final var technicalRef = this.getUserId();
    var userEntity = this.userRepository
        .findByTechnicalRefAndIsActiveTrue(technicalRef)
        .orElseThrow(() -> new UserNotFoundException(technicalRef));

    // Check if user is not existed in Keycloak
    return checkAndGetUserDetails(userEntity);
  }

  public List<UserDetail> getUserDetails(long serviceId) {
    String keycloakUserId = this.keycloakService.getUserInfo().getId();

    var userEntities = this.userRepository
        .findAllByDepartmentIdAndTechnicalRefAndIsActiveTrue(serviceId, keycloakUserId)
        .orElseThrow(() -> new UserNotFoundException(keycloakUserId));

    if (this.keycloakService.findUserById(keycloakUserId).isEmpty()) {
      log.error(MISSING_USER_KEYCLOAK + keycloakUserId);
      throw new UserKeycloakServiceExceptionHandler(USER_NOT_FOUND + keycloakUserId);
    }

    return userEntities.stream()
        .map(this::getUserDetailBuilder)
        .collect(Collectors.toList());
  }

  /**
   * To retrieve the username of super admin.
   *
   * @return username of the super admin
   */
  public String getUsernameSuperAdmin() {
    return this.keycloakService
        .findUserById(adminUserId)
        .orElseThrow(() -> new UserKeycloakServiceExceptionHandler("User is not found!"))
        .getUsername();
  }

  /**
   * Method used to get clientId.
   *
   * @return value of {@link Long}
   */
  private Long getClientId(String username) {
    var userId = this.keycloakService.getUserInfo(username).getId();
    return this.clientRepository
        .getClientIdByUserId(userId)
        .orElseThrow(() -> new ProfileNotBelongToServiceException(username));
  }

  /**
   * To receive the service of each user.
   *
   * @param usernames refers to username of a user
   * @return the collection of {@link UserServiceResponseDto} object
   */
  public List<UserServiceResponseDto> getUserService(List<String> usernames) {
    if (usernames.isEmpty()) {
      return List.of();
    }
    final var specification = UserSpecification.usernameIn(usernames.stream().distinct().collect(
        Collectors.toUnmodifiableList()));
    return this.userRepository.findAll(specification).stream()
        .map(u -> UserServiceResponseDto.builder()
            .serviceId(u.getDepartment().getId())
            .username(u.getUsername())
            .build())
        .collect(Collectors.toList());
  }
}
