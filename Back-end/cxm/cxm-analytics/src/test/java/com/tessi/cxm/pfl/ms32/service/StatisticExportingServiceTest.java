package com.tessi.cxm.pfl.ms32.service;

import static com.tessi.cxm.pfl.ms32.utils.ConstantProperties.SHARED_CLIENT_FILLERS_DTOS;
import static com.tessi.cxm.pfl.ms32.utils.ConstantProperties.getRequestFillerMock;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tessi.cxm.pfl.ms32.config.CSVExportingProperties;
import com.tessi.cxm.pfl.ms32.constant.CSVExportingConstant;
import com.tessi.cxm.pfl.ms32.constant.ExportType;
import com.tessi.cxm.pfl.ms32.dto.StatisticExportingRequestFilter;
import com.tessi.cxm.pfl.ms32.repository.FlowTraceabilityReportRepository;
import com.tessi.cxm.pfl.ms32.utils.ConstantProperties;
import com.tessi.cxm.pfl.shared.auth.AuthenticationUtils;
import com.tessi.cxm.pfl.shared.core.Context;
import com.tessi.cxm.pfl.shared.exception.BadRequestException;
import com.tessi.cxm.pfl.shared.exception.UserAccessDeniedExceptionHandler;
import com.tessi.cxm.pfl.shared.service.restclient.ProfileFeignClient;
import com.tessi.cxm.pfl.shared.service.restclient.SettingFeignClient;
import com.tessi.cxm.pfl.shared.utils.PrivilegeValidationUtil;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
class StatisticExportingServiceTest {
  @Mock FlowTraceabilityReportRepository flowTraceabilityReportRepository;
  @Mock SettingFeignClient settingFeignClient;
  @Mock ProfileFeignClient profileFeignClient;
  @Mock CSVExportingProperties csvExportingProperties;
  @Mock GlobalProductionDataExporter productionDataExporter;
  StatisticExportingService statisticExportingService;

  @BeforeEach
  void setUp() {
    this.statisticExportingService =
        new StatisticExportingService(
            flowTraceabilityReportRepository,
            settingFeignClient,
            profileFeignClient,
            csvExportingProperties);
  }

  @Test
  @Order(1)
  void testGenerateAndExportOnGlobal() throws ParseException, IOException {

    ConstantProperties.RequestFillerMock requestFillerMock = getRequestFillerMock();
    requestFillerMock.requestFilter.setExportingType(ExportType.GLOBAL.getValue());

    // Mock the static methods of PrivilegeValidationUtil, AuthenticationUtils and
    // ProductionDataExporters
    try (MockedStatic<PrivilegeValidationUtil> privilegeUtilMockedStatic =
            mockStatic(PrivilegeValidationUtil.class);
        var productionExport = mockStatic(ProductionDataExporters.class);
        MockedStatic<AuthenticationUtils> authUtils = mockStatic(AuthenticationUtils.class)) {
      authUtils.when(AuthenticationUtils::getAuthToken).thenReturn("token");
      productionExport
          .when(() -> ProductionDataExporters.getByKey(anyString()))
          .thenReturn(this.productionDataExporter);
      privilegeUtilMockedStatic
          .when(
              () ->
                  PrivilegeValidationUtil.getUserPrivilegeRelatedOwnerDetails(
                      anyString(), anyString(), anyBoolean(), anyBoolean()))
          .thenReturn(requestFillerMock.userDetails);

      // Mock the instance methods of the class under test
      when(this.profileFeignClient.getAllClientFillers(anyString()))
          .thenReturn(SHARED_CLIENT_FILLERS_DTOS);
      when(this.settingFeignClient.getCriteriaDistributions(any(), any()))
          .thenReturn(requestFillerMock.criteriaDistributionsResponse);
      when(this.csvExportingProperties.getDirectory())
          .thenReturn(System.getProperty("java.io.tmpdir"));

      // Invoke the method under test
      Path result =
          this.statisticExportingService.generateAndExport(requestFillerMock.requestFilter);

      // Verify the expected behavior and result
      verify(productionDataExporter).export(any(Context.class), any(BufferedWriter.class));
      Path expectedCsvPath =
          Path.of(System.getProperty("java.io.tmpdir"))
              .resolve(getCSVFilename(requestFillerMock.requestFilter));
      assertEquals(expectedCsvPath.toString(), result.toString());
      log.info("Csv Global Path {}", result);
    }
  }

