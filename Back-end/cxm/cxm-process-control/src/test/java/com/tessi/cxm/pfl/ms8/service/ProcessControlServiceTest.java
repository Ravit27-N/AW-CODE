package com.tessi.cxm.pfl.ms8.service;

import static com.cxm.tessi.pfl.shared.flowtreatment.constant.ProcessControlStep.IDENTIFICATION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants;
import com.cxm.tessi.pfl.shared.flowtreatment.model.request.DepositedFlowLaunchRequest;
import com.tessi.cxm.pfl.ms8.core.flow.BatchFlowProcessingManager;
import com.tessi.cxm.pfl.ms8.core.flow.portal.pdf.ManualFlowAnalyzer;
import com.tessi.cxm.pfl.ms8.core.flow.portal.pdf.ManualFlowIdentifier;
import com.tessi.cxm.pfl.ms8.core.flow.portal.pdf.ManualFlowProcessor;
import com.tessi.cxm.pfl.ms8.core.flow.portal.pdf.ManualFlowScheduler;
import com.tessi.cxm.pfl.ms8.core.flow.portal.pdf.ManualFlowSwitcher;
import com.tessi.cxm.pfl.ms8.core.flow.portal.pdf.ManualFlowValidation;
import com.tessi.cxm.pfl.ms8.core.flow.portal.pdf.ManualProcessFlowDocumentValidation;
import com.tessi.cxm.pfl.ms8.core.flow.portal.pdf.ManualProcessFlowValidation;
import com.tessi.cxm.pfl.ms8.core.flow.portal.pdf.ManualReIdentifierFlow;
import com.tessi.cxm.pfl.ms8.dto.FlowUnloadingPayload;
import com.tessi.cxm.pfl.ms8.repository.ResourceFileRepository;
import com.tessi.cxm.pfl.ms8.repository.UnloadingSchedulerJobRepository;
import com.tessi.cxm.pfl.ms8.service.restclient.FileCtrlMngtFeignClient;
import com.tessi.cxm.pfl.shared.auth.AuthenticationUtils;
import com.tessi.cxm.pfl.shared.core.chains.ExecutionContext;
import com.tessi.cxm.pfl.shared.exception.UserAccessDeniedExceptionHandler;
import com.tessi.cxm.pfl.shared.filectrl.model.portal.PortalFlowFileControl;
import com.tessi.cxm.pfl.shared.model.User;
import com.tessi.cxm.pfl.shared.model.UserDetail;
import com.tessi.cxm.pfl.shared.model.UserPrivilegeDetails;
import com.tessi.cxm.pfl.shared.service.keycloak.KeycloakService;
import com.tessi.cxm.pfl.shared.service.restclient.ProfileFeignClient;
import com.tessi.cxm.pfl.shared.service.restclient.SettingFeignClient;
import com.tessi.cxm.pfl.shared.utils.BearerAuthentication;

import java.awt.*;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
@Slf4j
class ProcessControlServiceTest {
  private static final String USERNAME = "dev@gmail.com";
  @Mock private StreamBridge streamBridge;
  @Mock private KeycloakService keycloakService;
  @Mock private BatchFlowProcessingManager batchFlowProcessingManager;
  @Mock private ManualFlowIdentifier manualFlowIdentifier;
  @Mock private ManualFlowAnalyzer manualFlowAnalyzer;
  @Mock private ManualFlowProcessor manualFlowProcessor;
  @Mock private ManualFlowSwitcher manualFlowSwitcher;
  @Mock private FlowUnloadingService flowUnloadingService;

  @Mock private ProfileFeignClient profileFeignClient;
  @Mock private ManualProcessFlowDocumentValidation manualProcessFlowDocumentValidation;

