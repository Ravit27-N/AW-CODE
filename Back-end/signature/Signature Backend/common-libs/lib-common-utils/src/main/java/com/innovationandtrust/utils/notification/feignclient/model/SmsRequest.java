package com.innovationandtrust.utils.notification.feignclient.model;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmsRequest {
  @NotEmpty private String participant;
  @NotEmpty private String message;
}
