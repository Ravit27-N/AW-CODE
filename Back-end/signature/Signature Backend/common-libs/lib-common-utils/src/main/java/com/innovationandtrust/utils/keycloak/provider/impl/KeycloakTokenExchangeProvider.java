package com.innovationandtrust.utils.keycloak.provider.impl;

import com.innovationandtrust.utils.keycloak.config.KeycloakProperties;
import com.innovationandtrust.utils.keycloak.provider.IKeycloakTokenExchange;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
@Slf4j
@RequiredArgsConstructor
public class KeycloakTokenExchangeProvider implements IKeycloakTokenExchange {

  private final Keycloak keycloak;

  private final RestTemplate restTemplate;

  private final KeycloakProperties properties;

  @Override
  public String getToken(String uuid) {
    return this.exchangeToken(uuid, getToken());
  }

  @Override
  public String getToken() {
    return keycloak.tokenManager().getAccessToken().getToken();
  }

  private String exchangeToken(String uuid, String token) {
    var url =
        String.format(
            "%s/realms/%s/protocol/openid-connect/token",
            properties.getAuthServerUrl(), properties.getRealm());
    var headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
    MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
    requestBody.add("grant_type", "urn:ietf:params:oauth:grant-type:token-exchange");
    requestBody.add("client_id", properties.getResource());
    requestBody.add("client_secret", properties.getSecret());
    requestBody.add("subject_token", token);
    requestBody.add("requested_token_type", "urn:ietf:params:oauth:token-type:access_token");
    requestBody.add("requested_subject", uuid);
    var response =
        restTemplate.postForEntity(
            url, new HttpEntity<Object>(requestBody, headers), AccessTokenResponse.class);
    return Objects.requireNonNull(response.getBody()).getToken();
  }

  @Override
  public Object introspectToken(String token) {
    var url =
            String.format(
                    "%s/realms/%s/protocol/openid-connect/token/introspect",
                    properties.getAuthServerUrl(), properties.getRealm());
    var headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
    MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
    requestBody.add("client_id", properties.getResource());
    requestBody.add("client_secret", properties.getSecret());
    requestBody.add("token", token);
    return
            restTemplate.postForEntity(
                    url, new HttpEntity<Object>(requestBody, headers), Object.class);
  }
  @Override
  public String getTokenTechnicalUser(){
    String userId = this.properties.getTechnicalUserId();
    if (StringUtils.isNotBlank(userId) && validateUserId(userId)){
      log.info("Request new token from keycloak with technicalUserId [{}]", this.properties.getTechnicalUserId());
      return this.getToken(this.properties.getTechnicalUserId());
    }
    return this.getToken();
  }

  private boolean validateUserId(String uuid) {
    Pattern pattern =
            Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");
    try {
      return pattern.matcher(uuid).matches();
    } catch (Exception ignored) {
      return false;
    }
  }
}
