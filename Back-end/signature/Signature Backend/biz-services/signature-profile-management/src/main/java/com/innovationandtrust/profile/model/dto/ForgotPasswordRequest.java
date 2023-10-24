package com.innovationandtrust.profile.model.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ForgotPasswordRequest {
  @NotEmpty(message = "Email cannot be empty!")
  private String email;
}
