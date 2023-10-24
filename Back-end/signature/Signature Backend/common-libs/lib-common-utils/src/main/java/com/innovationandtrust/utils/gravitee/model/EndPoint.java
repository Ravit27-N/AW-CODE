package com.innovationandtrust.utils.gravitee.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class EndPoint {
  private Boolean backup;
  private Boolean inherit = false;
  private String name;
  private Long weight;
  private String type;
  private String target;
  private Http http;
}
