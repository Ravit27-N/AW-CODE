package com.innovationandtrust.profile.controller;

import com.innovationandtrust.share.model.profile.LoginHistoryDto;
import com.innovationandtrust.profile.service.LoginHistoryService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/login-history")
@Slf4j
public class LoginHistoryController {
  private final LoginHistoryService loginHistoryService;

  @PostMapping
  @Tag(name = "Save login history", description = "To save user login history.")
  public ResponseEntity<LoginHistoryDto> saveLogin() {
    return new ResponseEntity<>(this.loginHistoryService.save(), HttpStatus.OK);
  }

  @GetMapping("/user/{id}")
  public ResponseEntity<List<LoginHistoryDto>> getLoginHistoryByUser(@PathVariable("id") Long id) {
    return new ResponseEntity<>(this.loginHistoryService.getLoginHistoryByUser(id), HttpStatus.OK);
  }

  @Hidden
  @PostMapping("/users")
  public ResponseEntity<List<LoginHistoryDto>> getLoginHistoriesByUsers(
      @RequestBody List<Long> userIds) {
    return new ResponseEntity<>(
        this.loginHistoryService.getLoginHistoriesByUsers(userIds), HttpStatus.OK);
  }
}
