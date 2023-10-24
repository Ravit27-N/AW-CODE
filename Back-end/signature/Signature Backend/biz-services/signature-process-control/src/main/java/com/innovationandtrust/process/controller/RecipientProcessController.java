package com.innovationandtrust.process.controller;

import com.innovationandtrust.process.service.RecipientProcessingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/recipients")
@RequiredArgsConstructor
public class RecipientProcessController {

  private final RecipientProcessingService recipientProcessingService;

  @PostMapping("/receive/{companyUuid}")
  public ResponseEntity<Void> receive(
      @PathVariable("companyUuid") String companyUuid, @RequestParam("token") String token) {
    this.recipientProcessingService.recipientExternal(companyUuid, token);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
