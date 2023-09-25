package com.allweb.rms.controller;

import com.allweb.rms.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import java.util.Map;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {
  private static final String DEFAULT_SORTING_FIELD = "createdAt";
  private final DashboardService dashboardService;

  public DashboardController(DashboardService dashboardService) {
    this.dashboardService = dashboardService;
  }

  @Operation(
      operationId = "findTopCandidates",
      description = "find top candidate for dashboard",
      tags = {"Dashboard"},
      parameters = {
        @Parameter(in = ParameterIn.QUERY, name = "page", description = "page index"),
        @Parameter(
            in = ParameterIn.QUERY,
            name = "pageSize",
            description = "page size or limit record")
      })
  @GetMapping("/candidate/top")
  public ResponseEntity<Map<String, Object>> findTopCandidates(
      @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int pageSize) {
    PageRequest pageRequest =
        PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.fromString("desc"), "gpa"));
    return new ResponseEntity<>(dashboardService.findTopCandidates(pageRequest), HttpStatus.OK);
  }

  @Operation(
      operationId = "countGenderAndStatusCandidate",
      description = "count gender and status candidate for dashboard",
      tags = {"Dashboard"})
  @GetMapping("/candidate/count")
  public ResponseEntity<Map<String, Object>> countGenderAndStatusCandidate() {
    return new ResponseEntity<>(dashboardService.countGenderAndStatusCandidate(), HttpStatus.OK);
  }

  @Operation(
      operationId = "numbersOfData",
      description = "count data for dashboard",
      tags = {"Dashboard"})
  @GetMapping("/count")
  public ResponseEntity<Map<String, Object>> numbersOfData() {
    return new ResponseEntity<>(dashboardService.numbersOfData(), HttpStatus.OK);
  }

  @Operation(
      operationId = "reportInterviewsOnGraphByYear",
      description = "report interview by year for dashboard",
      tags = {"Dashboard"},
      parameters = {@Parameter(in = ParameterIn.QUERY, name = "year")})
  @GetMapping("/interview/graph")
  public ResponseEntity<Map<String, Object>> reportInterviewsOnGraphByYear(
      @RequestParam(required = false) String year) {
    return new ResponseEntity<>(
        dashboardService.reportInterviewsOnGraphByYear(year), HttpStatus.OK);
  }
}
