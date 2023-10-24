package com.innovationandtrust.corporate.model.dto;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CompanyEmployee {
  private Long id;
  private String firstName;

  @Min(1)
  private Long userId;

  private Long businessUnitId;
  private Long companyId;
}
