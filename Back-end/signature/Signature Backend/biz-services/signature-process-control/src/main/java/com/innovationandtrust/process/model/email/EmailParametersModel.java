package com.innovationandtrust.process.model.email;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class EmailParametersModel {
  private String firstName;
  private String projectName;
  private String message;
  private String subject;
  private String linkUrl;
  private String email;
}
