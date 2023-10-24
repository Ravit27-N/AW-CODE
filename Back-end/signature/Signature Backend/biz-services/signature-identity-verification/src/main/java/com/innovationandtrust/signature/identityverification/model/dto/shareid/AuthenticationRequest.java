package com.innovationandtrust.signature.identityverification.model.dto.shareid;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.innovationandtrust.configuration.payload.ShareIdProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Request class for authentication. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequest {
  @JsonProperty("business_identifier")
  private String businessIdentifier;

  @JsonProperty("business_secret")
  private String businessSecret;

  public AuthenticationRequest(ShareIdProperty property) {
    this.businessIdentifier = property.getBusinessIdentifier();
    this.businessSecret = property.getBusinessSecret();
  }
}
