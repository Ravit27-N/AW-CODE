package com.tessi.cxm.pfl.ms5.service;

import static com.tessi.cxm.pfl.shared.utils.ProfileConstants.CXM_USER_MANAGEMENT;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.tessi.cxm.pfl.ms5.constant.Functionality;
import com.tessi.cxm.pfl.ms5.constant.PrivilegeLevelValidator;
import com.tessi.cxm.pfl.ms5.constant.ProfileConstants;
import com.tessi.cxm.pfl.ms5.constant.ProfileConstants.Attribute;
import com.tessi.cxm.pfl.ms5.constant.UserManagementConstants;
import com.tessi.cxm.pfl.ms5.dto.LoadOrganizationUserImpl;
import com.tessi.cxm.pfl.ms5.dto.PrivilegeProjection;
import com.tessi.cxm.pfl.ms5.dto.PrivilegeProjectionImpl;
import com.tessi.cxm.pfl.ms5.dto.ProfileDetailDto;
import com.tessi.cxm.pfl.ms5.dto.ProfileDto;
import com.tessi.cxm.pfl.ms5.dto.ProfileFilterCriteria;
import com.tessi.cxm.pfl.ms5.dto.UserDetailDTO;
import com.tessi.cxm.pfl.ms5.dto.UserProfilePrivilege;
import com.tessi.cxm.pfl.ms5.dto.UserProfileProjection;
import com.tessi.cxm.pfl.ms5.dto.UserProfileProjectionImpl;
import com.tessi.cxm.pfl.ms5.dto.UserProfilesDto;
import com.tessi.cxm.pfl.ms5.entity.Client;
import com.tessi.cxm.pfl.ms5.entity.Department;
import com.tessi.cxm.pfl.ms5.entity.Functionalities;
import com.tessi.cxm.pfl.ms5.entity.LoadUserPrivilegeDetailsImp;
import com.tessi.cxm.pfl.ms5.entity.Privilege;
import com.tessi.cxm.pfl.ms5.entity.Profile;
import com.tessi.cxm.pfl.ms5.entity.ProfileDetails;
import com.tessi.cxm.pfl.ms5.entity.UserEntity;
import com.tessi.cxm.pfl.ms5.entity.UserProfiles;
import com.tessi.cxm.pfl.ms5.entity.projection.LoadUserPrivilegeDetails;
import com.tessi.cxm.pfl.ms5.exception.ClientNotFoundException;
import com.tessi.cxm.pfl.ms5.exception.FunctionalityKeyNotAllowedException;
import com.tessi.cxm.pfl.ms5.exception.FunctionalityKeyNotFoundException;
import com.tessi.cxm.pfl.ms5.exception.PrivilegeKeyNotFoundException;
import com.tessi.cxm.pfl.ms5.exception.ProfileNameDuplicateException;
import com.tessi.cxm.pfl.ms5.exception.ProfileNotFoundException;
import com.tessi.cxm.pfl.ms5.exception.UserKeycloakServiceExceptionHandler;
import com.tessi.cxm.pfl.ms5.exception.UserNotFoundException;
import com.tessi.cxm.pfl.ms5.repository.ClientRepository;
import com.tessi.cxm.pfl.ms5.repository.DepartmentRepository;
import com.tessi.cxm.pfl.ms5.repository.ProfileDetailsRepository;
import com.tessi.cxm.pfl.ms5.repository.ProfileRepository;
import com.tessi.cxm.pfl.ms5.repository.UserProfileRepository;
import com.tessi.cxm.pfl.ms5.repository.UserRepository;
import com.tessi.cxm.pfl.ms5.service.specification.DepartmentSpecification;
import com.tessi.cxm.pfl.ms5.service.specification.ProfileSpecification;
import com.tessi.cxm.pfl.ms5.util.PrivilegeKeyValidator;
import com.tessi.cxm.pfl.shared.auth.AuthenticationUtils;
import com.tessi.cxm.pfl.shared.exception.BadRequestException;
import com.tessi.cxm.pfl.shared.exception.ModificationLevelNotExistException;
import com.tessi.cxm.pfl.shared.exception.UserAccessDeniedExceptionHandler;
import com.tessi.cxm.pfl.shared.exception.VisibilityLevelNotExistException;
import com.tessi.cxm.pfl.shared.model.SharedUserEntityDTO;
import com.tessi.cxm.pfl.shared.model.UserPrivilegeDetails;
import com.tessi.cxm.pfl.shared.model.UserPrivilegeDetailsOwner;
import com.tessi.cxm.pfl.shared.model.UsersRelatedToPrivilege;
import com.tessi.cxm.pfl.shared.service.AbstractCrudService;
import com.tessi.cxm.pfl.shared.service.keycloak.KeycloakService;
import com.tessi.cxm.pfl.shared.service.restclient.ModificationLevel;
import com.tessi.cxm.pfl.shared.service.restclient.VisibilityLevel;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.ws.rs.NotFoundException;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang.StringUtils;
import org.keycloak.representations.idm.UserRepresentation;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

/**
 * Handle business logic of {@link ProfileService}.
 *
 * @author Sokhour LACH
 * @author Pisey CHORN
 * @since 07/12/21
 */
