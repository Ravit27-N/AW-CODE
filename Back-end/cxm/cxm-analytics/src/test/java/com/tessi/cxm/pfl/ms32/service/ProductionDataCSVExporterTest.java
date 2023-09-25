package com.tessi.cxm.pfl.ms32.service;

import static com.tessi.cxm.pfl.ms32.utils.ConstantProperties.getClientFillerMock;
import static com.tessi.cxm.pfl.ms32.utils.ConstantProperties.getClientThreeFillerMock;
import static com.tessi.cxm.pfl.ms32.utils.ConstantProperties.getRequestFillerMock;
import static com.tessi.cxm.pfl.ms32.utils.ConstantProperties.getUserPrivilegeDetailOwnerMock;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.tessi.cxm.pfl.ms32.constant.AnalyticsConstants;
import com.tessi.cxm.pfl.ms32.constant.ExportType;
import com.tessi.cxm.pfl.ms32.constant.StatisticsExportingCSVHeader;
import com.tessi.cxm.pfl.ms32.dto.GlobalProductionRecord;
import com.tessi.cxm.pfl.ms32.dto.SpecificProductionRecord;
import com.tessi.cxm.pfl.ms32.entity.FlowTraceabilityReport;
import com.tessi.cxm.pfl.ms32.repository.FlowTraceabilityReportRepository;
import com.tessi.cxm.pfl.ms32.utils.ConstantProperties;
import com.tessi.cxm.pfl.ms32.utils.MockTuple;
import com.tessi.cxm.pfl.shared.core.Context;
import com.tessi.cxm.pfl.shared.model.SharedClientFillersDTO;
import com.tessi.cxm.pfl.shared.model.UserPrivilegeDetailsOwner.UserDetailsOwner;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.Tuple;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
class ProductionDataCSVExporterTest {
  @Mock FlowTraceabilityReportRepository flowTraceabilityReportRepository;

  @ParameterizedTest
  @MethodSource({"multipleParams"})
  @Order(1)
  void testExport(
      String exportingType, List<SharedClientFillersDTO> fillers, Integer expectedColumnCount)
      throws IOException, ParseException {

    // Create a mock request filter
    ConstantProperties.RequestFillerMock requestFillerMock = getRequestFillerMock();
    requestFillerMock.requestFilter.setExportingType(exportingType);
    Map<Long, UserDetailsOwner> userDetailsOwnerMap =
        getUserPrivilegeDetailOwnerMock().getUserDetailsOwners().stream()
            .collect(
                Collectors.toMap(UserDetailsOwner::getId, userDetailsOwner -> userDetailsOwner));
    requestFillerMock.requestFilter.setOwnerDetails(userDetailsOwnerMap);

    final var context = new Context();
    context.put(AnalyticsConstants.EXPORTING_REQUEST_FILTER_KEY, requestFillerMock.requestFilter);
    context.put(AnalyticsConstants.CLIENT_FILLERS_KEY, fillers);
    context.put(AnalyticsConstants.CSV_DELIMITER_KEY, ";");
    context.put(AnalyticsConstants.PAGE_SIZE_KEY, 1000);
    context.put(
        AnalyticsConstants.TARGET_EXPORTING_TIMEZONE,
        requestFillerMock.requestFilter.getTimeZone());

    final ByteArrayOutputStream memoryStorage = new ByteArrayOutputStream();
    final Writer writer = new BufferedWriter(new OutputStreamWriter(memoryStorage));
    // Stub
    if (exportingType.equals("global")) {
      this.mockGlobalProductionData();
    } else {
      this.mockSpecificProductionData();
    }
    // Call
    final ProductionDataExporter exporter = this.getInstanceKey(exportingType);
    exporter.export(context, writer);
    // Verify
    final var csvContent = getCsvContent(memoryStorage);
    List<String> lines = csvContent.lines().collect(Collectors.toList());
    // colum count
    var columnLength = lines.get(0).split(";").length;

    Assertions.assertEquals(expectedColumnCount, columnLength);
    log.info("Result total column: {}", columnLength);
    log.info("Result: {}", csvContent);
  }

