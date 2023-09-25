package com.tessi.cxm.pfl.ms5.service.encryption;

import com.tessi.cxm.pfl.ms5.entity.UserHub;
import com.tessi.cxm.pfl.ms5.repository.UserHubRepository;
import com.tessi.cxm.pfl.shared.core.Context;
import com.tessi.cxm.pfl.shared.service.encryption.SummarizeAccountEncryption;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@AllArgsConstructor
public abstract class UserAccountEncryption extends
    SummarizeAccountEncryption<UserHub> {

  private final UserHubRepository userHubRepository;

  protected abstract String encrypt(String rawPassword);

  @Override
  protected Page<UserHub> readByPageable(Context context, Pageable pageable) {
    return this.userHubRepository.findAllByEncryptedFalse(pageable);
  }

  @Override
  protected void encryptInternal(UserHub userHub) {
    String passwordEncrypted = encrypt(userHub.getPassword());
    userHub.setPassword(passwordEncrypted);
    userHub.setEncrypted(true);
    this.userHubRepository.save(userHub);
  }
}
