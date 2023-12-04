package com.innovationandtrust.process.controller;

import com.innovationandtrust.process.service.ApprovalProcessingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/approval")
public class ExternalApprovingProcessController {

  private final ApprovalProcessingService approvalProcessingService;

  public ExternalApprovingProcessController(ApprovalProcessingService approvalProcessingService) {
    this.approvalProcessingService = approvalProcessingService;
  }

  @PostMapping("/approve/{companyUuid}")
  public ResponseEntity<Void> approve(
      @PathVariable("companyUuid") String companyUuid, @RequestParam("token") String token) {
    this.approvalProcessingService.approveExternal(companyUuid, token);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PostMapping("/read/{companyUuid}")
  public ResponseEntity<Void> read(
      @PathVariable("companyUuid") String companyUuid, @RequestParam("token") String token) {
    this.approvalProcessingService.readExternal(companyUuid, token);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
