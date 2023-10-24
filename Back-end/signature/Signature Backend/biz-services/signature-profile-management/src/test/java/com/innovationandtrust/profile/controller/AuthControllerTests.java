package com.innovationandtrust.profile.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innovationandtrust.profile.model.dto.ForgotPasswordRequest;
import com.innovationandtrust.profile.model.dto.NewPasswordRequest;
import com.innovationandtrust.profile.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration("classpath:application-test.yml")
class AuthControllerTests {
  @InjectMocks AuthController authController;
  @Mock AuthService authService;
  @Mock ForgotPasswordRequest request;
  @Mock NewPasswordRequest newPasswordRequest;
  private MockMvc mockMvc;

  @BeforeEach
  void setup() {
    JacksonTester.initFields(this, new ObjectMapper());
    mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
  }

  @DisplayName("Forgot password api test")
  @Test
  void forgot_password_api_test() throws Exception {
    authService.forgotPassword(request.getEmail());
    mockMvc
        .perform(
            put("/auth/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"herman@gmail.com\"}"))
        .andExpect(status().isOk());

    // then
    verify(authService, times(1)).forgotPassword(request.getEmail());
  }

  @DisplayName("Reset password api test")
  @Test
  void reset_password_api_test() throws Exception {
    authService.resetPassword(newPasswordRequest);
    mockMvc
        .perform(
            post("/auth/password/reset")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"resetToken\":\"123\",\"newPassword\":\"123\",\"confirmPassword\":\"123\"}"))
        .andExpect(status().isOk());

    //then
    verify(authService, times(1)).resetPassword(newPasswordRequest);
  }
}
