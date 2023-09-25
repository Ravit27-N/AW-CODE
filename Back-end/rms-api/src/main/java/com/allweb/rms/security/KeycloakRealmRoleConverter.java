package com.allweb.rms.security;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class KeycloakRealmRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

  @SuppressWarnings("unchecked")
  @Override
  public Collection<GrantedAuthority> convert(Jwt source) {
    final Map<String, Object> realmAccess =
        (Map<String, Object>) source.getClaims().get("realm_access");
    return ((List<String>) realmAccess.get("roles"))
        .stream()
            .map(roleName -> "ROLE_" + roleName) // prefix to map to a Spring Security "role"
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());
  }
}
