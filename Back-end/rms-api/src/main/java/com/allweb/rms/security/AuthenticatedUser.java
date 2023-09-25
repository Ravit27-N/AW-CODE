package com.allweb.rms.security;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@RequiredArgsConstructor
public class AuthenticatedUser {
  @Getter private final String userId;

  @Getter private final String email;
  @Getter private final List<String> grantedAuthorities;
}
