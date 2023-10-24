package com.innovationandtrust.profile.model.dto;

import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.StringUtils;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CompanyInfo implements Serializable {
  private long id;
  private String name;
  private String logo;
  private long totalEmployees;
  private boolean isArchiving;
  private Date createdAt;
  private Date modifiedAt;

  public String getLogo() {
    if (StringUtils.hasText(logo)) {
      return logo;
    }

    return "";
  }
}
