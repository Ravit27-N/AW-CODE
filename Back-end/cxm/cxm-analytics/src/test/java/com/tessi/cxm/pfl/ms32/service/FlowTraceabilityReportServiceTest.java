package com.tessi.cxm.pfl.ms32.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import com.tessi.cxm.pfl.ms32.dto.FlowDepositMode;
import com.tessi.cxm.pfl.ms32.exception.DomainException;
import com.tessi.cxm.pfl.ms32.repository.FlowTraceabilityReportRepository;
import com.tessi.cxm.pfl.ms32.utils.ConstantProperties;
import com.tessi.cxm.pfl.shared.exception.UserAccessDeniedExceptionHandler;
import com.tessi.cxm.pfl.shared.service.keycloak.KeycloakService;
import com.tessi.cxm.pfl.shared.service.restclient.ProfileFeignClient;
import com.tessi.cxm.pfl.shared.utils.PrivilegeValidationUtil;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.util.Assert;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
class FlowTraceabilityReportServiceTest {

  private FlowTraceabilityReportService flowTraceabilityReportService;

  @Mock
  private ModelMapper modelMapper;

  @Mock
  private FilterPreferenceService filterPreferenceService;

  @Mock
  private FlowTraceabilityReportRepository repository;

  @Mock
  private ProfileFeignClient profileFeignClient;


  @Mock
  private KeycloakService keycloakService;


  private final String dumpToken = "test-token";
  private final String dumpAdminToken = "test-with-admin-token";
  private static final Date requestedAt = new Date();


  @BeforeEach()
  void setup() {
    this.flowTraceabilityReportService = new FlowTraceabilityReportService(
        repository,
        this.modelMapper,
        repository,
        filterPreferenceService,
        profileFeignClient,
        keycloakService);

    lenient().doReturn(MockData.mockUserInfoResponse)
        .when(profileFeignClient).checkUserIsAdmin(eq(dumpToken));

    lenient().doReturn(MockData.mockUserFilterPreferenceDto)
        .when(filterPreferenceService).getFilterPreference(anyString());
  }


  @Test
  @Order(1)
  void testGetByChannelModeGraph() {

    // Mock data
    var firstValueTest = 55.5556;

    doReturn(100L).when(this.repository)
        .countDocumentPerChannel(anyCollection(), eq(MockData.PostalParamter), any(), any(), eq(requestedAt));

    doReturn(50L).when(this.repository)
        .countDocumentPerChannel(anyCollection(), eq(MockData.EmailParamter), any(), any(), eq(requestedAt));

    doReturn(30L).when(this.repository)
        .countDocumentPerChannel(anyCollection(), eq(MockData.SMSParamter), any(), any(), eq(requestedAt));

    try (MockedStatic<PrivilegeValidationUtil> utils = mockStatic(PrivilegeValidationUtil.class)) {
      // Mock static check privilege
      utils.when(() -> PrivilegeValidationUtil.getUserPrivilegeDetails(anyString(), anyString(),
              anyBoolean(), anyBoolean()))
          .thenReturn(MockData.mockUserPrivilegeDetails);

      try {
        var report = flowTraceabilityReportService.getByGroupSubChannel(this.dumpToken, requestedAt);
        Assert.isTrue(report.size() == 3,
            "Expected result response with less or more than 3 channels");
        assertTrue(report.get(0).getKey().equals("Postal"));
        assertEquals(firstValueTest, report.get(0).getValue(), "Unexpected result of calculation");
      } catch (DomainException dex) {
        assertNotEquals(dex.getCode(), 204);
      }
    }
  }