  @Test
  @Order(2)
  void testFailGenerateAndExportOnGlobalWhenUserHasNonPrivilege()
      throws ParseException, IOException {

    ConstantProperties.RequestFillerMock requestFillerMock = getRequestFillerMock();
    requestFillerMock.requestFilter.setExportingType(ExportType.GLOBAL.getValue());

    // Mock the static methods of PrivilegeValidationUtil, AuthenticationUtils and
    // ProductionDataExporters
    try (MockedStatic<PrivilegeValidationUtil> privilegeUtilMockedStatic =
            mockStatic(PrivilegeValidationUtil.class);
        var productionExport = mockStatic(ProductionDataExporters.class);
        MockedStatic<AuthenticationUtils> authUtils = mockStatic(AuthenticationUtils.class)) {
      authUtils.when(AuthenticationUtils::getAuthToken).thenReturn("token");
      productionExport
          .when(() -> ProductionDataExporters.getByKey(anyString()))
          .thenReturn(this.productionDataExporter);
      privilegeUtilMockedStatic
          .when(
              () ->
                  PrivilegeValidationUtil.getUserPrivilegeRelatedOwnerDetails(
                      anyString(), anyString(), anyBoolean(), anyBoolean()))
          .thenThrow(new UserAccessDeniedExceptionHandler());
      // Invoke the method under test
      var result =
          assertThrows(
              UserAccessDeniedExceptionHandler.class,
              () ->
                  this.statisticExportingService.generateAndExport(
                      requestFillerMock.requestFilter));
      log.info("{}", result.getMessage());
    }
  }

  @Test
  @Order(3)
  void testGenerateAndExportOnSpecific() throws ParseException, IOException {

    // Create a mock request filter
    ConstantProperties.RequestFillerMock requestFillerMock = getRequestFillerMock();
    requestFillerMock.requestFilter.setExportingType(ExportType.SPECIFIC.getValue());
    // Mock the static methods of PrivilegeValidationUtil, AuthenticationUtils and
    // ProductionDataExporters
    try (MockedStatic<PrivilegeValidationUtil> privilegeUtilMockedStatic =
            mockStatic(PrivilegeValidationUtil.class);
        var productionExport = mockStatic(ProductionDataExporters.class);
        MockedStatic<AuthenticationUtils> authUtils = mockStatic(AuthenticationUtils.class)) {
      authUtils.when(AuthenticationUtils::getAuthToken).thenReturn("token");
      productionExport
          .when(() -> ProductionDataExporters.getByKey(anyString()))
          .thenReturn(this.productionDataExporter);
      privilegeUtilMockedStatic
          .when(
              () ->
                  PrivilegeValidationUtil.getUserPrivilegeRelatedOwnerDetails(
                      anyString(), anyString(), anyBoolean(), anyBoolean()))
          .thenReturn(requestFillerMock.userDetails);

      // Mock the instance methods of the class under test
      when(this.profileFeignClient.getAllClientFillers(anyString()))
          .thenReturn(SHARED_CLIENT_FILLERS_DTOS);
      when(this.settingFeignClient.getCriteriaDistributions(any(), any()))
          .thenReturn(requestFillerMock.criteriaDistributionsResponse);
      when(this.csvExportingProperties.getDirectory())
          .thenReturn(System.getProperty("java.io.tmpdir"));

      // Invoke the method under test
      Path result =
          this.statisticExportingService.generateAndExport(requestFillerMock.requestFilter);

      // Verify the expected behavior and result
      verify(productionDataExporter).export(any(Context.class), any(BufferedWriter.class));
      Path expectedCsvPath =
          Path.of(System.getProperty("java.io.tmpdir"))
              .resolve(getCSVFilename(requestFillerMock.requestFilter));
      assertEquals(expectedCsvPath.toString(), result.toString());
      log.info("Csv Specific Path {}", result);
    }
  }

  @Test
  @Order(3)
  void testGenerateAndExportOnSpecificFailWhenUserHasNonPrivilege()
      throws ParseException, IOException {

    // Create a mock request filter
    ConstantProperties.RequestFillerMock requestFillerMock = getRequestFillerMock();
    requestFillerMock.requestFilter.setExportingType(ExportType.SPECIFIC.getValue());
    // Mock the static methods of PrivilegeValidationUtil, AuthenticationUtils and
    // ProductionDataExporters
    try (MockedStatic<PrivilegeValidationUtil> privilegeUtilMockedStatic =
            mockStatic(PrivilegeValidationUtil.class);
        var productionExport = mockStatic(ProductionDataExporters.class);
        MockedStatic<AuthenticationUtils> authUtils = mockStatic(AuthenticationUtils.class)) {
      authUtils.when(AuthenticationUtils::getAuthToken).thenReturn("token");
      productionExport
          .when(() -> ProductionDataExporters.getByKey(anyString()))
          .thenReturn(this.productionDataExporter);
      privilegeUtilMockedStatic
          .when(
              () ->
                  PrivilegeValidationUtil.getUserPrivilegeRelatedOwnerDetails(
                      anyString(), anyString(), anyBoolean(), anyBoolean()))
          .thenThrow(new UserAccessDeniedExceptionHandler());
      // Invoke the method under test
      var result =
          assertThrows(
              UserAccessDeniedExceptionHandler.class,
              () ->
                  this.statisticExportingService.generateAndExport(
                      requestFillerMock.requestFilter));
      log.info("{}", result.getMessage());
    }
  }

