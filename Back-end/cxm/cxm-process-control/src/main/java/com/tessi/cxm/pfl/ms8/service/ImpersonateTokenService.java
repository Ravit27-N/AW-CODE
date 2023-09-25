package com.tessi.cxm.pfl.ms8.service;

import com.cxm.tessi.pfl.shared.flowtreatment.constant.FlowTreatmentConstants;
import com.tessi.cxm.pfl.ms8.config.ClientTokenExchangeProperties;
import com.tessi.cxm.pfl.ms8.dto.KeycloakImpersonateTokenResponse;
import com.tessi.cxm.pfl.ms8.dto.TokenRequest;
import com.tessi.cxm.pfl.ms8.dto.TokenResponse;
import com.tessi.cxm.pfl.shared.exception.EncryptionException;
import com.tessi.cxm.pfl.shared.service.mirrorlink.MirrorTokenService;
import com.tessi.cxm.pfl.shared.utils.AESUtils;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.keycloak.adapters.springboot.KeycloakSpringBootProperties;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@RequiredArgsConstructor
public class ImpersonateTokenService {

  public static final String PASSWORD_ENCRYPTION_SALT = StringUtils.reverse(
      FlowTreatmentConstants.MIRROR_TOKEN_SECRET);
  private static final String CLIENT_ID_KEY = "client_id";
  private static final String CLIENT_SECRET_KEY = "client_secret";
  private static final String GRANT_TYPE_KEY = "grant_type";
  private static final String KEYCLOAK_TOKEN_EXCHANGE_GRANT_TYPE = "urn:ietf:params:oauth:grant-type:token-exchange";
  private final MirrorTokenService tokenService;
  private final RestTemplate restTemplate;
  private final KeycloakSpringBootProperties keycloakProperties;
  private final ClientTokenExchangeProperties clientTokenExchangeProperties;
  private SecretKey cacheKey;
  private IvParameterSpec cacheIvParamSpec;

  public TokenResponse validateAndGenerateToken(TokenRequest request) {
    var response = TokenResponse.builder();
    var hash = request.getHash();

    try {
      var decryptedHash = this.decryptToken(hash);
      var payout = this.tokenService.verifyToken(decryptedHash);

      response.valid(true);
      response.flowUUID(payout.getContent());
      if (payout.isExpired()) {
        response.expired(true);
      } else {
        response.expired(false);
        // Generate keycloak token
        var generatedToken = this.impersonateUser(
            payout.getSub());

        if (generatedToken != null) {
          response.flowToken(generatedToken.getAccessToken());
          response.expiresAt(generatedToken.getExpiresIn());
          response.flowReferenceToken(generatedToken.getRefreshToken());
          response.refreshExpiresIn(generatedToken.getRefreshExpiresIn());
        } else {
          response.valid(false);
        }
      }
    } catch (Exception ex) {
      response.valid(false);
      log.error("Token is not valid.", ex);
    }

    return response.build();
  }

  private KeycloakImpersonateTokenResponse impersonateUser(String userid) {
    final var keycloakAuthenticationUrl = this.getKeycloakAuthenticationUrl();
    final var targetClientId = this.clientTokenExchangeProperties.getClientId();
    final var targetClientSecret = this.clientTokenExchangeProperties.getClientSecret();

    final var requestBody = new LinkedMultiValueMap<String, String>();
    requestBody.add(CLIENT_ID_KEY, targetClientId);
    requestBody.add(CLIENT_SECRET_KEY, targetClientSecret);
    requestBody.add(GRANT_TYPE_KEY, KEYCLOAK_TOKEN_EXCHANGE_GRANT_TYPE);
    requestBody.add("requested_subject", userid);

    var response = this.restTemplate.postForEntity(keycloakAuthenticationUrl, requestBody,
        KeycloakImpersonateTokenResponse.class);

    return response.getBody();
  }

  private String getKeycloakAuthenticationUrl() {
    return this.keycloakProperties.getAuthServerUrl() + "/realms/"
        + this.keycloakProperties.getRealm()
        + "/protocol/openid-connect/token";
  }

  private String decryptToken(String encryptedTokenString) {
    try {
      var secretKey = this.getSecretKey();
      var iv = this.getIvParamSpec();
      var base6dDecoded = Base64.getUrlDecoder().decode(encryptedTokenString.getBytes());

      byte[] decryptedData = AESUtils.decrypt(AESUtils.CBC_ALGORITHM, base6dDecoded,
          secretKey,
          iv);

      return new String(decryptedData);
    } catch (NoSuchAlgorithmException | IllegalBlockSizeException |
             InvalidAlgorithmParameterException | NoSuchPaddingException |
             BadPaddingException |
             InvalidKeySpecException | InvalidKeyException decryptionException) {
      throw new EncryptionException(decryptionException);
    }
  }

  private SecretKey getSecretKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
    if (cacheKey == null) {
      cacheKey = AESUtils.generateKeyFromPassword(FlowTreatmentConstants.MIRROR_TOKEN_SECRET,
          PASSWORD_ENCRYPTION_SALT);
    }
    return cacheKey;
  }

  private IvParameterSpec getIvParamSpec() throws NoSuchAlgorithmException {
    if (cacheIvParamSpec == null) {
      cacheIvParamSpec = AESUtils.generateIvSpec(FlowTreatmentConstants.MIRROR_TOKEN_SECRET);
    }
    return cacheIvParamSpec;
  }
}
