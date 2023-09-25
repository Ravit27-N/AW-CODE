package com.tessi.cxm.pfl.ms5.controller;


import com.tessi.cxm.pfl.ms5.service.UserHubService;
import com.tessi.cxm.pfl.shared.model.SummarizeAccountEncryption;
import com.tessi.cxm.pfl.shared.model.hubdigitalflow.ClientAccountResponseDto;
import com.tessi.cxm.pfl.shared.model.hubdigitalflow.UserHubAccount;
import com.tessi.cxm.pfl.shared.model.hubdigitalflow.UserHubRequestDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/user-hub")
@Tag(name = "User Hub Management", description = "The API endpoint to manage the user-hub")
public class UserHubController {

  private final UserHubService userHubService;


  public UserHubController(UserHubService userHubService) {
    this.userHubService = userHubService;
  }

  @GetMapping("/user-account")
  public ResponseEntity<UserHubAccount> getUserHub() {
    return ResponseEntity.ok(this.userHubService.getUserHubByTechnicalRef());
  }

  @GetMapping("/user-account/{username}")
  public ResponseEntity<UserHubAccount> getUserHub(@PathVariable String username) {
    return ResponseEntity.ok(this.userHubService.getUserHub(username));
  }

  @PostMapping("/user-account")
  public ResponseEntity<UserHubRequestDto> registerUserHub(
      @RequestBody @Valid UserHubRequestDto userHubRequestDto) {
    return ResponseEntity.ok(this.userHubService.registerUserHub(userHubRequestDto));
  }

  @GetMapping("/client-account")
  public ResponseEntity<ClientAccountResponseDto> getCustomerAccount(
      @RequestParam(value = "client", defaultValue = "", required = false) String client) {
    return ResponseEntity.ok(this.userHubService.getCustomerAccount(client));
  }

  @PostMapping("/user-account/encryption-password")
  public ResponseEntity<SummarizeAccountEncryption> encryptPassword() {
    return ResponseEntity.ok(this.userHubService.encryptPassword());
  }
}