  private ManualProcessFlowValidation manualProcessFlowValidation;
  private ManualFlowValidation manualFlowValidation;
  @Mock private FileCtrlMngtFeignClient fileCtrlMngtFeignClient;
  @Mock private ManualFlowScheduler manualFlowScheduler;
  @Mock private ProcessControlService processControlService;
  @Mock private UnloadingSchedulerJobRepository unloadingSchedulerJobRepository;
  @Mock private ProcessControlSchedulerService processControlSchedulerService;
  @Mock private ResourceFileService resourceFileService;
  @Mock private  ManualReIdentifierFlow manualReIdentifierFlow;
  private  SettingFeignClient settingFeignClient;
  private  ResourceFileRepository resourceFileRepository;
  @BeforeEach
  void setUp() {
    this.processControlService =
        spy(
            new ProcessControlService(
                resourceFileService,
                streamBridge,
                keycloakService,
                batchFlowProcessingManager,
                manualFlowIdentifier,
                manualFlowAnalyzer,
                manualFlowProcessor,
                manualFlowSwitcher,
                manualProcessFlowDocumentValidation,manualReIdentifierFlow,settingFeignClient,resourceFileRepository));

    this.processControlService.setManualFlowScheduler(manualFlowScheduler);
    this.processControlService.setProfileFeignClient(profileFeignClient);
    this.processControlService.setFileCtrlMngtFeignClient(fileCtrlMngtFeignClient);
    this.processControlService.setProcessControlSchedulerService(processControlSchedulerService);
  }

  @Test
  @Order(1)
  void whenLaunchProcess_thenSuccess() {
    var mockRequest =
        DepositedFlowLaunchRequest.builder()
            .depositDate(new Date())
            .flowType("ENI/Batch/C1/zip")
            .connector("C1")
            .customer("ENI")
            .depositType("Batch")
            .extension("zip")
            .idCreator(1L)
            .serviceId("cxm-test")
            .fileId("FILE_ID")
            .build();

    var context = new ExecutionContext();
    context.put(FlowTreatmentConstants.DEPOSITED_FLOW_LAUNCH_REQUEST, mockRequest);
    context.put(FlowTreatmentConstants.FLOW_TYPE, mockRequest.getFlowType());
    context.put(FlowTreatmentConstants.BEARER_TOKEN, null);
    context.put(FlowTreatmentConstants.FUNC_KEY, "");
    context.put(FlowTreatmentConstants.PRIV_KEY, "");
    processControlService.launch(mockRequest);
    verify(this.batchFlowProcessingManager, times(1)).execute(context);
  }

  @Test
  @Order(2)
  void whenLaunchProcess_withDepositIV_thenSuccess() {
    var mockRequest =
        DepositedFlowLaunchRequest.builder()
            .depositDate(new Date())
            .flowType("tessi/IV/pdf")
            .customer("tessi")
            .depositType("IV")
            .extension("pdf")
            .serviceId("cxm-test")
            .fileId("FILE_ID")
            .build();

    try (MockedStatic<AuthenticationUtils> utils = mockStatic(AuthenticationUtils.class)) {
      utils
          .when(() -> AuthenticationUtils.getPrincipal(any(Authentication.class)))
          .thenReturn("dev@gmail.com");
      final String token = UUID.randomUUID().toString();
      var context = new ExecutionContext();
      context.put(FlowTreatmentConstants.DEPOSITED_FLOW_LAUNCH_REQUEST, mockRequest);
      context.put(FlowTreatmentConstants.FLOW_TYPE, mockRequest.getFlowType());
      context.put(FlowTreatmentConstants.BEARER_TOKEN, token);
      context.put(FlowTreatmentConstants.USER_FULL_NAME, null);
      context.put(FlowTreatmentConstants.USERNAME, null);
      context.put(FlowTreatmentConstants.FUNC_KEY, "cxm_flow_deposit");
      context.put(FlowTreatmentConstants.PRIV_KEY, "send_a_letter");
      context.put(FlowTreatmentConstants.OWNER_ID, 1L);
      context.put(FlowTreatmentConstants.PROCESS_CONTROL_FLOW_PROCESSING_STEP, IDENTIFICATION);
      final User user = new User();
      user.setUsername("dev@gmail.com");
      UserDetail userDetail = new UserDetail();
      userDetail.setOwnerId(1L);
      when(profileFeignClient.getUserDetail(any())).thenReturn(userDetail);
      processControlService.identifyDepositedFlow(mockRequest, token);
      verify(this.manualFlowIdentifier, times(1)).execute(context);
    }
  }

