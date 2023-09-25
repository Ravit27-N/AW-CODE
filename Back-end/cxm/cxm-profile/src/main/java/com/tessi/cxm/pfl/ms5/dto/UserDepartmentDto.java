package com.tessi.cxm.pfl.ms5.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class UserDepartmentDto implements Serializable {
  private String firstName;
  private String lastName;
  private String userId;
  private String username;
  private String email;
  @JsonProperty("service")
  private DepartmentDto department;

}
