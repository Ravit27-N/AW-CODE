package com.innovationandtrust.share.model.corporateprofile;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Min;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmployeeDTO implements Serializable {
  private Long id;

  private String firstName;

  private String lastName;

  private String functional;

  @Min(1)
  private Long userId;

  private Long businessUnitId;

  private Long userAccessId;

  @Builder.Default private Long totalProjects = 0L;

  @Builder.Default private double percentage = 0D;

  private BusinessUnitRes department;

  private UserAccessDTO userAccess;

  private boolean deleted;

  private long loginCount;
}
