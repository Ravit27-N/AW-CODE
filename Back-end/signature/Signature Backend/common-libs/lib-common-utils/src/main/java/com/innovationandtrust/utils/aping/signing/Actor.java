package com.innovationandtrust.utils.aping.signing;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.innovationandtrust.utils.aping.model.JsonEmpty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Actor {
  @NotNull
  private String name;

  // Because naming convention of API-NG not stable
  @JsonProperty("first-name")
  private String requestFirstName;

  @NotNull
  private String firstName;

  @JsonProperty("login")
  @NotNull
  private String login;

  @NotNull private  String email;

  // Mobile can be optional, complex with template notification-type
  @NotNull private  String mobile;

  @NotEmpty private List<@Valid String> roles;

  private int type = 0;

  private String country = "FR";

  @JsonProperty("adm-id")
  private String administrativeCode = "testing admin code";

  @JsonProperty("manifest-data")
  private JsonEmpty manifestData = new JsonEmpty();

  @JsonProperty(value = "user-data")
  private JsonEmpty userData = new JsonEmpty();

  public Actor(
      String name,
      String firstName,
      String login,
      String email,
      @NotEmpty List<@Valid String> roles,
      String mobile) {
    this.name = name;
    this.firstName = firstName;
    this.login = login;
    this.email = email;
    this.roles = roles;
    this.mobile = mobile;
    this.requestFirstName = this.firstName;
  }
}
