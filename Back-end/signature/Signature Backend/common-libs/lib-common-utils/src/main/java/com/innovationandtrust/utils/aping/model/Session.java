package com.innovationandtrust.utils.aping.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class Session {
  @NotNull private final int ttl;

  @JsonProperty("manifest-data")
  private JsonEmpty manifestData = new JsonEmpty();

  @JsonProperty("user-data")
  private SessionUserData userData;

  public Session(int ttl) {
    this.ttl = ttl;
  }

  public Session(int ttl, SessionUserData userData) {
    this.ttl = ttl;
    this.userData = userData;
  }
}
