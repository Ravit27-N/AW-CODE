package com.innovationandtrust.process.controller;

import com.innovationandtrust.process.service.UpdatingProcessingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/project")
public class ExternalUpdateProcessController {

  private final UpdatingProcessingService updatingProcessingService;

  public ExternalUpdateProcessController(UpdatingProcessingService updatingProcessingService) {
    this.updatingProcessingService = updatingProcessingService;
  }

  @PostMapping("/refuse/{companyUuid}")
  public ResponseEntity<Void> refuse(
      @PathVariable("companyUuid") String companyUuid,
      @RequestParam("token") String token,
      @RequestParam("comment") String comment) {
    this.updatingProcessingService.refuseExternal(companyUuid, comment, token);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
