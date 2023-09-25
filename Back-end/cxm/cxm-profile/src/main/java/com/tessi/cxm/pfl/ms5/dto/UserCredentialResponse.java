package com.tessi.cxm.pfl.ms5.dto;

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
public class UserCredentialResponse {
  private boolean usernameValid;
  private boolean passwordValid;
}
