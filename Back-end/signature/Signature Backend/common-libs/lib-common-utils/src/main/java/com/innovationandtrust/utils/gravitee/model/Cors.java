package com.innovationandtrust.utils.gravitee.model;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Cors {
  private Boolean enabled = true;
  private Boolean allowCredentials = false;
  private List<String> allowOrigin;
  private List<String> allowHeaders;
  private List<String> allowMethods;
  private List<String> exposeHeaders = new ArrayList<>();
  private int maxAge = -1;
}
