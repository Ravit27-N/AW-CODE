package com.tessi.cxm.pfl.ms5.service;

import com.tessi.cxm.pfl.ms5.config.HubAccountEncryptionProperties;
import com.tessi.cxm.pfl.ms5.entity.Client;
import com.tessi.cxm.pfl.ms5.entity.UserHub;
import com.tessi.cxm.pfl.ms5.exception.ClientNotFoundException;
import com.tessi.cxm.pfl.ms5.exception.UserAPIFailureException;
import com.tessi.cxm.pfl.ms5.repository.ClientRepository;
import com.tessi.cxm.pfl.ms5.repository.UserHubRepository;
import com.tessi.cxm.pfl.ms5.repository.UserRepository;
import com.tessi.cxm.pfl.shared.auth.AuthenticationUtils;
import com.tessi.cxm.pfl.shared.core.Context;
import com.tessi.cxm.pfl.shared.exception.UserAccessDeniedExceptionHandler;
import com.tessi.cxm.pfl.shared.model.SummarizeAccountEncryption;
import com.tessi.cxm.pfl.shared.model.hubdigitalflow.AuthRequest;
import com.tessi.cxm.pfl.shared.model.hubdigitalflow.AuthResponse;
import com.tessi.cxm.pfl.shared.model.hubdigitalflow.ClientAccountResponseDto;
import com.tessi.cxm.pfl.shared.model.hubdigitalflow.ServiceProviderResponse;
import com.tessi.cxm.pfl.shared.model.hubdigitalflow.UserAPIRequest;
import com.tessi.cxm.pfl.shared.model.hubdigitalflow.UserHubAccount;
import com.tessi.cxm.pfl.shared.model.hubdigitalflow.UserHubRequestDto;
import com.tessi.cxm.pfl.shared.service.encryption.AccountEncryption;
import com.tessi.cxm.pfl.shared.service.restclient.HubDigitalFlow;
import com.tessi.cxm.pfl.shared.utils.AESHelper;
import com.tessi.cxm.pfl.shared.utils.BearerAuthentication;
import com.tessi.cxm.pfl.shared.utils.HubAccountEncryptionConstant;
import feign.FeignException.FeignClientException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class UserHubService implements AdminService {

  private final UserHubRepository userHubRepository;
  private final ModelMapper modelMapper;
  private final ClientRepository clientRepository;
  private final HubDigitalFlow hubDigitalFlow;

  private final UserRepository userRepository;
  private final AccountEncryption accountEncryption;

  @Value("${cxm.hub-account.username}")
  private String defaultUsername;

  @Value("${cxm.hub-account.password}")
  private String defaultPassword;

  @Value("${cxm.user.admin-id}")
  private String adminUserId;

  private final AESHelper aesHelper;
  private final UserService userService;
  private final HubAccountEncryptionProperties accountEncryptionProperties;

  @Override
  public String getConfiguredUserAdminId() {
    return this.adminUserId;
  }

  @Override
  public UserRepository getUserRepository() {
    return this.userRepository;
  }

  /**
   * Method used to get user hub account by using technical reference.
   *
   * @return - object of {@link UserHubAccount}
   */
  public UserHubAccount getUserHubByTechnicalRef() {
    AtomicBoolean isDefaultUser = new AtomicBoolean(false);
    UserHub userHub =
        this.userHubRepository
            .findByTechnicalRef(AuthenticationUtils.getPrincipalIdentifier())
            .orElseGet(
                () -> {
                  isDefaultUser.getAndSet(true);
                  return this.getDefaultUser();
                });

    var userHubAccount = this.modelMapper.map(userHub, UserHubAccount.class);
    if (!isDefaultUser.get()) {
      String password = this.aesHelper.decrypt(userHub.getPassword());
      userHubAccount.setPassword(password);
    }

    return userHubAccount;
  }

  /**
   * To retrieve information of the <b>User Hub</b> by <b>User Entity Id</b>
   *
   * @return object of {@link UserHubAccount}
   */
  public UserHubAccount getUserHub(String username) {
    AtomicBoolean isDefaultUser = new AtomicBoolean(false);
    var userHub =
        this.userHubRepository.findByUserEntityUsername(username)
            .orElseGet(() -> {
              isDefaultUser.set(true);
              return this.getDefaultUser();
            });

    var userHubAccount = this.modelMapper.map(userHub, UserHubAccount.class);
    if (!isDefaultUser.get()) {
      String password = this.aesHelper.decrypt(userHub.getPassword());
      userHubAccount.setPassword(password);
    }

    return userHubAccount;
  }

  public UserHubRequestDto registerUserHub(UserHubRequestDto request) {
    if (!this.isAdmin()) {
      throw new UserAccessDeniedExceptionHandler();
    }

    // Get client and validate client exist in db.
    final Client clientEntity = this.getClient(request.getClient());
    final String refHubToken = this.getHubToken();

    // Get existing user hub or create new user hub.
    UserHub userHub =
        this.userHubRepository
            .findByClientId(clientEntity.getId())
            .orElseGet(
                () -> {
                  final UserHub newUserHub = this.modelMapper.map(request, UserHub.class);
                  newUserHub.setClient(clientEntity);
                  return newUserHub;
                });

    // A modified username & password of user hub.
    userHub.setUsername(request.getUsername());
    userHub.setPassword(aesHelper.encrypt(request.getPassword()));
    userHub.setEncrypted(true);

    // Register customer to user API in hub-digital flow.
    this.registerUserAPI(request, refHubToken);

    // Save or modified user hub.
    var response = this.userHubRepository.save(userHub);

    // Mapping response dto.
    var responseDto = this.modelMapper.map(response, UserHubRequestDto.class);
    responseDto.setClient(clientEntity.getName());
    responseDto.setPassword(request.getPassword());
    return responseDto;
  }

  public ClientAccountResponseDto getCustomerAccount(String client) {
    if (!this.isAdmin()) {
      throw new UserAccessDeniedExceptionHandler();
    }

    if (client.isBlank()) {
      client = this.userService.getUserDetail().getClientName();
    }
    String hubToken = this.getHubToken();
    return this.hubDigitalFlow.getUserAPI(client, hubToken);
  }

  private Client getClient(String clientName) {
    return this.clientRepository
        .findByName(clientName)
        .orElseThrow(() -> new ClientNotFoundException("Client not exist"));
  }

  private void registerUserAPI(UserHubRequestDto requestDto, String refHubToken) {
    if (!this.isAdmin()) {
      throw new UserAccessDeniedExceptionHandler();
    }

    try {
      // Build user API request object.
      UserAPIRequest userAPIRequest =
          UserAPIRequest.builder()
              .login(requestDto.getUsername())
              .password(requestDto.getPassword())
              .customer(requestDto.getClient())
              .build();

      // Request to hub for create user API.
      this.hubDigitalFlow.registerUserAPI(userAPIRequest, refHubToken);

    } catch (FeignClientException feignClientException) {
      log.error(feignClientException.getMessage(), feignClientException);
      throw new UserAPIFailureException("Fail to create user API in hub-digitalflow");
    }
  }

  /**
   * Get authorization token of user hub account.
   *
   * @return - token {@link String}.
   */
  public String getHubToken() {
    AtomicBoolean isDefaultUser = new AtomicBoolean(false);
    final UserHub userHub =
        this.userHubRepository
            .findByTechnicalRef(AuthenticationUtils.getPrincipalIdentifier())
            .orElseGet(
                () -> {
                  isDefaultUser.getAndSet(true);
                  return this.getDefaultUser();
                });

    String password = userHub.getPassword();
    if (!isDefaultUser.get()) {
      password = this.aesHelper.decrypt(userHub.getPassword());
    }

    AuthResponse hubAuthResponse =
        this.hubDigitalFlow.getAuthToken(new AuthRequest(userHub.getUsername(), password));

    return BearerAuthentication.PREFIX_TOKEN.concat(hubAuthResponse.getToken());
  }

  /**
   * Method used to get all service provider per a channel.
   *
   * @param channel - value of {@link String}.
   * @return - object of {@link ServiceProviderResponse}.
   */
  public ServiceProviderResponse getServiceProvider(List<String> channel) {
    String hubToken = getHubToken();
    return this.hubDigitalFlow.getServiceProvider(channel, hubToken);
  }

  public UserHub getDefaultUser() {
    //    String defaultPasswordEncrypted = this.aesHelper.encrypt(this.defaultPassword);

    return UserHub.builder().username(this.defaultUsername).password(this.defaultPassword).build();
  }

  public SummarizeAccountEncryption encryptPassword() {
    if (!this.isAdmin()) {
      throw new UserAccessDeniedExceptionHandler();
    }
    log.info("--- Start encrypting the password using AES-256 ---");
    Context context = new Context();
    context.put(
        HubAccountEncryptionConstant.PAGE_SIZE, this.accountEncryptionProperties.getPageSize());
    SummarizeAccountEncryption summarizeAccountEncryption = accountEncryption.encrypt(context);
    log.info("--- End encrypting the password using AES-256 ---");
    return summarizeAccountEncryption;
  }
}
