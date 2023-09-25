package com.tessi.cxm.pfl.ms5.util;

import com.tessi.cxm.pfl.ms5.entity.UserHub;
import com.tessi.cxm.pfl.ms5.repository.UserHubRepository;
import com.tessi.cxm.pfl.shared.model.hubdigitalflow.AuthRequest;
import com.tessi.cxm.pfl.shared.service.keycloak.KeycloakService;
import com.tessi.cxm.pfl.shared.service.restclient.HubDigitalFlow;
import com.tessi.cxm.pfl.shared.utils.AESHelper;
import com.tessi.cxm.pfl.shared.utils.BearerAuthentication;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class HubUtil {

  private final KeycloakService keycloakService;
  private final UserHubRepository userHubRepository;
  private final HubDigitalFlow hubDigitalFlow;
  private final AESHelper aesHelper;

  @Value("${cxm.hub-account.username}")
  private String defaultUsername;
  @Value("${cxm.hub-account.password}")
  private String defaultPassword;

  public UserHub getDefaultUser() {
    return UserHub.builder().username(this.defaultUsername).password(this.defaultPassword).build();
  }

  /**
   * Get userHub token for request to hub.
   *
   * @param username - user created {@link String}.
   * @return - authorization token {@link String}.
   */
  public String getHubTokenByUser(String username) {
    AtomicBoolean isDefault = new AtomicBoolean(false);
    var userInfo = this.keycloakService.getUserInfo(username);
    var userHubInfo = this.userHubRepository
        .findByUserEntityUsername(userInfo.getUsername()).orElseGet(() -> {
          isDefault.getAndSet(true);
          return this.getDefaultUser();
        });

    String password = userHubInfo.getPassword();
    if (!isDefault.get()) {
      password = this.aesHelper.decrypt(userHubInfo.getPassword());
    }

    var hubToken =
        this.hubDigitalFlow.getAuthToken(
            new AuthRequest(userHubInfo.getUsername(), password));
    return BearerAuthentication.PREFIX_TOKEN.concat(hubToken.getToken());
  }
}
