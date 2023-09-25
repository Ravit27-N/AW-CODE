package com.tessi.cxm.pfl.ms11.service;

import static com.tessi.cxm.pfl.ms11.util.ConstantProperties.CUSTOMER_NAME;
import static com.tessi.cxm.pfl.ms11.util.ConstantProperties.FUNCTIONALITIES;
import static com.tessi.cxm.pfl.ms11.util.ConstantProperties.PORTAL_SETTING;
import static com.tessi.cxm.pfl.ms11.util.ConstantProperties.PORTAL_SETTING_CONFIG_STATUS_DTO;
import static com.tessi.cxm.pfl.ms11.util.ConstantProperties.POSTAL_CONFIGURATION_DTO;
import static com.tessi.cxm.pfl.ms11.util.ConstantProperties.SETTING;
import static com.tessi.cxm.pfl.ms11.util.ConstantProperties.SETTING_INSTRUCTIONS;
import static com.tessi.cxm.pfl.ms11.util.ConstantProperties.SETTING_LIST;
import static com.tessi.cxm.pfl.ms11.util.ConstantProperties.USER_INFO_RESPONSE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import com.cxm.tessi.pfl.shared.flowtreatment.model.response.DepositValidation;
import com.cxm.tessi.pfl.shared.flowtreatment.model.response.PreProcessingSettingResponseDto;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.tessi.cxm.pfl.ms11.config.Go2pdfFileConfig;
import com.tessi.cxm.pfl.ms11.entity.PortalSetting;
import com.tessi.cxm.pfl.ms11.entity.Setting;
import com.tessi.cxm.pfl.ms11.entity.SettingInstruction;
import com.tessi.cxm.pfl.ms11.entity.projection.BatchSettingProjection;
import com.tessi.cxm.pfl.ms11.entity.projection.DepositValidationProjection;
import com.tessi.cxm.pfl.ms11.exception.CustomerNotFoundException;
import com.tessi.cxm.pfl.ms11.repository.CriteriaDistributionRepository;
import com.tessi.cxm.pfl.ms11.repository.PortalSettingRepository;
import com.tessi.cxm.pfl.ms11.repository.SettingInstructionRepository;
import com.tessi.cxm.pfl.ms11.repository.SettingRepository;
import com.tessi.cxm.pfl.shared.auth.AuthenticationUtils;
import com.tessi.cxm.pfl.shared.exception.UserAccessDeniedExceptionHandler;
import com.tessi.cxm.pfl.shared.model.PortalSettingConfigStatusDto;
import com.tessi.cxm.pfl.shared.model.PostalConfigurationDto;
import com.tessi.cxm.pfl.shared.model.ProfileClientSettingRequest;
import com.tessi.cxm.pfl.shared.model.UserInfoResponse;
import com.tessi.cxm.pfl.shared.service.restclient.ProfileFeignClient;
import com.tessi.cxm.pfl.shared.utils.CustomerDepositModeDto;
import com.tessi.cxm.pfl.shared.utils.DepositModeConstant;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(OrderAnnotation.class)
@Slf4j
class SettingServiceTest {
  private final Go2pdfFileConfig localFileConfig = new Go2pdfFileConfig();
  @Mock SettingRepository settingRepository;
  @Mock SettingInstructionRepository settingInstructionRepository;
  @Mock PortalSettingRepository portalSettingRepository;
  @Mock ProfileFeignClient profileFeignClient;
  @InjectMocks MockSettingService settingService;
  @Mock ConfigurationService configurationService;
  @Mock CriteriaDistributionRepository criteriaDistributionRepository;

  @BeforeEach
  void setUp() {
    ModelMapper modelMapper = new ModelMapper();
    modelMapper.addMappings(
        new PropertyMap<DepositValidationProjection, DepositValidation>() {
          @Override
          protected void configure() {
            map().getContent().setFlowType(source.getFlowType());
            map().getContent().setIdCreator(source.getIdCreator());
            map().setScanActivation(source.isScanActivation());
          }
        });
    this.localFileConfig.setPath("/apps/cxm/go2pdf");
    this.settingService =
        new MockSettingService(
            settingRepository,
            modelMapper,
            settingInstructionRepository,
            profileFeignClient,
            portalSettingRepository);
    this.settingService.setConfigurationService(this.configurationService);
    this.settingService.setGo2pdfFileConfig(this.localFileConfig);
    this.settingService.setUsername("User1");
  }

