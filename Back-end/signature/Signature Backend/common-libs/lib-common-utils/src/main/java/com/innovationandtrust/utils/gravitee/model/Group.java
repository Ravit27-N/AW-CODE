package com.innovationandtrust.utils.gravitee.model;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class Group {
  private String name;
  private List<EndPoint> endpoints;
}