  //  @Test
  //  @Order(3)
  //  void testSendFlow_AndDepositFlowSchedule_thenReturnSuccess() {
  //    try (MockedStatic<AuthenticationUtils> utils = mockStatic(AuthenticationUtils.class)) {
  //      utils
  //          .when(() -> AuthenticationUtils.getPrincipal(any(Authentication.class)))
  //          .thenReturn(USERNAME);
  //
  //      utils.when(AuthenticationUtils::getAuthToken).thenReturn(USERNAME);
  //
  //      MockedStatic<ProcessControlExecutionContextUtils> contextUtilsMockedStatic =
  //          mockStatic(ProcessControlExecutionContextUtils.class);
  //      contextUtilsMockedStatic
  //          .when(
  //              () ->
  // ProcessControlExecutionContextUtils.getBearerToken(any(ExecutionContext.class)))
  //          .thenReturn(USERNAME);
  //
  //      contextUtilsMockedStatic
  //          .when(() -> ProcessControlExecutionContextUtils.getBearerTokenWithPrefix(anyString()))
  //          .thenReturn(USERNAME);
  //
  //      when(keycloakService.getUserInfo()).thenReturn(ConstantProperties.USER);
  //
  //      when(profileFeignClient.checkContainPrivilegeKey(anyString(), anyString(), anyString()))
  //          .thenReturn(true);
  //
  //      when(profileFeignClient.getClientUnloadDetails(anyString()))
  //          .thenReturn(ConstantProperties.SHARED_CLIENT_UNLOAD_DETAILS_DTO);
  //      var context = new ExecutionContext();
  //      context.put(FlowTreatmentConstants.CREATED_BY, USERNAME);
  //      context.put(FlowTreatmentConstants.FLOW_UUID, "8cd0e247-ceef-42b8-9a40-570e20c83d76");
  //      context.put(FlowTreatmentConstants.COMPOSED_FILE_ID,
  // "41bb5a9b-62ac-461e-8175-82236a7f36bc");
  //      context.put(FlowTreatmentConstants.BEARER_TOKEN, USERNAME);
  //      context.put(FlowTreatmentConstants.IS_DOCUMENT_VALIDATION, false);
  //      context.put(
  //          FlowTreatmentConstants.SWITCH_FLOW_RESPONSE, ConstantProperties.SWITCH_FLOW_RESPONSE);
  //      context.put(FlowTreatmentConstants.PORTAL_DEPOSIT, FlowTreatmentConstants.PORTAL_DEPOSIT);
  //      context.put(FlowTreatmentConstants.DEPOSITED_FLOW_LAUNCH_REQUEST,
  // ConstantProperties.DEPOSITED_FLOW_LAUNCH_REQUEST);
  //      context.put(FlowTreatmentConstants.IS_VALIDATE_ACTION, false);
  //      context.put(
  //          ProcessControlConstants.CLIENT_UNLOADING_DETAIL,
  //          ConstantProperties.SHARED_CLIENT_UNLOAD_DETAILS_DTO);
  //
  //      this.processControlService.sendFlow(
  //          "8cd0e247-ceef-42b8-9a40-570e20c83d76",
  //          "41bb5a9b-62ac-461e-8175-82236a7f36bc",
  //          false,
  //          false);
  //
  //      verify(this.manualFlowScheduler, times(1)).execute(context);
  //    }
  //  }