  @Test
  @Order(1)
  void shouldReturnData_WhenGetDepositValidation() {
    try (MockedStatic<AuthenticationUtils> ignored = mockStatic(AuthenticationUtils.class)) {
      var mockResponse =
          new DepositValidationProjection() {
            @Override
            public String getFlowType() {
              return "ENI/Batch/C1/zip";
            }

            @Override
            public Long getIdCreator() {
              return 1L;
            }

            @Override
            public boolean isScanActivation() {
              return true;
            }

            /**
             * @return
             */
            @Override
            public boolean isConfigurationActivation() {
              return false;
            }
          };

      when(this.settingRepository.findDepositValidation(
              anyString(), anyString(), anyString(), anyString()))
          .thenReturn(mockResponse);

      var result = this.settingService.getDepositValidation("ENI", "Batch", "C1", "zip");

      assertNotEquals(JsonNodeFactory.instance.objectNode(), result.getData());
      loggingActualResponse(result.getData());
    }
  }

  @Test
  @Order(2)
  void shouldReturnEmptyObject_WhenGetDepositValidation() {
    try (MockedStatic<AuthenticationUtils> ignored = mockStatic(AuthenticationUtils.class)) {
      when(this.settingRepository.findDepositValidation(
              anyString(), anyString(), anyString(), anyString()))
          .thenReturn(null);

      var result = this.settingService.getDepositValidation("ENI", "Batch", "C1", "zip");

      assertEquals(JsonNodeFactory.instance.objectNode(), result.getData());
      loggingActualResponse(result.getData());
    }
  }

  @Test
  @Order(3)
  void shouldReturnData_WhenGetChannelAndSubChannel() {
    try (MockedStatic<AuthenticationUtils> ignored = mockStatic(AuthenticationUtils.class)) {
      var mockResponse =
          new BatchSettingProjection() {
            @Override
            public String getChannel() {
              return "Digital";
            }

            @Override
            public String getSubChannel() {
              return "Email";
            }

            @Override
            public String getModelType() {
              return "Email";
            }
          };

      when(this.settingInstructionRepository.extractBatchSetting(
              anyString(), anyLong(), anyString()))
          .thenReturn(Optional.of(mockResponse));

      var result = this.settingService.extractSetting("ENI/Batch/C1/zip", 1, "FLOW_1");

      assertNotEquals(
          JsonNodeFactory.instance.objectNode(), result.getData(), "object data must not empty.");
      loggingActualResponse(result.getData());
    }
  }

  @Test
  @Order(4)
  void shouldReturnEmptyObject_WhenGetChannelAndSubChannel() {
    try (MockedStatic<AuthenticationUtils> ignored = mockStatic(AuthenticationUtils.class)) {
      when(this.settingInstructionRepository.extractBatchSetting(
              anyString(), anyLong(), anyString()))
          .thenReturn(Optional.empty());

      var result = this.settingService.extractSetting("ENI/Batch/C1/zip", 1, "FLOW_1");

      assertEquals(JsonNodeFactory.instance.objectNode(), result.getData());
      loggingActualResponse(result.getData());
    }
  }

  @Test
  @Order(5)
  void shouldReturnData_whenGetDocumentInstructions() {
    try (MockedStatic<AuthenticationUtils> ignored = mockStatic(AuthenticationUtils.class)) {
      var mockResponse =
          SettingInstruction.builder()
              .emailObject("Column 7")
              .address("18 to 21")
              .pjs("17 and 11 to 14")
              .idBreakingPage("/XXX/")
              .data("26 to 27")
              .channel("Digital")
              .subChannel("Email")
              .build();

      when(this.settingInstructionRepository.getInstructionDetails(
              anyString(), anyString(), anyLong()))
          .thenReturn(mockResponse);

      var result = this.settingService.getInstructionDetails("ENI/Batch/C1/zip", "MODEL_NAME", 1);

      var actualResponse = (PreProcessingSettingResponseDto) result.getData();
      assertEquals("18 to 21", actualResponse.getAddress());
      assertEquals("17 and 11 to 14", actualResponse.getPjs());
      assertNotEquals(JsonNodeFactory.instance.objectNode(), result.getData());
      loggingActualResponse(actualResponse);
    }
  }

