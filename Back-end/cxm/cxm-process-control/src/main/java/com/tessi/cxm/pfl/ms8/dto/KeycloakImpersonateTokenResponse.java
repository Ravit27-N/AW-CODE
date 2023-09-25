package com.tessi.cxm.pfl.ms8.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class KeycloakImpersonateTokenResponse {

  @JsonProperty("access_token")
  private String accessToken;

  @JsonProperty("refresh_token")
  private String refreshToken;

  @JsonProperty("expires_in")
  private long expiresIn;

  @JsonProperty("refresh_expires_in")
  private long refreshExpiresIn;

  @JsonProperty("token_type")
  private String tokenType;

  @JsonProperty("session_state")
  private String sessionState;

  private String scope;

}
