package com.tessi.cxm.pfl.ms32.controller;

import com.tessi.cxm.pfl.ms32.constant.AnalyticsConstants;
import com.tessi.cxm.pfl.ms32.constant.DepositModeResponseDto;
import com.tessi.cxm.pfl.ms32.dto.FlowDocumentEvolutionReportData;
import com.tessi.cxm.pfl.ms32.dto.FlowDocumentReportDto;
import com.tessi.cxm.pfl.ms32.exception.DomainException;
import com.tessi.cxm.pfl.ms32.service.FlowTraceabilityReportService;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/flow-traceability/report")
@RequiredArgsConstructor
public class FlowTraceabilityReportController {

  private final FlowTraceabilityReportService flowTraceabilityReportService;
  @GetMapping("/deposit-modes")
  public ResponseEntity<List<DepositModeResponseDto>> getDepositModes(
      @RequestHeader HttpHeaders headers, @RequestParam("requestedAt") @DateTimeFormat(pattern = AnalyticsConstants.DATE_FORMAT_ISO) Date requestedAt) {

    List<DepositModeResponseDto> deposit = null;
    try {

      deposit =
          this.flowTraceabilityReportService.getDepositModes(
              headers.getFirst(HttpHeaders.AUTHORIZATION),
              requestedAt);

    } catch (DomainException dex) {
      if (dex.getCode() == 204) {
        return ResponseEntity.noContent().build();
      }
    }

    return ResponseEntity.ok(deposit);
  }

  @GetMapping("/flow-documents")
  public ResponseEntity<FlowDocumentReportDto> getFlowDocumentReports(
      @RequestHeader HttpHeaders headers, @RequestParam("requestedAt") @DateTimeFormat(pattern = AnalyticsConstants.DATE_FORMAT_ISO) Date requestedAt) {

    FlowDocumentReportDto flowDoucumentReport = null;
    try {

      flowDoucumentReport =
          this.flowTraceabilityReportService.getFlowDocumentReport(
              headers.getFirst(HttpHeaders.AUTHORIZATION), requestedAt);

    } catch (DomainException dex) {
      if (dex.getCode() == 204) {
        return ResponseEntity.noContent().build();
      }
    }

    return ResponseEntity.ok(flowDoucumentReport);
  }

  @GetMapping("/flow-channel")
  public ResponseEntity<List<DepositModeResponseDto>> getDepositChannel(
      @RequestHeader HttpHeaders headers, @RequestParam("requestedAt") @DateTimeFormat(pattern = AnalyticsConstants.DATE_FORMAT_ISO) Date requestedAt) {

    List<DepositModeResponseDto> depositChannel = null;
    try {
      depositChannel =
          this.flowTraceabilityReportService.getByGroupSubChannel(
              headers.getFirst(HttpHeaders.AUTHORIZATION), requestedAt);
    } catch (DomainException dex) {
      if (dex.getCode() == 204) {
        return ResponseEntity.noContent().build();
      }
    }

    return ResponseEntity.ok(depositChannel);
  }

  @GetMapping("/evolution")
  public ResponseEntity<List<FlowDocumentEvolutionReportData>> getEvolutionReport(
      @RequestHeader HttpHeaders headers, @RequestParam("requestedAt") @DateTimeFormat(pattern = AnalyticsConstants.DATE_FORMAT_ISO) Date requestedAt) {

    List<FlowDocumentEvolutionReportData> flowEvolution = null;

    try {
      flowEvolution = this.flowTraceabilityReportService
          .getFlowDocumentEvolutionReport(headers.getFirst(HttpHeaders.AUTHORIZATION), requestedAt);
    } catch (DomainException dex) {
      if (dex.getCode() == 204) {
        return ResponseEntity.noContent().build();
      }
    }

    return ResponseEntity.ok(flowEvolution);
  }
}
