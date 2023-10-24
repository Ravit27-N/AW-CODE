package com.innovationandtrust.process.model;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OtpInfo implements Serializable {
  private boolean isExpired = true;
  private boolean isValidated = false;
  private int totalError = 0;
}
