package com.innovationandtrust.utils.aping.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SessionUserData {
  private String uuid;
  private String name;
  private String email;
}
