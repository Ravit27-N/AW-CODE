package com.innovationandtrust.utils.gravitee.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationResponse {
  private List<ApplicationResponseList> data;
}
