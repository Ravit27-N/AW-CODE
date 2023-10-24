package com.innovationandtrust.utils.gravitee.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Configuration {
  private String authorizationServerUrl;
  private String introspectionEndpoint;
  private Boolean useSystemProxy;
  private String introspectionEndpointMethod;
  private String scopeSeparator;
  private String userInfoEndpoint;
  private String userInfoEndpointMethod;
  private Boolean useClientAuthorizationHeader;
  private String clientAuthorizationHeaderName;
  private String clientAuthorizationHeaderScheme;
  private Boolean tokenIsSuppliedByQueryParam;
  private String tokenQueryParamName;
  private Boolean tokenIsSuppliedByHttpHeader;
  private Boolean tokenIsSuppliedByFormUrlEncoded;
  private String tokenFormUrlEncodedName;
  private String userClaim;
  private String clientId;
  private String clientSecret;
  private String tokenHeaderName;
}
