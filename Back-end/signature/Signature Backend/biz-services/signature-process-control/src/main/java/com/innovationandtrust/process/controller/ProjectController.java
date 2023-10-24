package com.innovationandtrust.process.controller;

import com.innovationandtrust.process.service.RequestSignService;
import com.innovationandtrust.process.service.SendReminderService;
import com.innovationandtrust.share.model.project.Project;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Project processing controller. */
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/process-controls")
public class ProjectController {

  private final RequestSignService requestSignService;
  private final SendReminderService sendReminderService;

  /**
   * To initialize project for signing process.
   *
   * @param project refers to project object {@link Project}
   */
  @PostMapping("/project/send")
  public ResponseEntity<Void> requestSign(@Validated @RequestBody Project project) {
    this.requestSignService.requestSign(project);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  /**
   * internal method to extending project expiration date.
   *
   * @param flowId project unique flow id
   * @param expireDate new expiration date
   */
  @Hidden
  @PostMapping("/project/update/{flowId}")
  public ResponseEntity<Void> updateExpireDate(
      @PathVariable("flowId") String flowId, @RequestParam String expireDate) {
    this.requestSignService.updateExpireDate(flowId, expireDate);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PostMapping("/project/send-reminder/{flowId}")
  public ResponseEntity<Void> sendReminder(@PathVariable("flowId") String flowId) {
    this.sendReminderService.sendReminder(flowId);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @GetMapping("/manifest/{flowId}")
  public ResponseEntity<Resource> getManifest(@PathVariable("flowId") String flowId) {
    var response = this.requestSignService.downloadManifest(flowId);
    return new ResponseEntity<>(
        new ByteArrayResource(response.getResource()), response.getResourceHeader(), HttpStatus.OK);
  }

  @GetMapping("/is-finished/{flowId}")
  public ResponseEntity<Boolean> isFinished(@PathVariable("flowId") String flowId) {
    return new ResponseEntity<>(this.requestSignService.isFinished(flowId), HttpStatus.OK);
  }
}
