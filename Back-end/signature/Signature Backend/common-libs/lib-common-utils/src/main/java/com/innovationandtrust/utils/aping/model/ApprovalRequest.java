package com.innovationandtrust.utils.aping.model;

import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalRequest implements Serializable {
  private String actor;
  private List<String> documents;
  private String tag;
  private String otp;
}
