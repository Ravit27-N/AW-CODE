package com.innovationandtrust.share.model.corporateprofile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeResponseDto {
  private Long id;
  private Long userId;
  private BusinessUnitRes businessUnit;
  private UserAccessDTO userAccess;
}