  @Test
  @Order(6)
  void shouldReturnEmptyObject_whenGetDocumentInstructions() {
    try (MockedStatic<AuthenticationUtils> ignored = mockStatic(AuthenticationUtils.class)) {
      when(this.settingInstructionRepository.getInstructionDetails(
              anyString(), anyString(), anyLong()))
          .thenReturn(null);

      var result = this.settingService.getInstructionDetails("ENI/Batch/C1/zip", "MODEL_NAME", 1);

      assertEquals(JsonNodeFactory.instance.objectNode(), result.getData());
      loggingActualResponse(result.getData());
    }
  }

  void loggingActualResponse(Object actualResult) {
    log.info("actual response: {}.", actualResult);
    assertNotNull(actualResult);
  }

  @Test
  @Order(7)
  void testCreatePortalPath() {
    String configPath = String.format("%s%s/config.ini", "//apps//cxm\\config//go2pdf/", "tessi");
    String normalizePath = Paths.get(configPath).normalize().toString();
    final String finalPath = FilenameUtils.separatorsToUnix(normalizePath);
    loggingActualResponse(finalPath);
    assertNotNull(finalPath);
  }

  @Test
  @Order(8)
  void createFlowTypePortalPDf() {

    ProfileClientSettingRequest profileClientSettingRequest =
        new ProfileClientSettingRequest(CUSTOMER_NAME, FUNCTIONALITIES);
    try (MockedStatic<AuthenticationUtils> ignored = mockStatic(AuthenticationUtils.class)) {
      when(profileFeignClient.checkUserIsAdmin(anyString())).thenReturn(USER_INFO_RESPONSE);
      when(settingRepository.findByCustomerInIgnoreCaseAndFlowTypeInIgnoreCase(
              anyList(), anyList()))
          .thenReturn(List.of(SETTING_LIST.get(2)));
      this.settingService.createClientSetting(
          profileClientSettingRequest, UUID.randomUUID().toString());
    }
    loggingActualResponse(List.of(SETTING_LIST.get(2), PORTAL_SETTING));
  }

  @Test
  @Order(9)
  void updateFlowTypePortalPdfScanActiveFalse() {
    List<ProfileClientSettingRequest.Functionality> functionalities = FUNCTIONALITIES;
    functionalities.forEach(functionality -> functionality.setActivate(false));
    Setting setting = SETTING_LIST.get(2);
    PortalSetting portalSetting = PORTAL_SETTING;
    setting.setScanActivation(false);
    portalSetting.setActive(false);
    ProfileClientSettingRequest profileClientSettingRequest =
        new ProfileClientSettingRequest("client_digital", functionalities);
    try (MockedStatic<AuthenticationUtils> ignored = mockStatic(AuthenticationUtils.class)) {
      when(profileFeignClient.checkUserIsAdmin(anyString())).thenReturn(USER_INFO_RESPONSE);
      when(settingRepository.findByCustomerInIgnoreCaseAndFlowTypeInIgnoreCase(
              anyList(), anyList()))
          .thenReturn(List.of(setting));
      this.settingService.createClientSetting(
          profileClientSettingRequest, UUID.randomUUID().toString());
    }
    loggingActualResponse(List.of(setting, portalSetting));
  }

