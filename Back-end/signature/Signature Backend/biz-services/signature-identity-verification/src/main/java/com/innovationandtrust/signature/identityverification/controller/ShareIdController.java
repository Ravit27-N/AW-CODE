package com.innovationandtrust.signature.identityverification.controller;

import com.innovationandtrust.signature.identityverification.model.dto.shareid.OnBoardingDemandDto;
import com.innovationandtrust.signature.identityverification.service.ShareIdService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Controller class for shareid. */
@RestController
@RequestMapping("/v1/shareid")
@RequiredArgsConstructor
public class ShareIdController {

  private final ShareIdService shareIdService;

  @PostMapping("/callback")
  @Tag(name = "Call back share-id", description = "To call back share-id.")
  public ResponseEntity<String> callback(@RequestBody OnBoardingDemandDto onBoardingDemandDto) {
    return ResponseEntity.ok(shareIdService.callback(onBoardingDemandDto));
  }
}
