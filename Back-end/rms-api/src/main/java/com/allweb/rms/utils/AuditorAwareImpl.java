package com.allweb.rms.utils;

import com.allweb.rms.security.utils.AuthenticationUtils;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;

public class AuditorAwareImpl implements AuditorAware<String> {

  @Autowired private AuthenticationUtils authenticationUtils;

  @Override
  public Optional<String> getCurrentAuditor() {
    // use Spring Security to retrieve the currently logged-in user(s)
    try {
      return Optional.of(authenticationUtils.getAuthenticatedUser().getUserId());
    } catch (RuntimeException e) {
      // Fix me:
      // Spring batch's task is running inside scheduler background thread and Spring
      // security context is null.
      // So We may create a default user for this.scheduler and other system process.
      return Optional.of("SYSTEM-USER");
    }
  }
}