@Log4j2
@Service
@Transactional
public class ProfileService extends AbstractCrudService<ProfileDto, Profile, Long>
    implements AdminService {

  private final ProfileRepository profileRepository;
  private final ProfileDetailsRepository profileDetailsRepository;
  private final ClientRepository clientRepository;
  private final UserProfileRepository userProfileRepository;
  private final ClientService clientService;
  private final UserRepository userRepository;
  private final ObjectMapper objectMapper;
  private final DepartmentRepository departmentRepository;

  @Setter
  @Value("${cxm.user.admin-id}")
  private String adminUserId;

  @Value("classpath:admin-profile-privileges.json")
  private Resource adminProfilePrivilegesJsonResource;

  private UserProfilePrivilege adminProfilePrivileges;

  /**
   * The constructor used to initial required bean.
   *
   * @param modelMapper bean of {@link ModelMapper}
   * @param profileRepository bean of {@link ProfileRepository}
   * @param profileDetailsRepository bean of {@link ProfileDetailsRepository}
   * @param clientRepository bean of {@link ClientRepository}
   * @param userProfileRepository bean of {@link UserProfileRepository}
   * @param objectMapper bean of {@link ObjectMapper}
   */
  public ProfileService(
      ModelMapper modelMapper,
      ProfileRepository profileRepository,
      ProfileDetailsRepository profileDetailsRepository,
      ClientRepository clientRepository,
      UserProfileRepository userProfileRepository,
      ClientService clientService,
      UserRepository userRepository,
      KeycloakService keycloakService,
      ObjectMapper objectMapper,
      DepartmentRepository departmentRepository) {
    super(keycloakService);
    this.objectMapper = objectMapper;
    this.setRepository(profileRepository);
    this.clientRepository = clientRepository;
    this.profileDetailsRepository = profileDetailsRepository;
    this.userProfileRepository = userProfileRepository;
    this.clientService = clientService;
    this.userRepository = userRepository;
    this.modelMapper = modelMapper;
    this.profileRepository = profileRepository;
    this.departmentRepository = departmentRepository;
  }

  @Override
  public UserRepository getUserRepository() {
    return this.userRepository;
  }

  /**
   * Assign a specific profile to a specific client.
   *
   * @param profileId Profile's id.
   * @param clientId Client's id.
   */
  public ProfileDto assignProfileToClient(Long profileId, Long clientId) {
    var profile =
        this.profileRepository
            .findById(profileId)
            .orElseThrow(() -> new ProfileNotFoundException(profileId));
    var client =
        this.clientRepository
            .findById(clientId)
            .orElseThrow(() -> new ClientNotFoundException(clientId));
    profile.setClient(client);
    return this.mapData(this.profileRepository.save(profile), new ProfileDto());
  }

  /**
   * Assign a specific profile to a specific user.
   *
   * @param profileId Profile's id.
   */
  @Transactional(readOnly = true)
  public UserProfilesDto assignProfileToUser(Long profileId, String username) {
    var profile =
        this.profileRepository
            .findById(profileId)
            .orElseThrow(() -> new ProfileNotFoundException(profileId));

    var user = this.keycloakService.getUserInfo(username);

    if (!this.getUsername().equalsIgnoreCase(username) && user == null) {
      throw new UserKeycloakServiceExceptionHandler(
          String.format("User is not found: %s.", username));
    }
    var userEntity = this.userRepository.getById(Long.valueOf(user.getId()));
    var userProfiles = new UserProfiles(userEntity, profile);
    userProfiles.setCreatedBy(this.getUsername());
    return this.modelMapper.map(
        this.userProfileRepository.save(userProfiles), UserProfilesDto.class);
  }

  /**
   * Assign a specific profile to current user.
   *
   * @param profileId Profile's id.
   */
  public UserProfilesDto assignProfileToCurrentUser(Long profileId) {
    return this.assignProfileToUser(profileId, this.getUsername());
  }

  /**
   * Create profile and profile detail.
   *
   * @param profileDto object of {@link ProfileDto}
   * @return object of {@link ProfileDto}
   */
  @Transactional(rollbackFor = Exception.class)
  public ProfileDto createProfile(ProfileDto profileDto) {
    return createProfile(profileDto, null);
  }

  public ProfileDto createProfile(ProfileDto profileDto, Client client) {
    final boolean isAdmin = this.isAdmin();

    validateUserCreateProfile(isAdmin);

    client = getClientInfo(profileDto, client, isAdmin);
    checkDuplicateProfileName(profileDto.getName(), client);

    var userId = this.keycloakService.getUserInfo().getId();

    UserEntity userEntity = getUserEntityValidateByRole(userId, isAdmin);

    this.validateFunctionality(profileDto);
    this.validatePrivilege(profileDto);

    // only allow create profile with same lv or below current user access
    if (!isAdmin) {
      var filteredAccessProfile =
          filterAllowedAccessLevel(profileDto, this.getUserProfileAccessLevel());
      profileDto.setFunctionalities(filteredAccessProfile);
    }

    var profile = this.mapEntity(profileDto, new Profile());
    profile.setCreatedBy(this.getUsername());

    if (userEntity != null) {
      profile.setOwnerId(userEntity.getId());
    }

    profile.setClient(client);
    profile.addProfileDetails(this.mapProfileDetailDtoToEntity(profileDto.getFunctionalities()));

    return this.mapData(this.profileRepository.save(profile), profileDto);
  }

  /**
   * Get user entity by validate by user role.
   *
   * @param userId refer to type {@link String}.
   * @param isAdmin refer to type {@link Boolean}.
   * @return {@link UserEntity}.
   */
  private UserEntity getUserEntityValidateByRole(String userId, boolean isAdmin) {
    if (!isAdmin) {
      return this.userRepository
          .findByTechnicalRefAndIsActiveTrue(userId)
          .orElseThrow(() -> new UserNotFoundException(userId));
    }
    return null;
  }

  /**
   * Validate user created profile by user role.
   *
   * @param isAdmin refer to type {@link Boolean}.
   */
  private void validateUserCreateProfile(boolean isAdmin) {
    if (!isAdmin
        && this.notContainsPrivilege(
            CXM_USER_MANAGEMENT,
            CXM_USER_MANAGEMENT.concat(
                "_".concat(UserManagementConstants.Profile.CREATE)))) {
      throw new UserAccessDeniedExceptionHandler();
    }
  }

  /**
   * Get user information by checking user role.
   *
   * @param profileDto refer to {@link Object} {@link ProfileDto}.
   * @param client refer to {@link Object} {@link Client}.
   * @param isAdmin refer to type {@link Boolean}.
   * @return {@link Client}.
   */
  private Client getClientInfo(ProfileDto profileDto, Client client, boolean isAdmin) {
    if (isAdmin) {
      if (Objects.nonNull(client)) {
        return client;
      }
      if (Objects.isNull(profileDto.getClientId()) || profileDto.getClientId() <= 0) {
        throw new BadRequestException("Client's ID cannot be null and must be greater than 0");
      }
      return this.clientService.findEntity(profileDto.getClientId());
    }
    return this.clientService.findEntity(this.getClientId());
  }

  /**
   * To check the duplicate name of {@link Profile}.
   *
   * @param profileName refer to name of {@link Profile}
   * @param client refer to object of {@link Client}
   */
  private void checkDuplicateProfileName(String profileName, Client client) {
    if (!this.isAdmin()) {
      if (this.isDuplicateName(profileName, this.getClientId())) {
        throw new ProfileNameDuplicateException(profileName);
      }
    } else {
      if (client != null) {
        if (this.isDuplicateName(profileName, client.getId())) {
          throw new ProfileNameDuplicateException(profileName);
        }
      } else {
        if (this.isProfileExist(profileName)) {
          throw new ProfileNameDuplicateException(profileName);
        }
      }
    }
  }

  /**
   * Method used to validate unique name.
   *
   * @param name refer to name of {@link Profile}
   * @param clientId refer to the client id of {@link Profile}
   * @return value of {@link Boolean}
   * @see Profile#getName()
   */
  @Transactional(readOnly = true)
  public boolean isDuplicateName(String name, Long clientId) {
    if (this.isAdmin()) {
      return this.profileRepository.existsAllByNameIgnoreCaseAndClientId(name, clientId);
    }

    return this.profileRepository.existsAllByNameIgnoreCaseAndClientId(
        name, Objects.isNull(clientId) ? this.getClientId() : clientId);
  }

  public boolean isProfileExist(String name) {
    return this.profileRepository.existsAllByNameIgnoreCase(name);
  }

  /**
   * Method used to get clientId.
   *
   * @return value of {@link Long}
   */
  private Long getClientId() {
    return this.clientService.getClientId();
  }

  /**
   * Method used to retrieve profiles with pageable and custom filtering.
   *
   * @param page int the page request that contain page, pageSize, sortByField, and sortDirection
   * @param pageSize int
   * @param sortDirection string
   * @param filter string value for filtering with specification such as name, displayName of {@link
   *     Profile}
   * @return content {@link Profile} list converted to {@link ProfileDto} wrapped by {@link Page}
   */
  @Transactional(readOnly = true)
  public Page<ProfileDto> findAll(int page, int pageSize, String sortDirection, String sortByField, String filter, List<Long> clientIds) {
    Pageable pageable = buildPageRequest(page, pageSize, sortDirection, sortByField);

    final boolean isAdmin = this.isAdmin();
    var specification = Specification.where(ProfileSpecification.containName(filter));

    if (!isAdmin) {
      checkUsersRelatedToPrivilege();
      specification =
          this.getProfileSpecification(specification)
              .and(ProfileSpecification.equalClientId(this.getClientId()));

      if (!CollectionUtils.isEmpty(clientIds))
        throw new UserAccessDeniedExceptionHandler("Only super admin allows to list profiles by clientIds");
    } else {
      if (!CollectionUtils.isEmpty(clientIds))
        specification = specification.and(ProfileSpecification.containsClientIds(clientIds));
    }

    return this.profileRepository
        .findAll(specification, pageable)
        .map(
            dt -> {
              var profileDto = this.modelMapper.map(dt, ProfileDto.class);
              if (dt.getClient() != null) {
                profileDto.setClientName(dt.getClient().getName());
              }

              return profileDto;
            });
  }

  private Specification<Profile> getProfileSpecification(Specification<Profile> specification) {
    specification =
        specification.and(
            Specification.where(
                ProfileSpecification.containOwnerIds(this.getUserIdsInPrivilege())));
    return specification;
  }

  /**
   * To load the list of profiles.
   *
   * @return content {@link Profile} list converted to {@link ProfileFilterCriteria}.
   */
  @Override
  @Transactional(readOnly = true)
  public List<ProfileDto> findAll() {
    if (this.isAdmin()) {
      return this.profileRepository.findAll().stream()
          .map(e -> this.modelMapper.map(e, ProfileDto.class))
          .collect(Collectors.toList());
    }
    checkUsersRelatedToPrivilege();

    Specification<Profile> specification = Specification.where(null);
    specification = getProfileSpecification(specification);

    return this.profileRepository.findAll(specification).stream()
        .map(e -> this.modelMapper.map(e, ProfileDto.class))
        .collect(Collectors.toList());
  }

  /**
   * Method used to retrieve profiles with filtering on the associated client id.
   */
  @Transactional(readOnly = true)
  public Page<ProfileDto> findAllWithClientID(int page, int pageSize, String sortDirection, String sortByField, String filter, Long clientIDConnectedUser, List<Long> clientIds) {
    final boolean isAdmin = this.isAdmin();
    Pageable pageable = buildPageRequest(page, pageSize, sortDirection, sortByField);
    var specification = Specification.where(ProfileSpecification.containName(filter));

    if(!isSuperAdmin()){
      log.info("Service Profile - findAllWithClientID : debut traitement des specs pour les profiles non superAdmin");
      /* A non-administrator user sees the profiles of his client. */
      if(StringUtils.isNotBlank(filter)){
        specification = Specification.where(ProfileSpecification.containName(filter))
            .and(ProfileSpecification.equalClientId(clientIDConnectedUser));
      }else{
        specification = Specification.where(ProfileSpecification.equalClientId(clientIDConnectedUser));
      }

      checkUsersRelatedToPrivilege();

      specification = getProfileSpecification(specification);
      log.info("Service Profile - findAllWithClientID : fin traitement des specs pour les profiles non superAdmin");
    }

    if (!CollectionUtils.isEmpty(clientIds)) {
      if (!isAdmin) {
        throw new UserAccessDeniedExceptionHandler(
            "Only super admin allows to list profiles by clientIds");
      }
      log.info("Service Profile - findAllWithClientID : traitement des specs si la list clients id est valide");
      specification = specification.and(ProfileSpecification.containsClientIds(clientIds));
    }

    log.info("Service Profile - findAllWithClientID : fin des traitements");
    return this.profileRepository
        .findAll(specification, pageable)
        .map(
            dt -> {
              var profileDto = this.modelMapper.map(dt, ProfileDto.class);
              if (dt.getClient() != null) {
                profileDto.setClientName(dt.getClient().getName());
              }

              return profileDto;
            });
  }

  /**
   * To load the list of profile criteria for filtering users by profile.
   *
   * @return content {@link Profile} list converted to {@link ProfileFilterCriteria}.
   */
  @Transactional(readOnly = true)
  public List<ProfileFilterCriteria> findProfileCriteria(String sortDirection) {
    if (this.isAdmin()) {
      Direction direction = sortDirection.equalsIgnoreCase("desc") ? Direction.DESC : Direction.ASC;
      Sort sort = Sort.by(direction, "name");
      return this.profileRepository.findAll(sort).stream()
          .map(e -> this.modelMapper.map(e, ProfileFilterCriteria.class))
          .collect(Collectors.toList());
    }
    checkUsersRelatedToPrivilege();
    return this.profileRepository.findAllByCreatedByIn(getUsersInPrivilege()).stream()
        .map(e -> this.modelMapper.map(e, ProfileFilterCriteria.class))
        .collect(Collectors.toList());
  }

  private List<String> getUsersInPrivilege() {
    return loadAllUsernames(
        true,
        Optional.of(CXM_USER_MANAGEMENT),
        CXM_USER_MANAGEMENT.concat(
            "_" + UserManagementConstants.Profile.LIST),
        "");
  }

  private List<Long> getUserIdsInPrivilege() {
    return this.loadAllUserIds(
        true,
        Optional.of(com.tessi.cxm.pfl.shared.utils.ProfileConstants.CXM_USER_MANAGEMENT),
        com.tessi.cxm.pfl.shared.utils.ProfileConstants.CXM_USER_MANAGEMENT.concat(
            "_" + UserManagementConstants.Profile.LIST),
        "");
  }

  @Transactional(readOnly = true)
  public Profile findEntity(long id) {
    return this.profileRepository.findById(id).orElseThrow(() -> new ProfileNotFoundException(id));
  }

  /**
   * To get all profile details by identity of {@link Profile}.
   *
   * @param profileId id of {@link Profile}
   * @return collection of {@link ProfileDetailDto}
   */
  @Transactional(readOnly = true)
  public List<ProfileDetailDto> getProfileDetailsByProfileId(long profileId) {
    return this.profileDetailsRepository.findAllByProfileId(profileId).stream()
        .map(e -> modelMapper.map(e, ProfileDetailDto.class))
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  @Override
  public ProfileDto findById(Long id) {

    if (!this.isAdmin()) {
      UsersRelatedToPrivilege userPrivilegeRelated =
          this.getUserPrivilegeRelated(
              true,
              CXM_USER_MANAGEMENT,
              CXM_USER_MANAGEMENT.concat(
                  "_".concat(UserManagementConstants.Profile.EDIT)));

      if (StringUtils.isBlank(userPrivilegeRelated.getLevel())
          || this.checkIfUserCanNotVisible(id, userPrivilegeRelated)) {
        throw new UserAccessDeniedExceptionHandler();
      }
      final var profile = this.findEntity(id);
      var profileDto = this.mapData(profile, new ProfileDto());
      profileDto.setFunctionalities(
          this.mapAll(profile.getProfileDetails(), ProfileDetailDto.class));
      return profileDto;
    }

    final var profile =
        this.profileRepository.findById(id).orElseThrow(() -> new ProfileNotFoundException(id));
    var profileDto = this.mapData(profile, new ProfileDto());
    profileDto.setFunctionalities(this.mapAll(profile.getProfileDetails(), ProfileDetailDto.class));
    return profileDto;
  }

  public Profile findById(Long id, String privKey, boolean isVisibility) {

    if (!this.isAdmin()) {
      UsersRelatedToPrivilege userPrivilegeRelated =
          this.getUserPrivilegeRelated(
              isVisibility,
              CXM_USER_MANAGEMENT,
              CXM_USER_MANAGEMENT.concat(
                  "_".concat(privKey)));

      if (StringUtils.isBlank(userPrivilegeRelated.getLevel())
          || this.checkIfUserCanNotVisible(id, userPrivilegeRelated)) {
        throw new UserAccessDeniedExceptionHandler();
      }
      return this.findEntity(id);
    }

    return this.profileRepository.findById(id).orElseThrow(() -> new ProfileNotFoundException(id));
  }

  // Combine user level profile into list functionalities
  private List<UserProfileProjection> getUserProfileAccessLevel() {
    final var loadAllUsersBaseOnLevel = this.loadAllUsersBaseOnLevel(getUsername());
    var usersByLevel = loadAllUsersBaseOnLevel.getT2();
    return this.combineUserProfileLevel(usersByLevel);
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public ProfileDto update(ProfileDto dto) {

    final boolean isAdmin = this.isAdmin();

    if (!isAdmin) {
      this.checkUserModificationPrivilege(dto.getId(), UserManagementConstants.Profile.MODIFY);
    }

    this.validateFunctionality(dto);
    this.validatePrivilege(dto);

    var profile = this.findById(dto.getId(), UserManagementConstants.Profile.MODIFY, false);
    profile.setLastModifiedBy(this.getUsername());
    profile.setName(dto.getName());
    profile.setDisplayName(dto.getDisplayName());
    profile.setLastModified(new Date());
    if (!isAdmin) {
      var client = this.clientService.findEntity(this.getClientId());
      profile.setClient(client);
    } else {
      this.profileRepository
          .findById(dto.getId())
          .ifPresent(
              entity -> {
                if (entity.getClient() != null) {
                  profile.setClient(entity.getClient());
                }
              });
    }

    if (Objects.nonNull(dto.getClientId()) && profile.getClient().getId() != dto.getClientId()) {
      throw new UserAccessDeniedExceptionHandler();
    }

    if (isAdmin) {
      profile.removeProfileDetails(profile.getProfileDetails());
    } else {
      validateProfileBeforeModify(dto, profile);
    }

    profile.addProfileDetails(this.mapProfileDetailDtoToEntity(dto.getFunctionalities()));

    return mapData(this.profileRepository.save(profile), dto);
  }

  /**
   * To verify and check the validation profile functional privilege keys before modify.
   *
   * @param dto refer to object of {@link ProfileDto}
   * @param profile refer to entity object of {@link Profile}
   */
  private void validateProfileBeforeModify(ProfileDto dto, Profile profile) {
    var profileAccessLv = this.getUserProfileAccessLevel();

    List<String> funcKeysEntity =
        profileAccessLv.stream()
            .map(UserProfileProjection::getFunctionalityKey)
            .collect(Collectors.toList());
    List<String> funcKeysDto =
        dto.getFunctionalities().stream()
            .map(ProfileDetailDto::getFunctionalityKey)
            .collect(Collectors.toList());
    List<String> ownFuncKeys =
        profileAccessLv.stream()
            .map(UserProfileProjection::getFunctionalityKey)
            .collect(Collectors.toList());

    @SuppressWarnings("unchecked")
    List<String> rejectFuncKeys =
        ListUtils.removeAll(ListUtils.removeAll(funcKeysDto, funcKeysEntity), ownFuncKeys);

    if (!CollectionUtils.isEmpty(rejectFuncKeys)) {
      throw new FunctionalityKeyNotAllowedException(rejectFuncKeys);
    }

    // get functionality keys that are requested from client app.
    @SuppressWarnings("unchecked")
    List<String> functionalRemoved = ListUtils.removeAll(funcKeysEntity, funcKeysDto);

    var filteredAccessProfile = filterAllowedAccessLevel(dto, profileAccessLv);

    // remove profile details by collection of functional keys
    profile
        .getProfileDetails()
        .removeIf(detail -> functionalRemoved.contains(detail.getFunctionalityKey()));

    var clearEntityState =
        profile.getProfileDetails().stream()
            .filter(x -> filteredAccessProfile.stream().anyMatch(y -> x.getId() == y.getId()))
            .collect(Collectors.toList());
    profile.removeProfileDetails(clearEntityState);

    dto.setFunctionalities(filteredAccessProfile);
  }

  /**
   * To delete a profile from database and another table related profile by profile id.
   *
   * @param id refer to profile id
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public void delete(Long id) {
    if (this.isAdmin()) {
      var entity =
          this.profileRepository.findById(id).orElseThrow(() -> new ProfileNotFoundException(id));
      userProfileRepository.deleteAllByProfile(entity);
      profileRepository.delete(entity);
      return;
    }

    var entity = this.findEntity(id);
    userProfileRepository.deleteAllByProfile(entity);
    profileRepository.delete(entity);
  }

  private List<ProfileDetails> mapProfileDetailDtoToEntity(List<ProfileDetailDto> dto) {
    final var profileDetails = new ArrayList<ProfileDetails>();
    dto.forEach(
        dt -> {
          var detail = this.modelMapper.map(dt, ProfileDetails.class);
          detail.setCreatedBy(this.getUsername());
          profileDetails.add(detail);
        });
    return profileDetails;
  }

  /**
   * To retrieve key values pairs of {@link Functionality}.
   *
   * @param key refer to key of Functionality
   * @return return list of {@link Map} with {@link String} keys and {@link String} values.
   */
  public Map<String, Object> getFunctionalityPrivilege(String key) {
    if (Strings.isNullOrEmpty(key)) {
      return Map.of(Attribute.FUNCTIONALITIES, Functionality.getKeyValues());
    } else {
      return Map.of(Attribute.FUNCTIONALITIES, Functionality.getKeyValuesByKey(key));
    }
  }

  /**
   * To get the necessary information if {@link UserProfiles}.
   *
   * @return the collection of {@link UserProfileProjection}
   */
  public List<UserProfileProjection> getDetailsById() {
    return this.profileDetailsRepository.findAllByProfileIdIn(getProfileIdOfUser());
  }

  public List<UserProfileProjection> combineUserProfileLevel(
      Map<String, List<LoadUserPrivilegeDetails>> usersByLevel) {
    var clientFunctionalities = this.getClientFunctionalities();

    var collects =
        this.getDetailsById().stream()
            .filter(profile -> clientFunctionalities.contains(profile.getFunctionalityKey()))
            .collect(Collectors.groupingBy(UserProfileProjection::getFunctionalityKey));

    var finalUserProfile =
        collects.keySet().stream()
            .map(e -> getTopProfile(e, collects.get(e)))
            .collect(Collectors.toList());

    List<UserProfileProjection> userProfiles = new ArrayList<>();

    finalUserProfile.forEach(
        p -> {
          List<PrivilegeProjection> privileges =
              p.getPrivileges().stream()
                  .map(
                      privilege -> {
                        final Tuple2<List<Long>, List<Long>> ownerIdAndUsernameOfUserTuple4 =
                            getOwnerIdAndUsernameOfUserTuple2(
                                usersByLevel.get(
                                    Objects.requireNonNullElse(
                                            ModificationLevel.valuesByKey(
                                                privilege.getModificationLevel()),
                                            ModificationLevel.SPECIFIC)
                                        .name()),
                                usersByLevel.get(
                                    Objects.requireNonNullElse(
                                            VisibilityLevel.valueByKey(
                                                privilege.getVisibilityLevel()),
                                            VisibilityLevel.SPECIFIC)
                                        .name()));

                        return PrivilegeProjectionImpl.builder()
                            .modificationLevel(privilege.getModificationLevel())
                            .visibilityLevel(privilege.getVisibilityLevel())
                            .key(privilege.getKey())
                            .modificationOwners(ownerIdAndUsernameOfUserTuple4.getT1())
                            .visibilityOwners(ownerIdAndUsernameOfUserTuple4.getT2())
                            .build();
                      })
                  .collect(Collectors.toList());
          userProfiles.add(
              new UserProfileProjectionImpl(
                  p.getFunctionalityKey(),
                  privileges,
                  p.getVisibilityLevel(),
                  p.getModificationLevel()));
        });

    return userProfiles;
  }

  /**
   * Get user profile privilege base one current login use.
   *
   * @return a value present
   */
  @Transactional
  public UserProfilePrivilege getUserProfilePrivilege(boolean forceToChangePassword) {

    if(Boolean.TRUE.equals(forceToChangePassword)) {
      return UserProfilePrivilege.builder()
              .name(this.getUsername())
              .functionalities(new ArrayList<>())
              .id(0)
              .isAdmin(false)
              .build();
    }

    if (this.isSuperAdmin()) {
      return this.getAdminProfilePrivileges();
    }

    // filter functionality for the client.
    final var loadAllUsersBaseOnLevel = this.loadAllUsersBaseOnLevel(getUsername());

    var usersByLevel = loadAllUsersBaseOnLevel.getT2();
    var userProfiles = this.combineUserProfileLevel(usersByLevel);

    return UserProfilePrivilege.builder()
        .name(this.getUsername())
        .functionalities(userProfiles)
        .id(loadAllUsersBaseOnLevel.getT1())
        .isAdmin(this.isPlatformAdmin())
        .build();
  }

  private Tuple2<List<Long>, List<Long>> getOwnerIdAndUsernameOfUserTuple2(
      List<LoadUserPrivilegeDetails> userDetailInModification,
      List<LoadUserPrivilegeDetails> userDetailInVisibility) {
    final Iterator<LoadUserPrivilegeDetails> iteratorUserModi = userDetailInModification.iterator();
    final Iterator<LoadUserPrivilegeDetails> iteratorUserVisible =
        userDetailInVisibility.iterator();

    // for modification level.
    List<Long> ownerIdModi = new ArrayList<>();

    // for visibility level.
    List<Long> ownerIdVisible = new ArrayList<>();

    while (iteratorUserModi.hasNext() || iteratorUserVisible.hasNext()) {
      LoadUserPrivilegeDetails user;
      if (iteratorUserModi.hasNext()) {
        user = iteratorUserModi.next();
        ownerIdModi.add(user.getId());
      }
      if (iteratorUserVisible.hasNext()) {
        user = iteratorUserVisible.next();
        ownerIdVisible.add(user.getId());
      }
    }

    return Tuples.of(ownerIdModi, ownerIdVisible);
  }

  private Tuple2<Long, Map<String, List<LoadUserPrivilegeDetails>>> loadAllUsersBaseOnLevel(
      String username) {
    var userId = this.keycloakService.getUserInfo(username).getId();
    final Optional<UserEntity> userTechnical =
        userRepository.findByTechnicalRefAndIsActiveTrue(userId);
    if (userTechnical.isEmpty()) {
      return Tuples.of(0L, new HashMap<>());
    }

    final UserEntity userEntity = userTechnical.get();
    final var organization =
        userRepository.loadOrganizationUser(userId).orElse(new LoadOrganizationUserImpl());
    final List<LoadUserPrivilegeDetails> allUsersInClient =
        userRepository.getAllUsersInClient(organization.getClientId(), null);
    final List<LoadUserPrivilegeDetails> allUsersInDivision =
        userRepository.getAllUsersInDivision(organization.getDivisionId());
    final List<LoadUserPrivilegeDetails> allUsersInService =
        userRepository.getAllUsersInService(organization.getServiceId());
    return Tuples.of(
        userEntity.getId(),
        Map.of(
            VisibilityLevel.CLIENT.name(),
            allUsersInClient,
            VisibilityLevel.DIVISION.name(),
            allUsersInDivision,
            VisibilityLevel.SERVICE.name(),
            allUsersInService,
            VisibilityLevel.USER.name(),
            List.of(new LoadUserPrivilegeDetailsImp(userEntity.getId(), userEntity.getUsername())),
            ModificationLevel.OWNER.name(),
            List.of(new LoadUserPrivilegeDetailsImp(userEntity.getId(), userEntity.getUsername())),
            VisibilityLevel.SPECIFIC.name(),
            new ArrayList<>()));
  }

  /**
   * Get user profile base on functionality key.
   *
   * @param key is key of functionality like cxm_template
   * @param profiles collection of userProfile base on func key
   * @return a value is present
   */
  public UserProfileProjection getTopProfile(String key, List<UserProfileProjection> profiles) {
    var modificationLevel =
        ModificationLevel.valuesByKey(
            profiles.stream()
                .map(UserProfileProjection::getModificationLevel)
                .collect(Collectors.toList()));
    var visibilityLevel =
        VisibilityLevel.valuesByKey(
            profiles.stream()
                .map(UserProfileProjection::getVisibilityLevel)
                .collect(Collectors.toList()));
    var profileOfVisibility =
        Objects.requireNonNullElse(visibilityLevel, VisibilityLevel.SPECIFIC).getKey();
    var profileOfModify =
        Objects.requireNonNullElse(modificationLevel, ModificationLevel.SPECIFIC).getKey();
    var privilegeStream =
        profiles.stream()
            .flatMap(e -> e.getPrivileges().stream())
            .collect(Collectors.groupingBy(PrivilegeProjection::getKey));
    var privilegeStream1 =
        privilegeStream.keySet().stream()
            .map(e -> this.getPrivilegeFinal(e, privilegeStream.get(e)))
            .collect(Collectors.toList());
    return new UserProfileProjectionImpl(
        key, privilegeStream1, profileOfVisibility, profileOfModify);
  }

  /**
   * Get privilege of user base on privilege key.
   *
   * @param funKey is key of privilege like cxm_template_modify
   * @param privileges collection of privilege base on funKey
   * @see ProfileService#getPrivilege(String, List, List)
   */
  private PrivilegeProjection getPrivilegeFinal(
      String funKey, List<PrivilegeProjection> privileges) {
    var visibilityLevel =
        privileges.stream()
            .map(PrivilegeProjection::getVisibilityLevel)
            .collect(Collectors.toList());
    var modificationLevel =
        privileges.stream()
            .map(PrivilegeProjection::getModificationLevel)
            .collect(Collectors.toList());
    return this.getPrivilege(funKey, visibilityLevel, modificationLevel);
  }

  /** Build privilege found for {@link ProfileService#getPrivilegeFinal(String, List)}. */
  public PrivilegeProjection getPrivilege(
      String funKey, List<String> visibilityLevels, List<String> modificationLevels) {

    var modify =
        Objects.requireNonNullElse(
                ModificationLevel.valuesByKey(modificationLevels), ModificationLevel.SPECIFIC)
            .getKey();
    var visit =
        Objects.requireNonNullElse(
                VisibilityLevel.valuesByKey(visibilityLevels), VisibilityLevel.SPECIFIC)
            .getKey();
    return PrivilegeProjectionImpl.builder()
        .modificationLevel(modify)
        .visibilityLevel(visit)
        .key(funKey)
        .build();
  }

  /**
   * Get level of visibility of user.
   *
   * @param functionalKey refer to key of Functionality.
   * @param privilegeKey refer to key of privilege.
   * @return value of {@link String}
   */
  @Transactional(readOnly = true)
  public String findTopPrivilegeLevelOfUser(
      boolean isVisibilityLevel,
      String functionalKey,
      String privilegeKey,
      boolean checkWithContainsKey) {
    var levels =
        this.loadPrivilegeLevel(
            isVisibilityLevel, functionalKey, privilegeKey, checkWithContainsKey);

    if (levels.isEmpty()) {
      return "";
    }
    return (isVisibilityLevel)
        ? VisibilityLevel.valuesByKey(levels).getKey()
        : ModificationLevel.valuesByKey(levels).getKey();
  }

  public String findTopPrivilegeLevelOfUser(
      boolean isVisibilityLevel, String functionalKey, String privilegeKey) {
    return findTopPrivilegeLevelOfUser(isVisibilityLevel, functionalKey, privilegeKey, false);
  }

  /**
   * Load all users into organization.
   *
   * @param functionKey reference to the functional key.
   * @param privilegeKey reference to the privilege key.
   * @return list of {@link String}
   */
  @Transactional(readOnly = true)
  public List<String> loadAllUsernames(
      boolean isVisibilityLevel, Optional<String> functionKey, String privilegeKey, String level) {
    return this.findAllUsers(isVisibilityLevel, functionKey, privilegeKey, level).stream()
        .map(LoadUserPrivilegeDetails::getUsername)
        .collect(Collectors.toList());
  }

  /**
   * Load all user ids into organization.
   *
   * @param functionKey reference to the functional key.
   * @param privilegeKey reference to the privilege key.
   * @return list of {@link String}
   */
  @Transactional(readOnly = true)
  public List<Long> loadAllUserIds(
      boolean isVisibilityLevel, Optional<String> functionKey, String privilegeKey, String level) {
    return this.findAllUsers(isVisibilityLevel, functionKey, privilegeKey, level).stream()
        .map(LoadUserPrivilegeDetails::getId)
        .collect(Collectors.toList());
  }

  /**
   * Load all users into organization.
   *
   * @param functionKey reference to the functional key.
   * @param privilegeKey reference to the privilege key.
   * @return list of {@link String}
   */
  @Transactional(readOnly = true)
  public List<LoadUserPrivilegeDetails> findAllUsers(
      boolean isVisibilityLevel, Optional<String> functionKey, String privilegeKey, String level) {
    // load user id from keycloak by username
    var userId = this.keycloakService.getUserInfo(this.getUsername()).getId();

    final var organization =
        userRepository.loadOrganizationUser(userId).orElse(new LoadOrganizationUserImpl());
    if (functionKey.isPresent()) {
      level = this.findTopPrivilegeLevelOfUser(isVisibilityLevel, functionKey.get(), privilegeKey);
    }

    if (Strings.isNullOrEmpty(level)
        || level.equals(ModificationLevel.OWNER.getKey())
        || level.equals(VisibilityLevel.USER.getKey())) {

      var userEntity =
          userRepository
              .findByTechnicalRefAndIsActiveTrue(userId)
              .map(entity -> this.modelMapper.map(entity, LoadUserPrivilegeDetailsImp.class))
              .orElseThrow(() -> new UserNotFoundException(userId));

      return List.of(userEntity);
    }
    switch (VisibilityLevel.valueByKey(level)) {
      case CLIENT:
        return userRepository.getAllUsersInClient(organization.getClientId(), true);
      case DIVISION:
        return userRepository.getAllUsersInDivision(organization.getDivisionId());
      case SERVICE:
        return userRepository.getAllUsersInService(organization.getServiceId());
      default:
        return new ArrayList<>();
    }
  }

  /**
   * To find the final top level of modification level privilege by its privilegeKey and
   * functionalityKey.
   *
   * @param functionalKey refer to functional key {@link
   *     com.tessi.cxm.pfl.shared.utils.ProfileConstants} of the {@link Profile}
   * @param privilegeKey refer to privilege key {@link
   *     com.tessi.cxm.pfl.shared.utils.ProfileConstants.Privilege}
   * @return the {@link String} as level of modification level
   */
  @Transactional(readOnly = true)
  public String findModificationLevelOfUser(String functionalKey, String privilegeKey) {
    var levels = this.loadPrivilegeLevel(false, functionalKey, privilegeKey);
    if (levels.isEmpty()) {
      return "";
    }
    return ModificationLevel.valuesByKey(levels).getKey();
  }

  private List<Long> getProfileIdOfUser() {
    return this.userProfileRepository.findAllProfileByUser(
        this.keycloakService.getUserInfo(this.getUsername()).getId());
  }

  /**
   * To retrieve all users that related with profile.
   *
   * @param profileId reference to profile id
   * @return return list of {@link String}
   */
  @Transactional(readOnly = true)
  public List<String> loadAllUsersByProfileId(long profileId) {
    final var profile =
        this.profileRepository
            .findById(profileId)
            .orElseThrow(() -> new ProfileNotFoundException(profileId));
    return userProfileRepository.loadAllUserIdByProfileId(profile);
  }

  /**
   * To retrieve all level of user from profiles.
   *
   * @param isVisibilityLevel if isVisibilityLevel is true, it will be visibility level else it will
   *     be modification level.
   * @param functionalKey refer to functional key of profiles
   * @param privilegeKey refer to privilege key of profiles
   * @return list of {@link String}
   */
  private List<String> loadPrivilegeLevel(
      boolean isVisibilityLevel, String functionalKey, String privilegeKey) {
    return loadPrivilegeLevel(isVisibilityLevel, functionalKey, privilegeKey, false);
  }

  private List<String> loadPrivilegeLevel(
      boolean isVisibilityLevel,
      String functionalKey,
      String privilegeKey,
      boolean checkWithContainsKey) {
    var details =
        profileRepository.getAllPrivilegesOfProfile(this.getProfileIdOfUser(), functionalKey);
    if (checkWithContainsKey) {
      final boolean noContainsKey =
          details.stream()
              .flatMap(profileDetails -> profileDetails.getPrivileges().stream())
              .noneMatch(privilege -> privilege.getKey().equals(privilegeKey));
      if (noContainsKey) {
        throw new PrivilegeKeyNotFoundException("User does not has the request privilege");
      }
    }
    List<String> levels = new ArrayList<>();
    details.stream()
        .map(
            dt ->
                dt.getPrivileges().stream()
                    .filter(privilege -> privilege.getKey().equals(privilegeKey))
                    .map(
                        isVisibilityLevel
                            ? Privilege::getVisibilityLevel
                            : Privilege::getModificationLevel)
                    .filter(StringUtils::isNotBlank)
                    .collect(Collectors.toList()))
        .forEach(levels::addAll);
    return levels;
  }

  /**
   * Check if the current invoking user has a specific functional and privilege key.
   *
   * @param functionalKey Functional key.
   * @param privilegeKey Privilege key
   * @return Ture if user has both functionality and privilege key.
   */
  public boolean notContainsPrivilege(String functionalKey, String privilegeKey) {
    var details =
        this.profileRepository.getAllPrivilegesOfProfile(this.getProfileIdOfUser(), functionalKey);
    return details.stream()
        .noneMatch(
            profileDetails ->
                profileDetails.getPrivileges().stream()
                    .anyMatch(privilege -> privilege.getKey().equals(privilegeKey)));
  }

  private List<ProfileDetailDto> filterAllowedAccessLevel(
      ProfileDto profileDto, List<UserProfileProjection> listProfileAcces) {

    List<ProfileDetailDto> allowedProfile = new ArrayList<>();

    profileDto
        .getFunctionalities()
        .forEach(
            profileDetailDto -> {
              // compare current user access lv to target profile access lv
              listProfileAcces.stream()
                  .filter(
                      p ->
                          p.getFunctionalityKey()
                              .equalsIgnoreCase(profileDetailDto.getFunctionalityKey()))
                  .findFirst()
                  .ifPresent(
                      (userCurrentAccess) -> {
                        boolean allowed = true;

                        if (StringUtils.isNotBlank(profileDetailDto.getVisibilityLevel())) {
                          var visibilityLv =
                              VisibilityLevel.valueByKey(userCurrentAccess.getVisibilityLevel());
                          var targetVisibilityLv =
                              VisibilityLevel.valueByKey(profileDetailDto.getVisibilityLevel());

                          if (!targetVisibilityLv.equals(VisibilityLevel.SPECIFIC)) {
                            if ((5 - targetVisibilityLv.getValue())
                                > (5 - visibilityLv.getValue())) {
                              // throw new UserAccessDeniedExceptionHandler();
                              allowed = false;
                            }
                          }
                        }

                        if (StringUtils.isNotBlank(profileDetailDto.getModificationLevel())) {
                          var modificationLv =
                              ModificationLevel.valuesByKey(
                                  userCurrentAccess.getModificationLevel());
                          var targetmodificationLv =
                              ModificationLevel.valuesByKey(
                                  profileDetailDto.getModificationLevel());

                          if (!targetmodificationLv.equals(ModificationLevel.SPECIFIC)) {
                            if ((5 - targetmodificationLv.getValue())
                                > (5 - modificationLv.getValue())) {
                              // throw new UserAccessDeniedExceptionHandler();
                              allowed = false;
                            }
                          }
                        }

                        if (allowed) {
                          allowedProfile.add(profileDetailDto);
                        }
                      });
            });

    return allowedProfile;
  }

  private void validateFunctionality(ProfileDto profileDto) {
    profileDto
        .getFunctionalities()
        .forEach(
            profileDetailDto -> {
              if (!Functionality.keyExists(profileDetailDto.getFunctionalityKey(), true)) {
                throw new FunctionalityKeyNotFoundException(profileDetailDto.getFunctionalityKey());
              }
            });
  }

  private void validatePrivilege(ProfileDto profileDto) {
    var privilegeList =
        profileDto.getFunctionalities().stream()
            .map(ProfileDetailDto::getPrivileges)
            .flatMap(Collection::stream)
            .collect(Collectors.toList());

    privilegeList.forEach(
        privilege -> {
          var privilegeLevel = PrivilegeKeyValidator.getPrivilegeLevel(privilege.getKey());
          switch (PrivilegeLevelValidator.valueOf(privilegeLevel.name())) {
            case VISIBILITY:
              if (!VisibilityLevel.keyExists(privilege.getVisibilityLevel())) {
                throw new VisibilityLevelNotExistException(privilege.getVisibilityLevel());
              }
              break;
            case MODIFICATION:
              if (!ModificationLevel.keyExists(privilege.getModificationLevel())) {
                throw new ModificationLevelNotExistException(privilege.getModificationLevel());
              }
              break;
            default:
          }
        });
  }

  /**
   * To get all users related to privilege with a specific functionalKey of {@link Functionality}
   * and its privilege.
   *
   * @param isVisibilityLevel If the value is true, it is visibility-level; otherwise, it is
   *     modification-level.
   * @param functionKey refer to the key of {@link Functionality}
   * @param privilegeKey refer to key of privilege in each {@link Functionality}.
   * @return an object contain information related to above params.
   */
  public UsersRelatedToPrivilege getUserPrivilegeRelated(
      boolean isVisibilityLevel, String functionKey, String privilegeKey) {
    final var topLevelOfUser =
        this.findTopPrivilegeLevelOfUser(isVisibilityLevel, functionKey, privilegeKey);

    final var users =
        this.loadAllUsernames(isVisibilityLevel, Optional.empty(), privilegeKey, topLevelOfUser);
    final var serviceIds =
        this.loadAllServices(isVisibilityLevel, Optional.empty(), privilegeKey, topLevelOfUser);

    return UsersRelatedToPrivilege.builder()
        .privilegeType(
            isVisibilityLevel
                ? ProfileConstants.VISIBILITY_LEVEL
                : ProfileConstants.MODIFICATION_LEVEL)
        .level(topLevelOfUser)
        .relatedUsers(users)
        .relatedServices(serviceIds)
        .build();
  }

  private void checkUserModificationPrivilege(long id, String privilegeKey) {
    if (checkIfUserCanNotModify(
        id,
        getUserPrivilegeRelated(
            false,
            CXM_USER_MANAGEMENT,
            CXM_USER_MANAGEMENT.concat(
                "_".concat(privilegeKey))))) {
      throw new UserAccessDeniedExceptionHandler();
    }
  }

  @Override
  public String getConfiguredUserAdminId() {
    return this.adminUserId;
  }

  /**
   * To retrieve all admin privilege.
   *
   * @return privilege of user admin as {@link UserProfilePrivilege}
   */
  public UserProfilePrivilege getAdminProfilePrivileges() {
    if (this.adminProfilePrivileges == null) {
      try {
        this.adminProfilePrivileges =
            this.objectMapper.readValue(
                this.adminProfilePrivilegesJsonResource.getInputStream(),
                UserProfilePrivilege.class);
        this.adminProfilePrivileges.setAdmin(true);
      } catch (IOException ioException) {
        log.error("Cannot initialize admin profile privileges.", ioException);
      }
    }
    return this.adminProfilePrivileges;
  }

  /**
   * To delete the profile by client {@link Client}.
   *
   * @param clientId refer to id of {@link Client}
   */
  public void deleteByClient(Long clientId) {
    this.profileRepository.deleteByClientId(clientId);
  }

  /**
   * To collection data of profiles by client id.
   *
   * @param clientId refer to id of {@link Client}
   * @return object {@link Collection} of {@link Profile}
   */
  public List<Profile> getAllProfilesByClientId(Long clientId) {
    return this.profileRepository.findByClientId(clientId);
  }

  private Set<String> getClientFunctionalities() {
    return this.clientService.getFunctionalitiesByClientId(this.getClientId()).stream()
        .map(Functionalities::getKey)
        .collect(Collectors.toSet());
  }

  @Transactional(readOnly = true)
  public List<Long> loadAllServices(
      boolean isVisibilityLevel, Optional<String> functionKey, String privilegeKey, String level) {
    // Load user id from keycloak by username.
    var userId = this.keycloakService.getUserInfo(this.getUsername()).getId();

    final var organization =
        userRepository.loadOrganizationUser(userId).orElse(new LoadOrganizationUserImpl());
    if (functionKey.isPresent()) {
      level = this.findTopPrivilegeLevelOfUser(isVisibilityLevel, functionKey.get(), privilegeKey);
    }

    List<Long> services = new ArrayList<>();

    if (Strings.isNullOrEmpty(level)
        || level.equals(ModificationLevel.OWNER.getKey())
        || level.equals(VisibilityLevel.USER.getKey())) {
      if (organization.getServiceId() != 0) {
        services.add(organization.getServiceId());
      }
      return services;
    }

    switch (VisibilityLevel.valueByKey(level)) {
      case CLIENT:
        services = getServiceInClient(organization.getClientId());
        break;
      case DIVISION:
        services = getServiceInDivision(organization.getDivisionId());
        break;
      case SERVICE:
        services.add(organization.getServiceId());
        break;
      default:
        // do nothing
    }
    return services;
  }

  private List<Long> getServiceInClient(long clientId) {
    final var specification = DepartmentSpecification.clientEqual(clientId);
    return this.departmentRepository.findAll(specification).stream()
        .map(Department::getId)
        .collect(Collectors.toList());
  }

  private List<Long> getServiceInDivision(long departmentId) {
    final var specification = DepartmentSpecification.divisionEqual(departmentId);
    return this.departmentRepository.findAll(specification).stream()
        .map(Department::getId)
        .collect(Collectors.toList());
  }

  public UserPrivilegeDetails getUserPrivilegeDetails(
      boolean isVisibilityLevel, String functionKey, String privilegeKey, boolean getRelatedUsers) {
    final var topLevelOfUser =
        this.findTopPrivilegeLevelOfUser(isVisibilityLevel, functionKey, privilegeKey, true);
    final UserPrivilegeDetails.UserPrivilegeDetailsBuilder builder = UserPrivilegeDetails.builder();
    List<Long> relatedOwners = new ArrayList<>();
    if (getRelatedUsers && StringUtils.isNotBlank(topLevelOfUser)) {
      final var userOwnerIds =
          this.loadAllUsersPrivilegedDetailsByLevel(topLevelOfUser).stream()
              .map(UserDetailDTO::getId)
              .collect(Collectors.toList());
      relatedOwners.addAll(userOwnerIds);
    }
    this.userRepository
        .findByTechnicalRefAndIsActiveTrue(AuthenticationUtils.getPrincipalIdentifier())
        .ifPresent(
            userEntity -> {
              if (!relatedOwners.contains(userEntity.getId())) {
                relatedOwners.add(userEntity.getId());
              }
            });
    return builder
        .privilegeType(
            isVisibilityLevel
                ? ProfileConstants.VISIBILITY_LEVEL
                : ProfileConstants.MODIFICATION_LEVEL)
        .level(topLevelOfUser)
        .relatedOwners(relatedOwners)
        .nonLevelPrivilege(StringUtils.isBlank(topLevelOfUser))
        .build();
  }

  /**
   * Load all users into organization.
   *
   * @return list of {@link String}
   */
  @Transactional(readOnly = true)
  public List<UserDetailDTO> loadAllUsersPrivilegedDetailsByLevel(String level) {
    // load user id from keycloak by username
    var userId = AuthenticationUtils.getPrincipalIdentifier();

    final var organization =
        userRepository.loadOrganizationUser(userId).orElse(new LoadOrganizationUserImpl());
    if (level.equals(ModificationLevel.OWNER.getKey())
        || level.equals(VisibilityLevel.USER.getKey())) {
      final UserEntity userEntity =
          this.userRepository
              .findByTechnicalRefAndIsActiveTrue(userId)
              .orElseThrow(UserAccessDeniedExceptionHandler::new);
      return List.of(
          UserDetailDTO.builder()
              .id(userEntity.getId())
              .username(userEntity.getUsername())
              .build());
    }
    switch (VisibilityLevel.valueByKey(level)) {
      case CLIENT:
        return userRepository.getAllUsersIdInClient(organization.getClientId()).stream()
            .map(this::mappingUserDetailDTO)
            .collect(Collectors.toList());
      case DIVISION:
        return userRepository.getAllUsersIdInDivision(organization.getDivisionId()).stream()
            .map(this::mappingUserDetailDTO)
            .collect(Collectors.toList());
      case SERVICE:
        return userRepository.getAllUsersIdInService(organization.getServiceId()).stream()
            .map(this::mappingUserDetailDTO)
            .collect(Collectors.toList());
      default:
        return new ArrayList<>();
    }
  }

  /**
   * To retrieve collection the users that mapped with {@link SharedUserEntityDTO}.
   *
   * @param functionKey refer to functionality key.
   * @param privilegeKey refer to privilege key of functionality {@code Create, Update, Delete,
   *     List}
   * @param isVisibilityLevel true, it's visibility else modification.
   * @return object of {@link SharedUserEntityDTO}
   */
  public List<SharedUserEntityDTO> loadAllUsersEntities(
      String functionKey, String privilegeKey, boolean isVisibilityLevel) {
    final var topLevel =
        this.findTopPrivilegeLevelOfUser(isVisibilityLevel, functionKey, privilegeKey, true);

    var users = loadAllUsersPrivilegedDetailsByLevel(topLevel);

    return users.stream()
        .map(
            user -> {
              UserRepresentation userKeycloak =
                  this.keycloakService.getUserResource().search(user.getUsername(), true).stream()
                      .findFirst()
                      .orElse(null);
              if (Objects.isNull(userKeycloak)) {
                final String logMessage =
                    "User is not presented in Keycloak due to manual deletion";
                if (log.isDebugEnabled()) {
                  log.debug(logMessage + " {}.", user);
                } else {
                  log.warn(logMessage + ". Enable debug mode to see more details.");
                }
              }
              if (userKeycloak != null) {
                return SharedUserEntityDTO.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .email(userKeycloak.getEmail())
                    .firstName(userKeycloak.getFirstName())
                    .lastName(userKeycloak.getLastName())
                    .technicalRef(userKeycloak.getId())
                    .build();
              }
              return null;
            })
        .collect(Collectors.toList())
        .stream()
        .filter(Objects::nonNull)
        .distinct()
        .collect(Collectors.toList());
  }

  /**
   * To mapping user details DTO.
   *
   * @param user refer to object of {@link LoadUserPrivilegeDetails}
   * @return return mapped object of {@link UserDetailDTO}
   */
  private UserDetailDTO mappingUserDetailDTO(LoadUserPrivilegeDetails user) {
    return this.modelMapper.map(user, UserDetailDTO.class);
  }

  public List<ProfileFilterCriteria> getAllProfilesCriteria(Long serviceId) {

    final boolean isAdmin = isAdmin();
    UsersRelatedToPrivilege userPrivilegeRelated = null;

    if (!isAdmin)
      checkUsersRelatedToPrivilege();

    Specification<Profile> specification = Specification.where(null);

    if (isAdmin) {
      var client =
          this.clientRepository
              .findClientByServiceId(serviceId)
              .orElseThrow(
                  () -> new NotFoundException("Service's ID does not found with ID: " + serviceId));
      specification = specification.and(ProfileSpecification.equalClientId(client.getId()));

    } else if (Objects.nonNull(userPrivilegeRelated)) {
      specification =
          getProfileCriteriaSpecification(specification, userPrivilegeRelated.getRelatedUsers());
    }

    return mapProfileCriteria(this.profileRepository.findAll(specification));
  }

  private List<ProfileFilterCriteria> mapProfileCriteria(List<Profile> profiles) {
    return profiles.stream()
        .parallel()
        .map(e -> new ProfileFilterCriteria(e.getId(), e.getName()))
        .collect(Collectors.toList());
  }

  private Specification<Profile> getProfileCriteriaSpecification(
      Specification<Profile> specification, List<String> users) {
    specification = specification.and(Specification.where(ProfileSpecification.containsIn(users)));
    return specification;
  }


  private Pageable buildPageRequest(int page, int pageSize, String sortDirection, String sortByField) {
    if(page > 0)
      page  = page - 1;

    Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortByField).ascending()
            : Sort.by(sortByField).descending();
    return PageRequest.of(page, pageSize, sort);
  }

  private void checkUsersRelatedToPrivilege() {
    UsersRelatedToPrivilege userPrivilegeRelated =
            this.getUserPrivilegeRelated(
                    true,
                    CXM_USER_MANAGEMENT,
                    CXM_USER_MANAGEMENT.concat(
                            "_".concat(UserManagementConstants.Profile.LIST)));
    if (StringUtils.isBlank(userPrivilegeRelated.getLevel()))
      throw new UserAccessDeniedExceptionHandler();
  }


  /**
   * Load all users into organization.
   *
   * @return list of {@link String}
   */
  @Transactional(readOnly = true)
  public List<UserDetailDTO> loadAllUsersDetailsPrivilegedDetailsByLevel(String level) {
    // load user id from keycloak by username
    var userId = AuthenticationUtils.getPrincipalIdentifier();

    final var organization =
            userRepository.loadOrganizationUser(userId).orElse(new LoadOrganizationUserImpl());
    if (level.equals(ModificationLevel.OWNER.getKey())
            || level.equals(VisibilityLevel.USER.getKey())) {
      final UserEntity userEntity =
              this.userRepository
                      .findByTechnicalRefAndIsActiveTrue(userId)
                      .orElseThrow(UserAccessDeniedExceptionHandler::new);
      return List.of(
              UserDetailDTO.builder()
                      .id(userEntity.getId())
                      .username(userEntity.getUsername())
                      .serviceName(userEntity.getDepartment().getName())
                      .serviceId(userEntity.getDepartment().getId())
                      .divisionName(userEntity.getDepartment().getDivision().getName())
                      .divisionId(userEntity.getDepartment().getDivision().getId())
                      .clientName(userEntity.getDepartment().getDivision().getClient().getName())
                      .clientId(userEntity.getDepartment().getDivision().getClient().getId())
                      .build());
    }
    switch (VisibilityLevel.valueByKey(level)) {
      case CLIENT:
        return userRepository.loadAllUsersDetailsIdInClient(organization.getClientId()).stream()
            .map(loadUserDetails -> this.modelMapper.map(loadUserDetails, UserDetailDTO.class))
            .collect(Collectors.toList());
      case DIVISION:
        return userRepository.loadAllUsersDetailsIdInDivision(organization.getDivisionId()).stream()
            .map(loadUserDetails -> this.modelMapper.map(loadUserDetails, UserDetailDTO.class))
            .collect(Collectors.toList());
      case SERVICE:
        return userRepository.loadAllUsersDetailsIdInService(organization.getServiceId()).stream()
            .map(loadUserDetails -> this.modelMapper.map(loadUserDetails, UserDetailDTO.class))
            .collect(Collectors.toList());
      default:
        return new ArrayList<>();
    }
  }


  public UserPrivilegeDetailsOwner getUserPrivilegeDetailsOwner(
      boolean isVisibilityLevel, String functionKey, String privilegeKey, boolean getRelatedUsers) {
    final var topLevelOfUser =
        this.findTopPrivilegeLevelOfUser(isVisibilityLevel, functionKey, privilegeKey, true);
    List<UserPrivilegeDetailsOwner.UserDetailsOwner> relatedOwners = new ArrayList<>();
    if (getRelatedUsers && StringUtils.isNotBlank(topLevelOfUser)) {
      this.loadAllUsersDetailsPrivilegedDetailsByLevel(topLevelOfUser)
          .forEach(
              userDetailDTO ->
                  relatedOwners.add(
                      this.modelMapper.map(
                          userDetailDTO, UserPrivilegeDetailsOwner.UserDetailsOwner.class)));
    }
    this.userRepository
        .findByTechnicalRefAndIsActiveTrue(AuthenticationUtils.getPrincipalIdentifier())
        .ifPresent(
            userEntity -> {
              boolean nonUserDetail =
                  relatedOwners.stream()
                      .noneMatch(
                          userDetailsOwner ->
                              Objects.equals(userDetailsOwner.getId(), userEntity.getId()));
              if (nonUserDetail) {
                UserPrivilegeDetailsOwner.UserDetailsOwner userDetailsOwner =
                    new UserPrivilegeDetailsOwner.UserDetailsOwner();
                userDetailsOwner.setId(userEntity.getId());
                userDetailsOwner.setUsername(userEntity.getUsername());
                userDetailsOwner.setServiceName(userEntity.getDepartment().getName());
                userDetailsOwner.setDivisionName(
                    userEntity.getDepartment().getDivision().getName());
                userDetailsOwner.setDivisionId(userEntity.getDepartment().getDivision().getId());
                userDetailsOwner.setClientName(
                    userEntity.getDepartment().getDivision().getClient().getName());
                userDetailsOwner.setClientId(
                    userEntity.getDepartment().getDivision().getClient().getId());
                relatedOwners.add(userDetailsOwner);
              }
            });
    UserPrivilegeDetailsOwner userPrivilegeDetailsOwner = new UserPrivilegeDetailsOwner();
    userPrivilegeDetailsOwner.setPrivilegeType(
        isVisibilityLevel
            ? ProfileConstants.VISIBILITY_LEVEL
            : ProfileConstants.MODIFICATION_LEVEL);
    userPrivilegeDetailsOwner.setLevel(topLevelOfUser);
    userPrivilegeDetailsOwner.setUserDetailsOwners(relatedOwners);
    userPrivilegeDetailsOwner.setNonLevelPrivilege(StringUtils.isBlank(topLevelOfUser));
    return userPrivilegeDetailsOwner;
  }
}