  @Test
  @Order(10)
  void createFlowTypePortalCampaignSmsAndEmail() {
    List<Setting> settings = SETTING_LIST;
    settings.remove(2);
    ProfileClientSettingRequest profileClientSettingRequest =
        new ProfileClientSettingRequest(CUSTOMER_NAME, FUNCTIONALITIES);
    try (MockedStatic<AuthenticationUtils> ignored = mockStatic(AuthenticationUtils.class)) {
      when(profileFeignClient.checkUserIsAdmin(anyString())).thenReturn(USER_INFO_RESPONSE);
      when(settingRepository.findByCustomerInIgnoreCaseAndFlowTypeInIgnoreCase(
              anyList(), anyList()))
          .thenReturn(settings);
      this.settingService.createClientSetting(
          profileClientSettingRequest, UUID.randomUUID().toString());
    }
    loggingActualResponse(List.of(settings, SETTING_INSTRUCTIONS));
  }

  @Test
  @Order(11)
  void updateFlowTypePortalCampaignSmsAndEmailScanActiveFalse() {
    List<ProfileClientSettingRequest.Functionality> functionalities = FUNCTIONALITIES;
    functionalities.forEach(functionality -> functionality.setActivate(false));
    List<Setting> settings = SETTING_LIST;
    settings.forEach(setting -> setting.setScanActivation(false));
    ProfileClientSettingRequest profileClientSettingRequest =
        new ProfileClientSettingRequest(CUSTOMER_NAME, functionalities);
    try (MockedStatic<AuthenticationUtils> ignored = mockStatic(AuthenticationUtils.class)) {
      when(profileFeignClient.checkUserIsAdmin(anyString())).thenReturn(USER_INFO_RESPONSE);
      when(settingRepository.findByCustomerInIgnoreCaseAndFlowTypeInIgnoreCase(
              anyList(), anyList()))
          .thenReturn(settings);
      this.settingService.createClientSetting(
          profileClientSettingRequest, UUID.randomUUID().toString());
    }
    loggingActualResponse(List.of(settings, SETTING_INSTRUCTIONS));
  }

  @Test
  @Order(12)
  void testNoPrivilegeToModifyConfiguration() {
    var payload =
        PortalSettingConfigStatusDto.builder().clientName("Client 1").isActive(true).build();
    var token = "Bearer token";
    var userInfoResponse =
        UserInfoResponse.builder().superAdmin(false).platformAdmin(false).build();

    when(this.profileFeignClient.checkUserIsAdmin(anyString())).thenReturn(userInfoResponse);

    assertThrows(
        UserAccessDeniedExceptionHandler.class,
        () -> this.settingService.modifiedPortalSettingConfig(payload, token));
  }

  @Test
  @Order(13)
  void getDepositTypeOfSetting() {
    when(settingRepository.findAllSetting(anyString(), anyList(), anyList()))
        .thenReturn(Optional.of(SETTING_LIST));
    List<CustomerDepositModeDto> depositModes = this.settingService.getDepositModes(CUSTOMER_NAME);
    loggingActualResponse(depositModes);
  }

  @Test
  @Order(13)
  void testSuccessNewConfiguration() {
    var token = "Bearer token";
    var userInfoResponse =
        UserInfoResponse.builder().superAdmin(true).platformAdmin(true).build();

    when(this.settingRepository.findAllSetting(anyString(), anyList(), anyList()))
        .thenReturn(Optional.of(SETTING_LIST));
    when(this.profileFeignClient.checkUserIsAdmin(anyString())).thenReturn(userInfoResponse);
    when(this.portalSettingRepository.save(any())).thenReturn(PORTAL_SETTING);

    PortalSettingConfigStatusDto response =
        this.settingService.modifiedPortalSettingConfig(PORTAL_SETTING_CONFIG_STATUS_DTO, token);

    assertNotNull(response);
  }

  @Test
  @Order(14)
  void testSuccessModifyConfiguration() {
    var token = "Bearer token";
    var userInfoResponse =
        UserInfoResponse.builder().superAdmin(true).platformAdmin(true).build();

    when(this.settingRepository.findAllSetting(anyString(), anyList(), anyList()))
        .thenReturn(Optional.of(SETTING_LIST));
    when(this.profileFeignClient.checkUserIsAdmin(anyString())).thenReturn(userInfoResponse);
    when(this.portalSettingRepository.save(any())).thenReturn(PORTAL_SETTING);

    PortalSettingConfigStatusDto response =
        this.settingService.modifiedPortalSettingConfig(PORTAL_SETTING_CONFIG_STATUS_DTO, token);

    assertNotNull(response);
  }

