package com.innovationandtrust.corporate.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractUser {
  protected long id;
  protected String firstName;
  protected String lastName;
  protected String email;

  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  protected String password;
}