  @Test
  @Order(2)
  void testGetByDepositMode() {

    try (MockedStatic<PrivilegeValidationUtil> utils = mockStatic(PrivilegeValidationUtil.class)) {
      // Mock static check privilege
      utils
          .when(
              () ->
                  PrivilegeValidationUtil.getUserPrivilegeDetails(
                      anyString(), anyString(), anyBoolean(), anyBoolean()))
          .thenReturn(MockData.mockUserPrivilegeDetails);

      when(this.repository.countTotalFlowPerDepositModes(any(), any(), any(), eq(requestedAt)))
          .thenReturn(this.mockGetFlowDepositModes());

      var report = flowTraceabilityReportService.getDepositModes(dumpToken, requestedAt);
      Assert.isTrue(
          report.size() == 4, "Expected result response with less or more than 4 channels");
      assertEquals(
          "flow.traceability.deposit.mode.portal",
          report.get(0).getKey(),
          "Portal should be first of report");
      assertEquals(50L, report.get(0).getValue(), "Unexpected result of calculation");
    } catch (DomainException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  @Order(3)
  void checkUserAdminCannotViewReport() {
    doReturn(MockData.mockAdminInfoResponse)
        .when(profileFeignClient).checkUserIsAdmin(eq(dumpAdminToken));

    // Mock data
    try (MockedStatic<PrivilegeValidationUtil> utils = mockStatic(PrivilegeValidationUtil.class)) {
      // Mock static check privilege
      utils.when(() -> PrivilegeValidationUtil.getUserPrivilegeDetails(anyString(), anyString(),
              anyBoolean(), anyBoolean()))
          .thenReturn(MockData.mockUserPrivilegeDetails);

      var thrown = assertThrows(
          DomainException.class,
          () -> flowTraceabilityReportService.getByGroupSubChannel(dumpAdminToken, requestedAt),
          "Expected to throw domain exception");

      assertEquals(thrown.getCode(), 204);
    }
  }


  @Test
  @Order(4)
  void testFlowDocumentReportByChannel() {
    // Mock data
    doReturn(MockData.mockListFlowDocumentReport).when(this.repository)
        .reportDocument(anyCollection(), eq(MockData.PostalParamter), any(), any(), eq(requestedAt));

    doReturn(MockData.mockListFlowDocumentReport).when(this.repository)
        .reportDocument(anyCollection(), eq(MockData.EmailParamter), any(), any(), eq(requestedAt));

    doReturn(MockData.mockListFlowDocumentReport).when(this.repository)
        .reportDocument(anyCollection(), eq(MockData.SMSParamter), any(), any(), eq(requestedAt));

    try (MockedStatic<PrivilegeValidationUtil> utils = mockStatic(PrivilegeValidationUtil.class)) {
      // Mock static check privilege
      utils.when(() -> PrivilegeValidationUtil.getUserPrivilegeDetails(anyString(), anyString(),
              anyBoolean(), anyBoolean()))
          .thenReturn(MockData.mockUserPrivilegeDetails);

      try {
        var report = flowTraceabilityReportService.getFlowDocumentReport(dumpToken, requestedAt);
        Assert.isTrue(report.getResult().size() == 4,
            "Expected result response with less or more than 4 channels");
        assertEquals(MockData.firstReportPercentageValue,
            report.getResult().get(0).getCompletedPercentage());
      } catch (DomainException dex) {
        assertNotEquals(dex.getCode(), 204);
      }
    }
  }

  @Test
  @Order(5)
  void testGetEvolutionOfVolumesByChannel_ThenReturnSuccess() throws DomainException {
    String token = "Bearer token";

    try (MockedStatic<PrivilegeValidationUtil> privilegeUtilMockedStatic = mockStatic(
        PrivilegeValidationUtil.class)) {
      privilegeUtilMockedStatic
          .when(() -> PrivilegeValidationUtil.getUserPrivilegeDetails(anyString(), anyString(),
              anyBoolean(), anyBoolean()))
          .thenReturn(ConstantProperties.USER_PRIVILEGE_DETAILS);

      when(this.profileFeignClient.checkUserIsAdmin(token)).thenReturn(
          ConstantProperties.USER_INFO_RESPONSE);

      when(this.filterPreferenceService.getFilterPreference(token))
          .thenReturn(ConstantProperties.USER_FILTER_PREFERENCE_DTO);

      when(this.repository.reportDocumentTimeSeries(anyCollection(), anyCollection(),
          any(Date.class), any(Date.class), eq(requestedAt)))
          .thenReturn(List.of(ConstantProperties.FLOW_DOCUMENT_TIME_SERIES_PROJECTION));

      var result = this.flowTraceabilityReportService.getFlowDocumentEvolutionReport(token, requestedAt);

      log.info("Result: {}", result);
      Assertions.assertNotNull(result, "Result must be not null");
    }
  }

  @Test
  @Order(6)
  void testGetEvolutionOfVolumesByChannel_ThenThrowUserAccessDeniedException() {
    String token = "Bearer token";

    try (MockedStatic<PrivilegeValidationUtil> privilegeUtilMockedStatic = mockStatic(
        PrivilegeValidationUtil.class)) {
      privilegeUtilMockedStatic
          .when(() -> PrivilegeValidationUtil.getUserPrivilegeDetails(anyString(), anyString(),
              anyBoolean(), anyBoolean()))
          .thenThrow(new UserAccessDeniedExceptionHandler());

      when(this.profileFeignClient.checkUserIsAdmin(token)).thenReturn(
          ConstantProperties.USER_INFO_RESPONSE);

      Assertions.assertThrows(UserAccessDeniedExceptionHandler.class,
          () -> this.flowTraceabilityReportService.getFlowDocumentEvolutionReport(token, requestedAt));
    }
  }

  private List<FlowDepositMode> mockGetFlowDepositModes() {
    return List.of(
            new FlowDepositMode() {

              @Override
              public String getKey() {
                return "Portal";
              }

              @Override
              public Long getValue() {
                return 25L;
              }
            },
            new FlowDepositMode() {

              @Override
              public String getKey() {
                return "Batch";
              }

              @Override
              public Long getValue() {
                return 25L;
              }
            });
  }
}