  @ParameterizedTest
  @MethodSource({"multipleParamsWithoutFiller"})
  @Order(2)
  void testExportWithoutFiller(String exportingType, Integer expectedColumnCount)
      throws IOException, ParseException {

    // Create a mock request filter
    ConstantProperties.RequestFillerMock requestFillerMock = getRequestFillerMock();
    requestFillerMock.requestFilter.setExportingType(exportingType);
    Map<Long, UserDetailsOwner> userDetailsOwnerMap =
        getUserPrivilegeDetailOwnerMock().getUserDetailsOwners().stream()
            .collect(
                Collectors.toMap(UserDetailsOwner::getId, userDetailsOwner -> userDetailsOwner));
    requestFillerMock.requestFilter.setOwnerDetails(userDetailsOwnerMap);

    final var context = new Context();
    context.put(AnalyticsConstants.EXPORTING_REQUEST_FILTER_KEY, requestFillerMock.requestFilter);
    context.put(AnalyticsConstants.CLIENT_FILLERS_KEY, new ArrayList<>());
    context.put(AnalyticsConstants.CSV_DELIMITER_KEY, ";");
    context.put(AnalyticsConstants.PAGE_SIZE_KEY, 1000);
    context.put(
        AnalyticsConstants.TARGET_EXPORTING_TIMEZONE,
        requestFillerMock.requestFilter.getTimeZone());

    final ByteArrayOutputStream memoryStorage = new ByteArrayOutputStream();
    final Writer writer = new BufferedWriter(new OutputStreamWriter(memoryStorage));
    // Stub
    if (exportingType.equals("global")) {
      this.mockGlobalProductionData();
    } else {
      this.mockSpecificProductionData();
    }
    // Call
    final ProductionDataExporter exporter = this.getInstanceKey(exportingType);
    exporter.export(context, writer);
    // Verify
    final var csvContent = getCsvContent(memoryStorage);
    List<String> lines = csvContent.lines().collect(Collectors.toList());
    // colum count
    var columnLength = lines.get(0).split(";").length;

    Assertions.assertNotEquals(columnLength, expectedColumnCount);
    log.info("Result: {}", csvContent);
  }

  @ParameterizedTest
  @MethodSource({"multipleParams"})
  @Order(3)
  void testExportWithoutOwnerDetails(
      String exportingType, List<SharedClientFillersDTO> fillers, Integer expectedColumnCount)
      throws IOException, ParseException {

    // Create a mock request filter
    ConstantProperties.RequestFillerMock requestFillerMock = getRequestFillerMock();
    requestFillerMock.requestFilter.setExportingType(exportingType);

    final var context = new Context();
    context.put(AnalyticsConstants.EXPORTING_REQUEST_FILTER_KEY, requestFillerMock.requestFilter);
    context.put(AnalyticsConstants.CLIENT_FILLERS_KEY, fillers);
    context.put(AnalyticsConstants.CSV_DELIMITER_KEY, ";");
    context.put(AnalyticsConstants.PAGE_SIZE_KEY, 1000);
    context.put(
        AnalyticsConstants.TARGET_EXPORTING_TIMEZONE,
        requestFillerMock.requestFilter.getTimeZone());

    final ByteArrayOutputStream memoryStorage = new ByteArrayOutputStream();
    final Writer writer = new BufferedWriter(new OutputStreamWriter(memoryStorage));
    // Stub
    if (exportingType.equals("global")) {
      this.mockGlobalProductionData();
    } else {
      this.mockSpecificProductionData();
    }
    // Call
    final ProductionDataExporter exporter = this.getInstanceKey(exportingType);
    exporter.export(context, writer);
    // Verify
    final var csvContent = getCsvContent(memoryStorage);
    List<String> lines = csvContent.lines().collect(Collectors.toList());
    // colum count
    var columnLength = lines.get(0).split(";").length;
    var dataLength = lines.get(1).split(";").length;

    Assertions.assertEquals(expectedColumnCount, columnLength);
    Assertions.assertNotEquals(columnLength, dataLength);
    log.info("Column length: {} not equal data column: {}", columnLength, dataLength);
    log.info("Result: {}", csvContent);
  }

  private ProductionDataExporter getInstanceKey(String exportingType) {
    return ExportType.GLOBAL.equalsIgnoreCase(exportingType)
        ? new GlobalProductionDataExporter(flowTraceabilityReportRepository)
        : new SpecificProductionDataExporter(flowTraceabilityReportRepository);
  }

  private String getCsvContent(ByteArrayOutputStream source) throws IOException {
    BufferedReader reader =
        new BufferedReader(new InputStreamReader(new ByteArrayInputStream(source.toByteArray())));
    return IOUtils.toString(reader);
  }

