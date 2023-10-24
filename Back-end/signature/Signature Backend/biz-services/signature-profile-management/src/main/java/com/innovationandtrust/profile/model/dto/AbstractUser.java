package com.innovationandtrust.profile.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.innovationandtrust.share.constant.PasswordConstant;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public abstract class AbstractUser {
  protected Long id;

  @NotEmpty(message = "Firstname is required.")
  protected String firstName;

  @NotEmpty(message = "Lastname is required.")
  protected String lastName;

  @NotEmpty(message = "Email is required.")
  protected String email;

  @NotEmpty(message = "Telephone number is required.")
  protected String phone;

  @JsonProperty(access = Access.WRITE_ONLY)
  @Pattern(regexp = PasswordConstant.REG, message = PasswordConstant.WRONG_REG)
  @Size(
      min = PasswordConstant.MIN_LENGTH,
      max = PasswordConstant.MAX_LENGTH,
      message = PasswordConstant.WRONG_LENGTH)
  protected String password;

  @JsonProperty(access = Access.READ_ONLY)
  protected UUID userEntityId;

  @JsonProperty(access = Access.READ_ONLY)
  protected Long createdBy;

  @NotEmpty(message = "Functional is required.")
  private String functional;

  @JsonProperty(access = Access.READ_ONLY)
  public String getFullName() {
    firstName = firstName != null ? firstName : "";
    lastName = lastName != null ? lastName : "";

    return (firstName + " " + lastName).trim();
  }
}
