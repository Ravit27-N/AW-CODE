package com.innovationandtrust.utils.gravitee.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class Resource {
  private String name;
  private String type;
  private Boolean enabled;
  private Configuration configuration;
}
