package com.innovationandtrust.share.model.project;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GeneratedOTP implements Serializable {

  private String otp;
  private String date;
  private String expires;
  private boolean isValidated;
  private int errorValidation;

  public GeneratedOTP(String otp, String date, String expires, int errorValidation) {
    this.otp = otp;
    this.date = date;
    this.expires = expires;
    this.isValidated = false;
    this.errorValidation = errorValidation;
  }
}
