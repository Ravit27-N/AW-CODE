package com.tessi.cxm.pfl.ms5.dto;

import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UserCredentialRequest {

  @NotEmpty(message = "Username field is required")
  private String username;

  @NotEmpty(message = "Password field is required")
  private String password;
}