  @Test
  @Order(15)
  void testGetDepositTypeOfSettingNotFound() {
    CustomerNotFoundException customerNotFoundException =
        Assertions.assertThrows(
            CustomerNotFoundException.class,
            () -> this.settingService.getDepositModes(CUSTOMER_NAME));
    loggingActualResponse(customerNotFoundException);
  }

  @Test
  @Order(17)
  void testSavePortalSetting() {
    CustomerDepositModeDto customerDepositModeDto = new CustomerDepositModeDto();
    customerDepositModeDto.setKey("flow.traceability.deposit.mode.portal");
    customerDepositModeDto.setValue(DepositModeConstant.PORTAL_DEPOSIT);
    customerDepositModeDto.setScanActivation(true);
    try (MockedStatic<AuthenticationUtils> ignored = mockStatic(AuthenticationUtils.class)) {
      when(profileFeignClient.checkUserIsAdmin(anyString())).thenReturn(USER_INFO_RESPONSE);
      when(settingRepository.findAllByCustomerAndDepositTypes(anyString(), anyList(), anyList()))
          .thenReturn(SETTING_LIST);
      when(settingRepository.save(any())).thenReturn(SETTING_LIST.get(0));
      var result =
          this.settingService.createOrModifiedClientSetting(
              CUSTOMER_NAME, List.of(customerDepositModeDto), "");
      loggingActualResponse(result);
    }
  }

  @Test
  @Order(18)
  void testFailToSavePortalSetting() {
    CustomerDepositModeDto customerDepositModeDto = new CustomerDepositModeDto();
    customerDepositModeDto.setKey("flow.traceability.deposit.mode.portal");
    customerDepositModeDto.setValue(DepositModeConstant.PORTAL_DEPOSIT);
    customerDepositModeDto.setScanActivation(true);
    try (MockedStatic<AuthenticationUtils> ignored = mockStatic(AuthenticationUtils.class)) {
      UserInfoResponse userInfoResponse = USER_INFO_RESPONSE;
      userInfoResponse.setPlatformAdmin(false);
      userInfoResponse.setSuperAdmin(false);
      when(profileFeignClient.checkUserIsAdmin(anyString())).thenReturn(userInfoResponse);
      UserAccessDeniedExceptionHandler userAccessDeniedExceptionHandler = assertThrows(
              UserAccessDeniedExceptionHandler.class,
              () -> this.settingService.createOrModifiedClientSetting(
                      CUSTOMER_NAME, List.of(customerDepositModeDto), ""));
      loggingActualResponse(userAccessDeniedExceptionHandler);
      userInfoResponse.setPlatformAdmin(true);
      userInfoResponse.setSuperAdmin(true);
    }
  }

  @Order(19)
  @Test
  void testModifiedPortalConfigurationAsAdmin_ThenReturnSuccess() {
    var token = "Bearer token";

    when(profileFeignClient.checkUserIsAdmin(anyString())).thenReturn(USER_INFO_RESPONSE);
    when(this.settingRepository.findSettingByCustomQuery(
            anyString(), anyString(), anyString()))
        .thenReturn(Optional.of(SETTING));

    PostalConfigurationDto response =
        this.settingService.modifiedINIConfiguration(POSTAL_CONFIGURATION_DTO, token);
    assertNotNull(response, "Response must be not null");
  }

  @Test
  @Order(20)
  void testModifiedPortalConfigurationNotAdmin_ThenThrowError() {
    var token = "Bearer token";
    final UserInfoResponse userResponse = USER_INFO_RESPONSE;
    userResponse.setPlatformAdmin(false);
    userResponse.setSuperAdmin(false);

    when(profileFeignClient.checkUserIsAdmin(anyString())).thenReturn(userResponse);

    Assertions.assertThrows(
        UserAccessDeniedExceptionHandler.class,
        () -> this.settingService.modifiedINIConfiguration(POSTAL_CONFIGURATION_DTO, token));
  }
}
