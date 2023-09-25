package com.tessi.cxm.pfl.ms5.dto;

import com.tessi.cxm.pfl.ms5.constant.UserRecordMessageConstant;
import com.tessi.cxm.pfl.shared.validation.ValidEmail;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.util.StringUtils;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserRecord {

  @NotBlank(message = UserRecordMessageConstant.CLIENT)
  private String client;

  @NotBlank(message = UserRecordMessageConstant.FIRST_NAME)
  private String firstName;

  @NotBlank(message = UserRecordMessageConstant.LAST_NAME)
  private String lastName;

  @ValidEmail
  private String email;

  @NotBlank(message = UserRecordMessageConstant.DIVISION)
  private String division;

  @NotBlank(message = UserRecordMessageConstant.SERVICE)
  private String service;

  @NotEmpty(message = UserRecordMessageConstant.PROFILES)
  private List<String> profiles = new ArrayList<>();

  public String getLastName() {
    return StringUtils.hasText(lastName) ? lastName.toUpperCase() : lastName;
  }

  public String getFirstName() {
    return com.tessi.cxm.pfl.ms5.util.StringUtils.titleCase(this.firstName);
  }
}
