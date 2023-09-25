package com.tessi.cxm.pfl.ms3.service;

import static com.tessi.cxm.pfl.ms3.util.ConstantProperties.MOCK_USER_PRIVILEGE_DETAILS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cxm.tessi.pfl.shared.flowtreatment.model.request.ValidationFlowDocumentRequest;
import com.cxm.tessi.pfl.shared.flowtreatment.model.request.ValidationFlowRequest;
import com.tessi.cxm.pfl.ms3.config.InternalConfig;
import com.tessi.cxm.pfl.ms3.constant.FlowValidationConstant;
import com.tessi.cxm.pfl.ms3.dto.FlowDocumentValidationRequest;
import com.tessi.cxm.pfl.ms3.dto.FlowFilterCriteria;
import com.tessi.cxm.pfl.ms3.dto.FlowValidationProjection;
import com.tessi.cxm.pfl.ms3.repository.FlowDocumentRepository;
import com.tessi.cxm.pfl.ms3.repository.FlowTraceabilityRepository;
import com.tessi.cxm.pfl.ms3.service.restclient.ProcessControlFeignClient;
import com.tessi.cxm.pfl.ms3.util.ConstantProperties;
import com.tessi.cxm.pfl.shared.auth.AuthenticationUtils;
import com.tessi.cxm.pfl.shared.model.User;
import com.tessi.cxm.pfl.shared.model.UsersRelatedToPrivilege;
import com.tessi.cxm.pfl.shared.service.keycloak.KeycloakService;
import com.tessi.cxm.pfl.shared.service.restclient.ProfileFeignClient;
import com.tessi.cxm.pfl.shared.utils.FlowTraceabilityStatus;
import com.tessi.cxm.pfl.shared.utils.PrivilegeValidationUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

import org.hibernate.validator.constraints.ModCheck;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ContextConfiguration(classes = {InternalConfig.class})
@Slf4j
class FlowTraceabilityValidationServiceTest {

  private static final String MESSAGE = "Result should be not null.";
  private final ModelMapper modelMapper = new ModelMapper();
  @Mock
  KeycloakService keycloakService;
  @Mock
  ProcessControlFeignClient processControlFeignClient;
  @Mock
  private FlowTraceabilityRepository flowTraceabilityRepository;
  @Mock
  private FlowTraceabilityValidationService flowTraceabilityValidationService;
  @Mock
  private ProfileFeignClient profileFeignClient;
  @Mock
  private FlowDocumentRepository flowDocumentRepository;
  @Mock
  private ReportingService reportingService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    flowTraceabilityValidationService =
        spy(
            new FlowTraceabilityValidationService(
                flowTraceabilityRepository,
                flowDocumentRepository,
                profileFeignClient,
                processControlFeignClient,
                keycloakService,
                modelMapper,
                    reportingService));

