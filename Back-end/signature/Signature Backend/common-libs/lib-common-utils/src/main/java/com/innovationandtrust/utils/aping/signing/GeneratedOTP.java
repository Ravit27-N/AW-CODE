package com.innovationandtrust.utils.aping.signing;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class GeneratedOTP {
  private String otp;
  private String date;
  private String expires;
  private Actor actor;
}
