package com.tessi.cxm.pfl.ms8.controller;

import com.tessi.cxm.pfl.ms8.dto.TokenRequest;
import com.tessi.cxm.pfl.ms8.dto.TokenResponse;
import com.tessi.cxm.pfl.ms8.service.ImpersonateTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/public")
public class PublicController {

  private final ImpersonateTokenService impersonateTokenService;

  @PostMapping(value = "/iv/token")
  public ResponseEntity<TokenResponse> validateDepositLink(
      @RequestBody() TokenRequest request) {
    return ResponseEntity.ok(this.impersonateTokenService.validateAndGenerateToken(request));
  }
}
