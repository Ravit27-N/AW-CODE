package com.tessi.cxm.pfl.ms5.controller;

import com.tessi.cxm.pfl.ms5.constant.SwaggerConstants;
import com.tessi.cxm.pfl.ms5.dto.UserCredentialRequest;
import com.tessi.cxm.pfl.ms5.dto.UserCredentialResponse;
import com.tessi.cxm.pfl.ms5.dto.UserRequestResetPasswordDto;
import com.tessi.cxm.pfl.ms5.dto.UserResetPasswordDto;
import com.tessi.cxm.pfl.ms5.dto.request.AuthenticationAttemptsRequestDTO;
import com.tessi.cxm.pfl.ms5.dto.request.UserLoginAttemptDTO;
import com.tessi.cxm.pfl.ms5.dto.response.AuthenticationAttemptsDTO;
import com.tessi.cxm.pfl.ms5.service.AuthenticationAttemptsService;
import com.tessi.cxm.pfl.ms5.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public/users")
@RequiredArgsConstructor
@Log4j2
public class UserPasswordController {

  private final UserService userService;
  private final AuthenticationAttemptsService authAttemptsService;

  @PostMapping("/request/reset-password")
  public ResponseEntity<UserRequestResetPasswordDto> requestResetPassword(
      @RequestBody UserRequestResetPasswordDto userRequestResetPassword) {
    return ResponseEntity.ok(this.userService.requestForResetPassword(userRequestResetPassword));
  }

  @PostMapping("/reset-password")
  public ResponseEntity<UserResetPasswordDto> resetPassword(
      @RequestBody UserResetPasswordDto userResetPasswordDto) {
    return ResponseEntity.ok(this.userService.resetPassword(userResetPasswordDto));
  }

  @GetMapping("/is-expired/{token}")
  public ResponseEntity<Boolean> validateToken(@PathVariable("token") String token) {
    return new ResponseEntity<>(this.userService.isExpire(token), HttpStatus.OK);
  }

  @PostMapping("/validate-user-credential")
  public ResponseEntity<UserCredentialResponse> validateUserCredential(
      @RequestBody UserCredentialRequest userCredential) {
    return new ResponseEntity<>(
        this.userService.validateUserCredential(userCredential), HttpStatus.OK);
  }

  /**
   *
   * Handle login attempts and update authentication attempts in the database.
   * This API endpoint is responsible for handling login attempts and updating the authentication attempts in the database for the user.
   * @return ResponseEntity with a String message indicating the result of the login attempt.
   */


  @Operation(operationId = "login-attempts", summary = "", description = SwaggerConstants.VERIFICATION_OF_BLOCKED_ACCOUNTS_DESCRIPTION,
          requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = SwaggerConstants.VERIFICATION_OF_BLOCKED_ACCOUNTS_REQUEST_BODY_DESCRIPTION), responses = {
          @ApiResponse(responseCode = "200", description = SwaggerConstants.VERIFICATION_OF_BLOCKED_ACCOUNTS_RESPONSE_200_DESCRIPTION)
  })
  @PostMapping(value = "/login-attempts", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<AuthenticationAttemptsDTO> loginAttempts(@Validated @RequestBody AuthenticationAttemptsRequestDTO request) {
    log.info("UserPasswordController - Start calling POST Api for verification of blocked account for user {} ", request.getUserName());
    return ResponseEntity.ok(authAttemptsService.isBlocked(request));
  }

  @Operation(operationId = "login-attempts/add", summary = "", description = SwaggerConstants.SAVING_ACCOUNT_LOGIN_ATTEMPTS_DESCRIPTION,
          requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = SwaggerConstants.SAVING_ACCOUNT_LOGIN_ATTEMPTS_REQUEST_BODY_DESCRIPTION),
          responses = { @ApiResponse(responseCode = "201", description = SwaggerConstants.SAVING_ACCOUNT_LOGIN_ATTEMPTS_RESPONSE_201_DESCRIPTION)
  })
  @PostMapping(value = "/login-attempts/add", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<UserLoginAttemptDTO> addLoginAttempts(@Validated @RequestBody UserLoginAttemptDTO request) {
    log.info("UserPasswordController - Start calling POST Api for saving login attempts for user {} ", request.getUserName());
    return new ResponseEntity<>(this.authAttemptsService.addUserLoginAttempt(request), HttpStatus.CREATED);

  }

}
