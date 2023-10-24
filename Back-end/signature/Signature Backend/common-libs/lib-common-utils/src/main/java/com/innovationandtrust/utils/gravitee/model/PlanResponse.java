package com.innovationandtrust.utils.gravitee.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PlanResponse {
  private String id;
  private String name;
  private String security;
}
