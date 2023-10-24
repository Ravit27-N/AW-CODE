package com.innovationandtrust.process.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties(prefix = "signature.process-control")
public class ProcessControlProperty {
  private String frontEndUrl;
  private String invitationContextPath;
  private String approvalContextPath;
  private String recipientContextPath;
  private String viewContextPath;
  private String signCompletedContextPath;
  private PhoneNumber phoneNumber;

  @Getter
  @Setter
  @AllArgsConstructor
  @NoArgsConstructor
  public static class PhoneNumber {
    private int missingLength = 3;
  }
}