  private void mockGlobalProductionData() throws ParseException {
    // Prepare
    GlobalProductionRecord record = new GlobalProductionRecord();
    record.addCsvRow(StatisticsExportingCSVHeader.ID_DOC.getOrder(), "12345");
    record.addCsvRow(StatisticsExportingCSVHeader.MODE_OF_DEPOSIT.getOrder(), "Online");
    record.addCsvRow(StatisticsExportingCSVHeader.CHANNEL.getOrder(), "Web");
    record.setOwnerId(47L);

    MockTuple mockTuple = new MockTuple();
    mockTuple.setTuple(0, 47L);
    mockTuple.setTuple(1, "eedd26f6-ede0-4f32-a");
    mockTuple.setTuple(2, "Portal");
    mockTuple.setTuple(3, "Digital");
    mockTuple.setTuple(4, "SMS");
    mockTuple.setTuple(6, new SimpleDateFormat("yyyy-MM-dd").parse("2023-04-20"));
    mockTuple.setTuple(7, new SimpleDateFormat("yyyy-MM-dd").parse("2023-04-20"));
    mockTuple.setTuple(8, "Sent");
    mockTuple.setTuple("filler1", "filler1");
    mockTuple.setTuple("filler2", "filler2");
    mockTuple.setTuple("filler3", "filler3");
    mockTuple.setTuple("filler4", "filler4");
    mockTuple.setTuple("filler5", "filler5");

    Page<Tuple> mockPagetuple = new PageImpl<>(Collections.singletonList(mockTuple));
    when(this.flowTraceabilityReportRepository.findAll(
            ArgumentMatchers.<Specification<FlowTraceabilityReport>>any(),
            ArgumentMatchers.<Class<FlowTraceabilityReport>>any(),
            ArgumentMatchers.<Class<Tuple>>any(),
            any(Pageable.class)))
        .thenReturn(mockPagetuple);
  }

  private void mockSpecificProductionData() throws ParseException {
    // Prepare
    SpecificProductionRecord record = new SpecificProductionRecord();
    record.addRow(StatisticsExportingCSVHeader.ID_DOC.getOrder(), "12345");
    record.addRow(StatisticsExportingCSVHeader.MODE_OF_DEPOSIT.getOrder(), "Online");
    record.addRow(StatisticsExportingCSVHeader.CHANNEL.getOrder(), "Web");
    record.setOwnerId(47L);

    MockTuple mockTuple = new MockTuple();
    mockTuple.setTuple(0, 47L);
    mockTuple.setTuple(1, "eedd26f6-ede0-4f32-a");
    mockTuple.setTuple(2, "Portal");
    mockTuple.setTuple(3, "Digital");
    mockTuple.setTuple(4, "SMS");
    mockTuple.setTuple(6, "032112333");
    mockTuple.setTuple(8, "Sent");
    mockTuple.setTuple(9, new SimpleDateFormat("yyyy-MM-dd").parse("2023-04-20"));
    mockTuple.setTuple(10, new SimpleDateFormat("yyyy-MM-dd").parse("2023-04-20"));
    mockTuple.setTuple(11, "Completed");
    mockTuple.setTuple("filler1", "filler1");
    mockTuple.setTuple("filler2", "filler2");
    mockTuple.setTuple("filler3", "filler3");
    mockTuple.setTuple("filler4", "filler4");
    mockTuple.setTuple("filler5", "filler5");

    Page<Tuple> mockPagetuple = new PageImpl<>(Collections.singletonList(mockTuple));

    when(this.flowTraceabilityReportRepository.findAll(
            ArgumentMatchers.<Specification<FlowTraceabilityReport>>any(),
            ArgumentMatchers.<Class<FlowTraceabilityReport>>any(),
            ArgumentMatchers.<Class<Tuple>>any(),
            any(Pageable.class)))
        .thenReturn(mockPagetuple);
  }

  private static Stream<Arguments> multipleParams() {
    return Stream.of(
        Arguments.arguments("global", getClientThreeFillerMock(), 13),
        Arguments.arguments("global", getClientFillerMock(), 15),
        Arguments.arguments("specific", getClientThreeFillerMock(), 17),
        Arguments.arguments("specific", getClientFillerMock(), 19));
  }

  private static Stream<Arguments> multipleParamsWithoutFiller() {
    return Stream.of(
        Arguments.arguments("global", 15),
        Arguments.arguments("global", 15),
        Arguments.arguments("specific", 19),
        Arguments.arguments("specific", 19));
  }
}