    this.flowTraceabilityValidationService.setRepository(flowTraceabilityRepository);
    PrivilegeValidationUtil.setProfileFeignClient(profileFeignClient);
    TransactionSynchronizationManager.initSynchronization();
  }

  @AfterEach
  void destroy() {
    TransactionSynchronizationManager.clear();
  }

  @Test
  @DisplayName("Test get a list of validation flow with pagination success")
  @Order(1)
  void testListFlowValidationWithPagination() {
    try (MockedStatic<AuthenticationUtils> ignored = mockStatic(AuthenticationUtils.class)) {
      when(profileFeignClient.getUserPrivilegeRelatedOwner(
          anyString(), anyString(), anyString(), anyBoolean(), anyBoolean()))
          .thenReturn(MOCK_USER_PRIVILEGE_DETAILS);

      lenient()
          .when(
              this.flowTraceabilityRepository.findAll(
                  any(Specification.class), any(Pageable.class)))
          .thenReturn(new PageImpl<>(Collections.emptyList()));

      // Call service.
      Pageable pageable = PageRequest.of(1, 10);
      final var filterCriteria = new FlowFilterCriteria();
      filterCriteria.setChannels(List.of());
      filterCriteria.setCategories(List.of());
      filterCriteria.setUsers(List.of());
      filterCriteria.setStartDate("");
      filterCriteria.setEndDate("");
      filterCriteria.setFilter("");
      final var result =
          this.flowTraceabilityValidationService.getFlowValidationList(pageable, filterCriteria);
      // Expected.
      Assertions.assertNotNull(result.getContent(), MESSAGE);
      log.info("Result expected => {}", result.getContent());
    }
  }

  @Test
  @DisplayName("Test refuse flow success")
  @Order(2)
  @Transactional
  void testRefuseFlow() {
    try (MockedStatic<AuthenticationUtils> ignored = mockStatic(AuthenticationUtils.class)) {
      when(ReflectionTestUtils.invokeMethod(flowTraceabilityValidationService,
          "getAuthTokenWithPrefix")).thenReturn("Bearer tokens");

      when(profileFeignClient.getUserPrivilegeRelatedOwner(
          anyString(), anyString(), anyString(), anyBoolean(), anyBoolean()))
          .thenReturn(MOCK_USER_PRIVILEGE_DETAILS);

      // Mock relative flow validation can refuse able.
      final var flowTraceability = ConstantProperties.FLOW_TRACEABILITY;
      flowTraceability.setCreatedBy("john-doe@example.com");
      lenient()
          .when(flowTraceabilityRepository.findAll(any(Specification.class)))
          .thenReturn(List.of(flowTraceability));
      // Mock current user information.
      final var user = new User();
      user.setUsername("john-doe@example.com");
      lenient().when(keycloakService.getUserInfo()).thenReturn(user);
      // Mock refusing flow.
      lenient()
          .when(flowTraceabilityRepository.saveAll(anyCollection()))
          .thenReturn(Collections.emptyList());

      lenient()
          .when(flowDocumentRepository.saveAll(anyCollection()))
          .thenReturn(Collections.emptyList());
      // Call service.
      final var result =
          flowTraceabilityValidationService.validateFlow(
              List.of("7b7ebea3-9d55-4e4a-b4e2-20330b5cd7ce"),
              FlowTraceabilityStatus.REFUSE_DOC.getValue());
      //TransactionSynchronizationManager.getSynchronizations().get(0).afterCompletion(anyInt());
      // Expected.
      Assertions.assertNotNull(result, MESSAGE);
      log.info("Result expected => {}", result);
    }
  }

  @Test
  @DisplayName("Test validate flow success")
  @Order(3)
  void testValidateFlowSuccess() {
    try (MockedStatic<AuthenticationUtils> ignored = mockStatic(AuthenticationUtils.class)) {
      when(profileFeignClient.getUserPrivilegeRelatedOwner(
          anyString(), anyString(), anyString(), anyBoolean(), anyBoolean()))
          .thenReturn(MOCK_USER_PRIVILEGE_DETAILS);

      // Mock relative flow validation can validate able.
      final var flowTraceability = ConstantProperties.FLOW_TRACEABILITY;
      flowTraceability.setCreatedBy("John Doe");
      lenient()
          .when(flowTraceabilityRepository.findAll(any(Specification.class)))
          .thenReturn(List.of(flowTraceability));
      // Mock current user information.
      final var user = new User();
      user.setUsername("john-doe@example.com");
      lenient().when(keycloakService.getUserInfo()).thenReturn(user);
      lenient().when(profileFeignClient.getClientUnloadDetails(anyString()))
          .thenReturn(ConstantProperties.SHARED_CLIENT_UNLOAD_DETAILS_DTO);
      // Mock validating flow.
      lenient()
          .when(flowTraceabilityRepository.saveAll(anyCollection()))
          .thenReturn(Collections.emptyList());
      lenient()
          .when(flowDocumentRepository.saveAll(anyCollection()))
          .thenReturn(Collections.emptyList());
      lenient()
          .when(
              processControlFeignClient.validateFlow(any(ValidationFlowRequest.class), anyString()))
          .thenReturn("7b7ebea3-9d55-4e4a-b4e2-20330b5cd7ce");
      // Call service.
      final var result =
          flowTraceabilityValidationService.validateFlow(
              List.of("7b7ebea3-9d55-4e4a-b4e2-20330b5cd7ce"),
              FlowTraceabilityStatus.VALIDATED.getValue());
      // Expected.
      Assertions.assertNotNull(result, MESSAGE);
      log.info("Result expected => {}", result);
    }
  }

  @Test
  @DisplayName("Test validate validation flow fail and set flow status to In Error")
  @Order(4)
  void testValidateFlowFail() {
    try (MockedStatic<AuthenticationUtils> ignored = mockStatic(AuthenticationUtils.class)) {
      when(profileFeignClient.getUserPrivilegeRelatedOwner(
          anyString(), anyString(), anyString(), anyBoolean(), anyBoolean()))
          .thenReturn(MOCK_USER_PRIVILEGE_DETAILS);

      // Mock relative flow validation can validate able.
      final var flowTraceability = ConstantProperties.FLOW_TRACEABILITY;
      flowTraceability.setCreatedBy("john-doe@example.com");
      lenient()
          .when(flowTraceabilityRepository.findAll(any(Specification.class)))
          .thenReturn(List.of(flowTraceability));
      // Mock current user information.
      final var user = new User();
      user.setUsername("john-doe@example.com");
      lenient().when(this.keycloakService.getUserInfo()).thenReturn(user);
      lenient().when(profileFeignClient.getClientUnloadDetails(anyString()))
          .thenReturn(ConstantProperties.SHARED_CLIENT_UNLOAD_DETAILS_DTO);
      // Mock validating flow.
      lenient()
          .when(flowTraceabilityRepository.saveAll(anyCollection()))
          .thenReturn(Collections.emptyList());
      lenient()
          .when(flowDocumentRepository.saveAll(anyCollection()))
          .thenReturn(Collections.emptyList());
      // Call service.
      final var result =
          flowTraceabilityValidationService.validateFlow(
              List.of("7b7ebea3-9d55-4e4a-b4e2-20330b5cd7ce"),
              FlowTraceabilityStatus.VALIDATED.getValue());
      // Expected.
      Assertions.assertNotNull(result, MESSAGE);
      log.info("Result expected => {}", result);
    }
  }

  @Test
  @DisplayName("Test get a list of validation flow document with pagination success")
  @Order(5)
  void testListFlowDocValidationWithPagination() {
    try (MockedStatic<AuthenticationUtils> ignored = mockStatic(AuthenticationUtils.class)) {
      when(profileFeignClient.getUserPrivilegeRelatedOwner(
          anyString(), anyString(), anyString(), anyBoolean(), anyBoolean()))
          .thenReturn(MOCK_USER_PRIVILEGE_DETAILS);

      lenient()
          .when(flowTraceabilityRepository.findById(1L))
          .thenReturn(Optional.of(ConstantProperties.FLOW_TRACEABILITY));

      // Mock repository.
      lenient()
          .when(this.flowDocumentRepository.findAll(any(Specification.class), any(Pageable.class)))
          .thenReturn(new PageImpl<>(Collections.emptyList()));

      // Call service.
      Pageable pageable = PageRequest.of(1, 10);
      final var result =
          this.flowTraceabilityValidationService.getFlowDocumentValidationList("", 1L, pageable);

      // Expected.
      Assertions.assertNotNull(result.getContent(), MESSAGE);
      log.info("Result expected => {}", result.getContent());
    }
  }

  @Test
  @DisplayName("Test validate flow document success")
  @Order(6)
  void testValidateFlowDocument() {
    try (MockedStatic<AuthenticationUtils> utils = mockStatic(AuthenticationUtils.class);
        MockedStatic<TransactionSynchronizationManager> ignored =
            mockStatic(TransactionSynchronizationManager.class)) {
      when(profileFeignClient.getUserPrivilegeRelatedOwner(
          anyString(), anyString(), anyString(), anyBoolean(), anyBoolean()))
          .thenReturn(MOCK_USER_PRIVILEGE_DETAILS);

      lenient()
          .when(flowTraceabilityRepository.findById(anyLong()))
          .thenReturn(Optional.of(ConstantProperties.FLOW_TRACEABILITY));
      lenient().when(profileFeignClient.getClientUnloadDetails(anyString()))
          .thenReturn(ConstantProperties.SHARED_CLIENT_UNLOAD_DETAILS_DTO);
      // Mock validating flow
      lenient()
          .when(flowTraceabilityRepository.getDocumentDocIdToValidate(anyLong()))
          .thenReturn(
              Optional.of(
                  new FlowValidationProjection() {
                    @Override
                    public String getFileId() {
                      return "2fd09f9c-42ce-11ed-b878-0242ac120002";
                    }

                    @Override
                    public String getComposedId() {
                      return "2fd09f9c-42ce-11ed-b878-0242ac120002";
                    }
                  }));
      lenient()
          .when(
              processControlFeignClient.validateFlowDocument(
                  new ValidationFlowDocumentRequest(
                      "2fd09f9c-42ce-11ed-b878-0242ac120001",
                      "2fd09f9c-42ce-11ed-b878-0242ac120002",
                      "john-doe@example.com",
                      Collections.emptyList(), true),
                  "token"))
          .thenReturn("");
      // Call service.
      flowTraceabilityValidationService.validateFlowDocument(
          FlowDocumentValidationRequest.builder()
              .flowId(1L)
              .documentIds(List.of("2fd09f9c-42ce-11ed-b878-0242ac120003"))
              .action(FlowValidationConstant.VALIDATE)
              .build());
      verify(flowTraceabilityValidationService)
          .validateFlowDocument(any(FlowDocumentValidationRequest.class));
      log.info("Validate flow document successfully.");
    }
  }

  @Test
  @DisplayName("Test refuse flow document success")
  @Order(7)
  void testRefuseFlowDocument() {
    try (MockedStatic<AuthenticationUtils> utils = mockStatic(AuthenticationUtils.class);
        MockedStatic<TransactionSynchronizationManager> ignored =
            mockStatic(TransactionSynchronizationManager.class)) {
      // Mock authorization.
      utils
          .when(() -> AuthenticationUtils.getPrincipal(any(Authentication.class)))
          .thenReturn("John Doe");
      lenient()
          .when(
              profileFeignClient.getUserPrivilegeRelated(
                  anyString(), anyString(), anyString(), anyBoolean()))
          .thenReturn(
              new UsersRelatedToPrivilege(
                  "privilege", "service", Collections.singletonList("john-doe@example.com"),
                  new ArrayList<>()));
      lenient()
          .when(flowTraceabilityRepository.findById(anyLong()))
          .thenReturn(Optional.of(ConstantProperties.FLOW_TRACEABILITY));
      when(profileFeignClient.getUserPrivilegeRelatedOwner(
          anyString(), anyString(), anyString(), anyBoolean(), anyBoolean()))
          .thenReturn(MOCK_USER_PRIVILEGE_DETAILS);

      // Mock validating flow
      lenient()
          .when(flowTraceabilityRepository.getDocumentDocIdToValidate(anyLong()))
          .thenReturn(
              Optional.of(
                  new FlowValidationProjection() {
                    @Override
                    public String getFileId() {
                      return "2fd09f9c-42ce-11ed-b878-0242ac120002";
                    }

                    @Override
                    public String getComposedId() {
                      return "2fd09f9c-42ce-11ed-b878-0242ac120002";
                    }
                  }));
      lenient()
          .when(
              processControlFeignClient.validateFlowDocument(
                  new ValidationFlowDocumentRequest(
                      "2fd09f9c-42ce-11ed-b878-0242ac120001",
                      "2fd09f9c-42ce-11ed-b878-0242ac120002",
                      "john-doe@example.com",
                      Collections.emptyList(), true),
                  "token"))
          .thenReturn("");
      // Call service.

      flowTraceabilityValidationService.validateFlowDocument(
          FlowDocumentValidationRequest.builder()
              .flowId(1L)
              .documentIds(List.of("2fd09f9c-42ce-11ed-b878-0242ac120003"))
              .action(FlowValidationConstant.REFUSE)
              .build());
      verify(flowTraceabilityValidationService)
          .validateFlowDocument(any(FlowDocumentValidationRequest.class));
      log.info("Refuse flow document successfully.");
    }
  }
}
