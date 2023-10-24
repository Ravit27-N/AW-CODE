package com.innovationandtrust.share.model.corporateprofile;

import jakarta.validation.constraints.Min;
import java.io.Serializable;
import java.util.Date;
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
public class CompanyDetailDTO implements Serializable {
  Long id;
  private String companyUuid;
  @Min(1)
  private Long companyId;

  @Min(1)
  private Long userId;
  private String name;
  private Date creationDate;
  private String firstName;
  private String lastName;
  private String gender;
  private String address;
  private String filename;
}
