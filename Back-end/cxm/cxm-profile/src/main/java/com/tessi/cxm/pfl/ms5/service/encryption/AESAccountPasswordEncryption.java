package com.tessi.cxm.pfl.ms5.service.encryption;

import com.tessi.cxm.pfl.ms5.entity.UserHub;
import com.tessi.cxm.pfl.ms5.repository.UserHubRepository;
import com.tessi.cxm.pfl.shared.exception.AccountEncryptionException;
import com.tessi.cxm.pfl.shared.utils.AESHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AESAccountPasswordEncryption extends UserAccountEncryption {

  private static final String ENCRYPT_FAILED = "Fail to encrypt password";
  private final AESHelper aesHelper;

  public AESAccountPasswordEncryption(
      UserHubRepository userHubRepository, AESHelper aesHelper) {
    super(userHubRepository);
    this.aesHelper = aesHelper;
  }

  @Override
  protected boolean shouldEncryption(UserHub userHub) {
    try {
      this.aesHelper.decrypt(userHub.getPassword());
      return false;
    } catch (Exception e) {
      return true;
    }
  }

  @Override
  protected String encrypt(String rawPassword) {
    try {
      return this.aesHelper.encrypt(rawPassword);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AccountEncryptionException(ENCRYPT_FAILED);
    }
  }
}
