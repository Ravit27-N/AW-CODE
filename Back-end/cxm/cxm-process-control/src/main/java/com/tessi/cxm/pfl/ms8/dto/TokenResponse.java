package com.tessi.cxm.pfl.ms8.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenResponse {

  private boolean valid;
  private boolean expired;

  private String flowUUID;

  private String flowToken;
  private String flowReferenceToken;
  private long expiresAt;
  private long refreshExpiresIn;

}
