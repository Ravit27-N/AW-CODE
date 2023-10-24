package com.innovationandtrust.corporate.controller;

import com.innovationandtrust.corporate.service.DashboardService;
import com.innovationandtrust.share.constant.CommonParamsConstant;
import com.innovationandtrust.share.model.corporateprofile.DashboardDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/dashboard")
public class DashboardController {

  private final DashboardService dashboardService;

  @GetMapping
  @Tag(name = "View information on dashboard", description = "To view information on dashboard.")
  public ResponseEntity<DashboardDTO> findAll(
      @RequestParam(value = "companyId") Long companyId,
      @RequestParam(value = "businessUnitId", defaultValue = "") Long businessUnitId,
      @RequestParam(value = CommonParamsConstant.START_DATE) String startDate,
      @RequestParam(value = CommonParamsConstant.END_DATE) String endDate) {

    return new ResponseEntity<>(
        this.dashboardService.findAll(companyId, businessUnitId, startDate, endDate),
        HttpStatus.OK);
  }
}
