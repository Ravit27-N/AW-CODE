package com.tessi.cxm.pfl.ms5.dto;

import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserResetPasswordDto {

  @NotEmpty(message = "Token field is required")
  private String token;

  @NotEmpty(message = "Password field is required")
  private String password;
}
