package com.innovationandtrust.profile.model.dto;

import com.innovationandtrust.share.constant.PasswordConstant;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewPasswordRequest {
  @NotEmpty(message = "Reset token cannot be empty!")
  private String resetToken;

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
}
