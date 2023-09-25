package com.tessi.cxm.pfl.ms5.dto;

import com.tessi.cxm.pfl.ms5.dto.enumeration.PasswordActionType;
import com.tessi.cxm.pfl.ms5.validators.PasswordMatches;
import com.tessi.cxm.pfl.ms5.validators.ValidPassword;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@PasswordMatches
public class UserInfoRequestUpdatePasswordDto {

  String currentPassword;
  @ValidPassword
  String newPassword;
  @NotNull
  @Size(min = 1)
  String confirmPassword;
  PasswordActionType actionType = PasswordActionType.RESET;
}
