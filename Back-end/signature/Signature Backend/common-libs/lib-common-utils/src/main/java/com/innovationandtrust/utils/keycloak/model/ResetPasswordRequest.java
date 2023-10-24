package com.innovationandtrust.utils.keycloak.model;

import com.innovationandtrust.share.constant.PasswordConstant;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResetPasswordRequest implements Serializable {
  @NotEmpty(message = "UUID cannot be empty!")
  private String id;

  @NotEmpty(message = "Current password cannot be empty!")
  private String currentPassword;

  @NotEmpty(message = "New password cannot be empty!")
  @Pattern(regexp = PasswordConstant.REG, message = PasswordConstant.WRONG_REG)
  @Size(
      min = PasswordConstant.MIN_LENGTH,
      max = PasswordConstant.MAX_LENGTH,
      message = PasswordConstant.WRONG_LENGTH)
  private String newPassword;

  @NotEmpty(message = "Confirm password cannot be empty!")
  @Pattern(regexp = PasswordConstant.REG, message = PasswordConstant.WRONG_REG)
  @Size(
      min = PasswordConstant.MIN_LENGTH,
      max = PasswordConstant.MAX_LENGTH,
      message = PasswordConstant.WRONG_LENGTH)
  private String confirmPassword;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ResetPasswordRequest that)) return false;
    return Objects.equals(getId(), that.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId());
  }
}
