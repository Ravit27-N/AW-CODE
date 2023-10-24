package com.innovationandtrust.configuration.security;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import java.util.Collection;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

@Slf4j
public class KeycloakRealmRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

  @Override
  public Collection<GrantedAuthority> convert(Jwt jwt) {
    var roles = Stream.of("$.realm_access.roles", "$.resource_access.*.roles")
        .flatMap(
            claimPaths -> {
              Object claim;
              try {
                claim = JsonPath.read(jwt.getClaims(), claimPaths);
              } catch (PathNotFoundException e) {
                claim = null;
              }
              if (claim == null) {
                return Stream.empty();
              }
              if (claim instanceof String claimStr) {
                return Stream.of(claimStr.split(","));
              }
              if (claim instanceof String[] claimArr) {
                return Stream.of(claimArr);
              }
              if (Collection.class.isAssignableFrom(claim.getClass())) {
                return this.extractCollection(claim);
              }
              return Stream.empty();
            })
        .map(roleName -> "ROLE_" + String.valueOf(roleName).toUpperCase())
        .map(SimpleGrantedAuthority::new)
        .map(GrantedAuthority.class::cast)
        .toList();
    log.debug("Extracted roles from token: {}", roles);
    return roles;
  }

  private Stream<?> extractCollection(Object claim) {
    final var iter = ((Collection<?>) claim).iterator();
    if (!iter.hasNext()) {
      return Stream.empty();
    }
    final var firstItem = iter.next();
    if (firstItem instanceof String) {
      return ((Collection<?>) claim).stream();
    }
    if (Collection.class.isAssignableFrom(firstItem.getClass())) {
      return ((Collection<?>) claim)
          .stream().flatMap(colItem -> ((Collection<?>) colItem).stream()).map(String.class::cast);
    }
    return Stream.empty();
  }
}
