package com.innovationandtrust.utils.encryption;

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

import com.innovationandtrust.utils.encryption.exception.EncryptionException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

public class ImpersonateToken {
  private SecretKey cacheKey;
  private IvParameterSpec cacheIvParamSpec;

  private final String mirrorSecretKey;

  public ImpersonateToken(String mirrorSecretKey) {
    this.mirrorSecretKey = mirrorSecretKey;
  }

  public String encrypt(String value) {
    try {
      var secretKey = this.getSecretKey();
      var ivParameterSpec = this.getIvParamSpec();

      byte[] encryptedData =
          AESUtils.encrypt(value, AESUtils.CBC_ALGORITHM, secretKey, ivParameterSpec);

      return Base64.getUrlEncoder().withoutPadding().encodeToString(encryptedData);
    } catch (NoSuchAlgorithmException
        | IllegalBlockSizeException
        | InvalidAlgorithmParameterException
        | NoSuchPaddingException
        | BadPaddingException
        | InvalidKeySpecException
        | InvalidKeyException encryptionException) {
      throw new EncryptionException(encryptionException);
    }
  }

  public String decryptToken(String encryptedTokenString) {
    try {
      var secretKey = this.getSecretKey();
      var iv = this.getIvParamSpec();
      var base6dDecoded = Base64.getUrlDecoder().decode(encryptedTokenString.getBytes());

      byte[] decryptedData = AESUtils.decrypt(AESUtils.CBC_ALGORITHM, base6dDecoded, secretKey, iv);

      return new String(decryptedData);
    } catch (NoSuchAlgorithmException
        | IllegalBlockSizeException
        | InvalidAlgorithmParameterException
        | NoSuchPaddingException
        | BadPaddingException
        | InvalidKeySpecException
        | InvalidKeyException decryptionException) {
      throw new EncryptionException(decryptionException);
    }
  }

  private SecretKey getSecretKey() throws NoSuchAlgorithmException, InvalidKeySpecException {

    if (cacheKey == null) {
      Assert.hasText(this.mirrorSecretKey, "secretKey must not be null or empty");
      final String passwordEncryptionSalt = StringUtils.reverse(this.mirrorSecretKey);
      cacheKey = AESUtils.generateKeyFromPassword(this.mirrorSecretKey, passwordEncryptionSalt);
    }
    return cacheKey;
  }

  private IvParameterSpec getIvParamSpec() throws NoSuchAlgorithmException {
    if (cacheIvParamSpec == null) {
      Assert.hasText(this.mirrorSecretKey, "secretKey must not be null or empty");
      cacheIvParamSpec = AESUtils.generateIvSpec(this.mirrorSecretKey);
    }
    return cacheIvParamSpec;
  }
}