  @Test
  @Order(4)
  void testGenerateAndExportOnSpecificFailWhenUserChoiceMultipleChannel() throws ParseException {

    // Create a mock request filter
    ConstantProperties.RequestFillerMock requestFillerMock = getRequestFillerMock();
    requestFillerMock.requestFilter.setExportingType(ExportType.SPECIFIC.getValue());
    requestFillerMock.requestFilter.setChannels(List.of("Digital", "Postal"));
    // Mock the static methods of PrivilegeValidationUtil, AuthenticationUtils and
    // ProductionDataExporters
    try (MockedStatic<PrivilegeValidationUtil> privilegeUtilMockedStatic =
            mockStatic(PrivilegeValidationUtil.class);
        var productionExport = mockStatic(ProductionDataExporters.class);
        MockedStatic<AuthenticationUtils> authUtils = mockStatic(AuthenticationUtils.class)) {
      authUtils.when(AuthenticationUtils::getAuthToken).thenReturn("token");
      productionExport
          .when(() -> ProductionDataExporters.getByKey(anyString()))
          .thenReturn(this.productionDataExporter);
      privilegeUtilMockedStatic
          .when(
              () ->
                  PrivilegeValidationUtil.getUserPrivilegeRelatedOwnerDetails(
                      anyString(), anyString(), anyBoolean(), anyBoolean()))
          .thenReturn(requestFillerMock.userDetails);

      // Mock the instance methods of the class under test
      when(this.profileFeignClient.getAllClientFillers(anyString()))
          .thenReturn(SHARED_CLIENT_FILLERS_DTOS);
      when(this.settingFeignClient.getCriteriaDistributions(any(), any()))
          .thenReturn(requestFillerMock.criteriaDistributionsResponse);

      // Invoke the method under test
      var result =
          assertThrows(
              BadRequestException.class,
              () ->
                  this.statisticExportingService.generateAndExport(
                      requestFillerMock.requestFilter));
      log.info("{}", result.getMessage());
    }
  }

  @Test
  @Order(5)
  void testGenerateAndExportOnSpecificFailWhenChannelDisabled() throws ParseException {

    // Create a mock request filter
    ConstantProperties.RequestFillerMock requestFillerMock = getRequestFillerMock();
    requestFillerMock.requestFilter.setExportingType(ExportType.SPECIFIC.getValue());
    requestFillerMock.criteriaDistributionsResponse.getCriteria("Postal").setEnabled(false);
    // Mock the static methods of PrivilegeValidationUtil, AuthenticationUtils and
    // ProductionDataExporters
    try (MockedStatic<PrivilegeValidationUtil> privilegeUtilMockedStatic =
            mockStatic(PrivilegeValidationUtil.class);
        var productionExport = mockStatic(ProductionDataExporters.class);
        MockedStatic<AuthenticationUtils> authUtils = mockStatic(AuthenticationUtils.class)) {
      authUtils.when(AuthenticationUtils::getAuthToken).thenReturn("token");
      productionExport
          .when(() -> ProductionDataExporters.getByKey(anyString()))
          .thenReturn(this.productionDataExporter);
      privilegeUtilMockedStatic
          .when(
              () ->
                  PrivilegeValidationUtil.getUserPrivilegeRelatedOwnerDetails(
                      anyString(), anyString(), anyBoolean(), anyBoolean()))
          .thenReturn(requestFillerMock.userDetails);

      // Mock the instance methods of the class under test
      when(this.profileFeignClient.getAllClientFillers(anyString()))
          .thenReturn(SHARED_CLIENT_FILLERS_DTOS);
      when(this.settingFeignClient.getCriteriaDistributions(any(), any()))
          .thenReturn(requestFillerMock.criteriaDistributionsResponse);

      // Invoke the method under test
      var result =
          assertThrows(
              BadRequestException.class,
              () ->
                  this.statisticExportingService.generateAndExport(
                      requestFillerMock.requestFilter));
      log.info("{}", result.getMessage());
    }
  }

  private String getCSVFilename(StatisticExportingRequestFilter requestFilter) {
    final SimpleDateFormat dateFormat =
        new SimpleDateFormat(CSVExportingConstant.CSV_NAME_DATE_FORMAT);
    var prefixCsvFile =
        (ExportType.GLOBAL.getValue().equalsIgnoreCase(requestFilter.getExportingType()))
            ? CSVExportingConstant.GLOBAL_PREFIX
            : CSVExportingConstant.SPECIFIC_PREFIX.concat("_" + requestFilter.getChannels().get(0));
    return prefixCsvFile
        .concat("_")
        .concat(dateFormat.format(requestFilter.getRequestedAt()))
        .concat("." + CSVExportingConstant.EXTENSION);
  }
}