  @Test
  @Order(4)
  void testCancelFlow_thenSuccess() {
    try (MockedStatic<BearerAuthentication> bearerAuthenticationMockedStatic =
            mockStatic(BearerAuthentication.class);
        MockedStatic<AuthenticationUtils> utils = mockStatic(AuthenticationUtils.class)) {
      utils
          .when(() -> AuthenticationUtils.getPrincipal(any(Authentication.class)))
          .thenReturn("dev@gmail.com");
      var mockUser =
          new User("1", "test@gmail.com", "Dev", "TEST", "test@gmail.com", new Date(), "123");
      when(this.keycloakService.getUserInfo()).thenReturn(mockUser);
      final PortalFlowFileControl portalFlowFileControl = new PortalFlowFileControl();
      portalFlowFileControl.setUserId("1");
      when(this.fileCtrlMngtFeignClient.getPortalJsonFileControl(anyString(), anyString()))
          .thenReturn(portalFlowFileControl);
      when(profileFeignClient.getUserPrivilegeRelatedOwner(any(), anyString(), anyString(), anyBoolean(),anyBoolean()))
              .thenReturn(new UserPrivilegeDetails("", "", false, List.of(1L)));
      this.processControlService.cancelFlow("e1c63607-cf91-42fd-b4aa-9d3d50adab73");

      verify(this.processControlService, times(1)).cancelFlow(anyString());
    }
  }

  @Test
  @Order(5)
  void testCancelFlow_thenFail() {
    try (MockedStatic<AuthenticationUtils> utils = mockStatic(AuthenticationUtils.class)) {
      utils
          .when(() -> AuthenticationUtils.getPrincipal(any(Authentication.class)))
          .thenReturn("dev@gmail.com");
      final PortalFlowFileControl portalFlowFileControl = new PortalFlowFileControl();
      portalFlowFileControl.setUserId("1");
      when(this.fileCtrlMngtFeignClient.getPortalJsonFileControl(anyString(), anyString()))
          .thenReturn(portalFlowFileControl);
      when(profileFeignClient.getUserPrivilegeRelatedOwner(
              any(), anyString(), anyString(), anyBoolean(),anyBoolean()))
          .thenReturn(new UserPrivilegeDetails("", "", false, List.of(2L)));

      Assertions.assertThrows(
          UserAccessDeniedExceptionHandler.class,
          () -> this.processControlService.cancelFlow("e1c63607-cf91-42fd-b4aa-9d3d50adab73"));
    }
  }

  @Test
  @Order(6)
  void testForceUnloadFlow_thenSuccess() {
    ArgumentCaptor<FlowUnloadingPayload> unloadingPayloadArgCaptor =
        ArgumentCaptor.forClass(FlowUnloadingPayload.class);
    ArgumentCaptor<Date> dateArgCaptor = ArgumentCaptor.forClass(Date.class);
    ArgumentCaptor<Boolean> booleanArgCaptor = ArgumentCaptor.forClass(Boolean.class);
    lenient()
        .doNothing()
        .when(this.flowUnloadingService)
        .unloadFlow(
            unloadingPayloadArgCaptor.capture(),
            dateArgCaptor.capture(),
            booleanArgCaptor.capture());
    this.flowUnloadingService.unloadFlow(
        FlowUnloadingPayload.builder().clientId(1L).build(), new Date(), true);
    assertEquals(1L, unloadingPayloadArgCaptor.getValue().getClientId());
    assertEquals(true, booleanArgCaptor.getValue());

    verify(this.flowUnloadingService, times(1))
        .unloadFlow(any(FlowUnloadingPayload.class), any(Date.class), anyBoolean());
  }

  @Test
  @Order(7)
  void testForceUnloadFlow_thenFail() {
    lenient()
        .doThrow(new NullPointerException())
        .when(flowUnloadingService)
        .unloadFlow(any(FlowUnloadingPayload.class), any(Date.class), anyBoolean());

    Assertions.assertThrows(
        NullPointerException.class,
        () ->
            this.flowUnloadingService.unloadFlow(
                FlowUnloadingPayload.builder().build(), new Date(), true));
  }
}
