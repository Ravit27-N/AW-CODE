package com.innovationandtrust.profile.controller;

import com.innovationandtrust.profile.model.dto.ActivityRes;
import com.innovationandtrust.profile.model.dto.ForgotPasswordRequest;
import com.innovationandtrust.profile.model.dto.NewPasswordRequest;
import com.innovationandtrust.profile.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Slf4j
public class AuthController {
  private final AuthService authService;

  @PutMapping("/forgot-password")
  @Tag(name = "Forgot password", description = "User request to reset password")
  public ResponseEntity<Void> forgotPassword(
      @Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest) {
    this.authService.forgotPassword(forgotPasswordRequest.getEmail());
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PutMapping("/redirect/forgot-password/{token}")
  @Tag(
      name = "Validate forgot password link",
      description = "The link has expire time and only one time click")
  public ResponseEntity<ActivityRes> disableForgotPasswordLink(
      @PathVariable("token") String token) {
    return new ResponseEntity<>(this.authService.disableLink(token), HttpStatus.OK);
  }

  @PostMapping("/password/reset")
  @Tag(name = "Reset password", description = "To reset user new password")
  public ResponseEntity<Void> resetPassword(
      @Valid @RequestBody NewPasswordRequest newPasswordRequest) {
    this.authService.resetPassword(newPasswordRequest);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @GetMapping("/user/{token}")
  @Tag(
      name = "Validate user activate link ",
      description = "The link has expire time and only one time click")
  public ResponseEntity<ActivityRes> disableActivateLink(@PathVariable("token") String token) {
    return new ResponseEntity<>(this.authService.disableLink(token), HttpStatus.OK);
  }

  @PutMapping("/activate/{token}")
  @Tag(name = "Activate user", description = "To active user login")
  public ResponseEntity<ActivityRes> activateUser(@PathVariable("token") String token) {
    return new ResponseEntity<>(this.authService.activateUser(token), HttpStatus.OK);
  }

  @GetMapping("/change-mail/{token}")
  @Tag(name = "Validate user change mail link")
  public ResponseEntity<ActivityRes> disableChangeMailLink(@PathVariable("token") String token) {
    return new ResponseEntity<>(this.authService.disableLink(token), HttpStatus.OK);
  }

  @PutMapping("/activate/mail/{token}")
  @Tag(name = "Confirm mail changing", description = "To activate the new mail")
  public ResponseEntity<ActivityRes> confirmMailChanging(@PathVariable("token") String token) {
    return new ResponseEntity<>(this.authService.confirmChangeMail(token), HttpStatus.OK);
  }
}
