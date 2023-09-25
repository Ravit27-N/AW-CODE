package com.allweb.rms.controller;

import com.allweb.rms.entity.dto.FirebaseTokenRequest;
import com.allweb.rms.security.AuthenticatedUser;
import com.allweb.rms.security.utils.AuthenticationUtils;
import com.allweb.rms.service.FirebaseTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/v1/user/firebase")
@Tag(name = "Firebase", description = "Manage Firebase notification for user.")
public class FirebaseTokenController {

  private final FirebaseTokenService userService;
  private final AuthenticationUtils authenticationUtils;

  public FirebaseTokenController(
      FirebaseTokenService userService, AuthenticationUtils authenticationUtils) {
    this.userService = userService;
    this.authenticationUtils = authenticationUtils;
  }

  @Operation(
      operationId = "assignUserFirebaseToken",
      description = "Assign or update Firebase token of current login user.",
      tags = {"Firebase"},
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              required = true,
              description = "Firebase Token.",
              content =
                  @Content(
                      mediaType = "application/json",
                      schema = @Schema(implementation = FirebaseTokenRequest.class))),
      responses = {
        @ApiResponse(responseCode = "200", description = "Success."),
        @ApiResponse(responseCode = "400", description = "Bad data being sent."),
        @ApiResponse(responseCode = "500", description = "Internal server error.")
      })
  @PutMapping(
      value = "/token",
      consumes = {MediaType.APPLICATION_JSON_VALUE},
      produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<String> name(
      @Valid @RequestBody FirebaseTokenRequest firebaseTokenRequest) {
    try {
      AuthenticatedUser authenticatedUser = authenticationUtils.getAuthenticatedUser();
      userService.saveUserToken(
          authenticatedUser.getUserId(),
          firebaseTokenRequest.getDeviceId(),
          firebaseTokenRequest.getToken());
    } catch (RuntimeException e) {
      log.debug(e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
    return ResponseEntity.ok().build();
  }
}
