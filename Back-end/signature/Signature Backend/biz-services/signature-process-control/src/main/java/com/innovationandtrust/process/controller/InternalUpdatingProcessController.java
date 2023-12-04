package com.innovationandtrust.process.controller;

import com.innovationandtrust.process.service.UpdatingProcessingService;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/project")
public class InternalUpdatingProcessController {

  private final UpdatingProcessingService updatingProcessingService;

  public InternalUpdatingProcessController(UpdatingProcessingService updatingProcessingService) {
    this.updatingProcessingService = updatingProcessingService;
  }

  @PostMapping("/refuse/{flowId}")
  public ResponseEntity<Void> refuse(
      @PathVariable("flowId") String flowId,
      @RequestParam("uuid") String uuid,
      @RequestParam("comment") String comment) {
    this.updatingProcessingService.refuse(flowId, uuid, comment);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Hidden
  @PutMapping("/cancel/{flowId}")
  public ResponseEntity<Void> cancel(@PathVariable("flowId") String flowId) {
    this.updatingProcessingService.cancel(flowId);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
