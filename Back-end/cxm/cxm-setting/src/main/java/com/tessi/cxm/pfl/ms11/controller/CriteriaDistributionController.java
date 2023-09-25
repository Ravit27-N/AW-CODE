package com.tessi.cxm.pfl.ms11.controller;

import com.tessi.cxm.pfl.ms11.service.CriteriaDistributionService;
import com.tessi.cxm.pfl.shared.model.setting.criteria.CriteriaDistributionRequest;
import com.tessi.cxm.pfl.shared.model.setting.criteria.CriteriaDistributionsResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/setting/criteria-distribution")
@Tag(
    name = "Criteria distribution",
    description = "The API endpoints to manage criteria distribution.")
public class CriteriaDistributionController {
  private final CriteriaDistributionService criteriaDistributionService;

  @GetMapping
  public ResponseEntity<CriteriaDistributionsResponse> getCriteriaDistributions(
      @RequestParam(value = "clientName", required = false) Optional<String> clientName) {
    return ResponseEntity.ok(this.criteriaDistributionService.getCriteriaDistribution(clientName));
  }

  @PostMapping
  public ResponseEntity<CriteriaDistributionRequest> saveCriteriaDistributions(
      @RequestBody CriteriaDistributionRequest criteriaDistributionRequest) {
    return ResponseEntity.ok(
        this.criteriaDistributionService.updateCriteriaDistributions(criteriaDistributionRequest));
  }

  @GetMapping("/channel-is-active")
  public ResponseEntity<Boolean> getCriteriaDistributionsActivatedChannel(
      @RequestParam("clientName") String clientName, @RequestParam("channel") String channel) {
    return new ResponseEntity<>(
        this.criteriaDistributionService.isActive(clientName, channel), HttpStatus.OK);
  }
}
