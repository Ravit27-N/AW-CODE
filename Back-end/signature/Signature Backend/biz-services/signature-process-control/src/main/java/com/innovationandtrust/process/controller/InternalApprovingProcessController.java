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
@RequestMapping("/v1/approval")
public class InternalApprovingProcessController {

  private final ApprovalProcessingService approvalProcessingService;

  public InternalApprovingProcessController(ApprovalProcessingService approvalProcessingService) {
    this.approvalProcessingService = approvalProcessingService;
  }

  @PostMapping("/{flowId}/approve")
  public ResponseEntity<Void> approve(
      @PathVariable("flowId") String flowId, @RequestParam("uuid") String uuid) {
    this.approvalProcessingService.approve(flowId, uuid);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PostMapping("/{flowId}/read")
  public ResponseEntity<Void> read(
      @PathVariable("flowId") String flowId, @RequestParam("uuid") String uuid) {
    this.approvalProcessingService.read(flowId, uuid);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
