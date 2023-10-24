package com.innovationandtrust.utils.aping.model;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CertificateRequest implements Serializable {
  private String actor;
  private String authority;
  private String token;
  private int ttl;

  public CertificateRequest(CaCguResponse ca) {
    this.actor = ca.getActor();
    this.authority = ca.getAuthority();
    this.token = ca.getToken();
    this.ttl = 1200;
  }

  public CertificateRequest(String actor, String authority, String token) {
    this.actor = actor;
    this.authority = authority;
    this.token = token;
    this.ttl = 1200;
  }
}
